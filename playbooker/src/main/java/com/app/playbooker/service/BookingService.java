package com.app.playbooker.service;

import com.app.playbooker.dto.BookingDTO;
import com.app.playbooker.dto.BookingResponse;
import com.app.playbooker.dto.PaymentResponseDTO;
import com.app.playbooker.dto.SlotInfoDTO;
import com.app.playbooker.entity.Booking;
import com.app.playbooker.entity.BookingInvoice;
import com.app.playbooker.entity.PlaySpace;
import com.app.playbooker.entity.User;
import com.app.playbooker.enums.BookingStatus;
import com.app.playbooker.enums.PaymentStatus;
import com.app.playbooker.exceptions.BookingException;
import com.app.playbooker.exceptions.BookingNotFoundException;
import com.app.playbooker.integrations.payment.RazorpayPaymentIntegration;
import com.app.playbooker.notification.NotificationFactory;
import com.app.playbooker.repository.BookingInvoiceRepository;
import com.app.playbooker.repository.BookingRepository;
import com.app.playbooker.utils.InvoicePdfGenerator;
import com.app.playbooker.utils.JwtUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class BookingService {

    public static final String EMAIL = "email";
    public static final String WHATSAPP = "whatsapp";

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PlaySpaceService playSpaceService;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private BookingInvoiceService bookingInvoiceService;

    @Autowired
    private BookingInvoiceRepository bookingInvoiceRepository;

    @Autowired
    private NotificationFactory notificationFactory;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RazorpayPaymentIntegration razorpayPaymentIntegration;

    public BookingResponse createBooking(BookingDTO bookingDTO) {
        try {
            LocalDate bookingDate = bookingDTO.getBookingDate();
            LocalDate today = LocalDate.now();
            if (bookingDate.isBefore(today)) {
                throw new RuntimeException("Booking date cannot be in the past.");
            }

            String playSpaceId = bookingDTO.getPlaySpaceId();
            String email = jwtUtil.getEmailFromToken(httpServletRequest);
            User user = customUserDetailsService.getUserByEmail(email);
            PlaySpace playSpace = playSpaceService.getPlaySpaceObjectById(playSpaceId);

            boolean isAvailable = isSlotAvailable(playSpace, bookingDTO);

            if (!isAvailable) {
                throw new RuntimeException("Selected time slot is not available.");
            }

            double totalPrice = playSpace.getPricePerHour() *
                    (bookingDTO.getEndTime().getHour() - bookingDTO.getStartTime().getHour());

            Booking booking = Booking.builder()
                    .userId(user.getId())
                    .playSpaceId(playSpaceId)
                    .bookingDate(bookingDTO.getBookingDate())
                    .startTime(bookingDTO.getStartTime())
                    .endTime(bookingDTO.getEndTime())
                    .totalPrice(totalPrice)
                    .build();

            PaymentResponseDTO paymentResponseDTO = razorpayPaymentIntegration.createPayment(booking, email);
            booking.setPaymentId(paymentResponseDTO.getId());
            booking.setPaymentReceiptId(paymentResponseDTO.getReceipt());
            if(paymentResponseDTO.getStatus().equalsIgnoreCase("created")) {
                booking.setPaymentStatus(PaymentStatus.PENDING);
                booking.setBookingStatus(BookingStatus.PENDING);
            } else {
                booking.setPaymentStatus(PaymentStatus.FAILED);
                booking.setBookingStatus(BookingStatus.FAILED);
                booking.setBookingFailedReason("Booking failed due to payment failure.");
            }
            bookingRepository.save(booking);

            return getBookingResponse(booking, email);

        } catch (Exception e) {
            throw new BookingException("Error occurred while booking play space due to - " + e.getMessage());
        }
    }

    public BookingResponse updateBookingForCallback(Map<String, Object> callbackRequest) {
        try {
            String paymentId = callbackRequest.getOrDefault("razorpay_order_id", "").toString();
            Booking booking = bookingRepository.findByPaymentId(paymentId);
            booking.setPaymentStatus(PaymentStatus.SUCCESS);
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            User user = customUserDetailsService.getUserById(booking.getUserId());
            PlaySpace playSpace = playSpaceService.getPlaySpaceObjectById(booking.getPlaySpaceId());
            BookingInvoice bookingInvoice = bookingInvoiceService.generateInvoice(booking);
            ByteArrayOutputStream pdfStream = bookingInvoiceService.getInvoiceMetaData(booking.getId());

            notificationFactory.getNotificationServiceByType(EMAIL)
                    .sendBookingConfirmation(user, playSpace, booking, pdfStream);

            if (StringUtils.hasText(user.getPhoneNumber()) && user.isPhoneNumberVerified()) {
                notificationFactory.getNotificationServiceByType(WHATSAPP)
                        .sendBookingConfirmation(user, playSpace, booking, pdfStream);
            }
            bookingInvoice.setSent(true);
            bookingInvoiceRepository.save(bookingInvoice);


            return getBookingResponse(booking, user.getEmail());
        } catch (Exception e) {
            throw new BookingException("Error occurred during payment due to " + e.getMessage());
        }
    }

    private boolean isSlotAvailable(PlaySpace playSpace, BookingDTO bookingDTO) {
        LocalDateTime startTime = bookingDTO.getStartTime();
        LocalDateTime endTime = bookingDTO.getEndTime();
        LocalDate bookingDate = bookingDTO.getBookingDate();
        String playSpaceId = playSpace.getId();

        // Get availability info for the given date
        List<SlotInfoDTO> availability = availabilityService.getSlotInfo(playSpaceId, bookingDate);

        // Calculate total number of 30-minute slots needed
        long minutes = Duration.between(startTime, endTime).toMinutes();
        int slotCount = (int) (minutes / 30);

        // Start checking availability for each half-hour slot
        LocalDateTime temp = startTime;

        for (int i = 0; i < slotCount; i++) {
            LocalTime slotTime = temp.toLocalTime();

            // Find matching slot from availability list
            boolean foundAvailable = availability.stream()
                    .anyMatch(slot -> slot.getTime().equals(slotTime) && slot.isAvailable());

            if (!foundAvailable) {
                return false; // If any required slot is unavailable, return false
            }

            temp = temp.plusMinutes(30);
        }

        return true; // All slots are available
    }

    public Page<BookingResponse> getAllBookingByUserId(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        String email = jwtUtil.getEmailFromToken(httpServletRequest);
        User user = customUserDetailsService.getUserByEmail(email);
        // Throw error if user is not the current user and also not the admin i.e. user is trying to get the data of another user
        if (!userId.equals("current") && !customUserDetailsService.isAdminUser(user)) {
            throw new AccessDeniedException("User does not have required permission");
        }

        String finalUserId = userId.equals("current") ? user.getId() : userId;
        Page<Booking> bookings = bookingRepository.findAllByUserId(finalUserId, pageable);
        return bookings.map(this::getBookingResponseFromBooking);
    }

    public BookingResponse getBookingResponse(Booking booking, String email) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .email(email)
                .playSpaceId(booking.getPlaySpaceId())
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .createdAt(booking.getCreatedAt())
                .createdBy(booking.getCreatedBy())
                .updatedAt(booking.getUpdatedAt())
                .updatedBy(booking.getUpdatedBy())
                .totalPrice(booking.getTotalPrice())
                .paymentId(booking.getPaymentId())
                .paymentStatus(booking.getPaymentStatus())
                .paymentReceiptId(booking.getPaymentReceiptId())
                .bookingStatus(booking.getBookingStatus())
                .build();
    }

    public void cancelBooking(String id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException(id));
        checkAccess(booking.getUserId());

        booking.setBookingStatus(BookingStatus.CANCELLED);
        User user = customUserDetailsService.getUserById(booking.getUserId());
        PlaySpace playSpace = playSpaceService.getPlaySpaceObjectById(booking.getPlaySpaceId());
        bookingRepository.save(booking);
        notificationFactory.getNotificationServiceByType(EMAIL)
                .sendBookingCancellation(user, playSpace, booking);

        if (StringUtils.hasText(user.getPhoneNumber()) && user.isPhoneNumberVerified()) {
            notificationFactory.getNotificationServiceByType(WHATSAPP)
                    .sendBookingCancellation(user, playSpace, booking);
        }
    }

    public BookingResponse getBookingResponseById(String id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException(id));
        checkAccess(booking.getUserId());
        return getBookingResponseFromBooking(booking);
    }

    public Page<BookingResponse> getAllBookingsByPlaySpaceId(String playSpaceId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Booking> bookings = bookingRepository.findAllByPlaySpaceId(playSpaceId, pageable);
        return bookings.map(this::getBookingResponseFromBooking);
    }

    public BookingResponse getBookingResponseFromBooking(Booking booking) {
        BookingResponse bookingResponse = new BookingResponse();
        BeanUtils.copyProperties(booking, bookingResponse);
        bookingResponse.setEmail(jwtUtil.getEmailFromToken(httpServletRequest));

        return bookingResponse;
    }

    public Page<BookingResponse> getAllBookings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Booking> bookings = bookingRepository.findAll(pageable);
        return bookings.map(this::getBookingResponseFromBooking);
    }

    private void checkAccess(String userId) {
        String email = jwtUtil.getEmailFromToken(httpServletRequest);
        User loggedInUser = customUserDetailsService.getUserByEmail(email);
        if (!userId.equals(loggedInUser.getId()) && !customUserDetailsService.isAdminUser(loggedInUser)) {
            throw new AccessDeniedException("User is not allowed to cancel other's bookings.");
        }
    }

    @Transactional
    public void deleteBooking(String id) {
        bookingRepository.deleteById(id);
    }

    public long getTotalBookings() {
        return bookingRepository.countByBookingStatus(BookingStatus.CONFIRMED);
    }

    public BigDecimal getTotalRevenue() {
        return bookingRepository.sumAmountByPaymentStatus(PaymentStatus.SUCCESS);
    }

    public Booking getBookingById(String id) {
        return bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException(id));
    }
}
