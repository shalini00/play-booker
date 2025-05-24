package com.app.playbooker.service;

import com.app.playbooker.dto.BookingDTO;
import com.app.playbooker.dto.BookingResponse;
import com.app.playbooker.dto.PaymentResponseDTO;
import com.app.playbooker.entity.AvailabilitySlot;
import com.app.playbooker.entity.Booking;
import com.app.playbooker.entity.PlaySpace;
import com.app.playbooker.entity.User;
import com.app.playbooker.enums.BookingStatus;
import com.app.playbooker.enums.PaymentStatus;
import com.app.playbooker.integrations.payment.RazorpayPaymentIntegration;
import com.app.playbooker.repository.BookingRepository;
import com.app.playbooker.repository.PlaySpaceRepository;
import com.app.playbooker.repository.UserRepository;
import com.app.playbooker.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
public class BookingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaySpaceRepository playSpaceRepository;

    @Autowired
    private BookingRepository bookingRepository;

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
            String email = getEmailFromToken();
            User user = userRepository.findByEmail(email);
            PlaySpace playSpace = playSpaceRepository.findById(playSpaceId).get();

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
            booking.setCreatedAt(LocalDateTime.now());
            bookingRepository.save(booking);
            updateAvailabilitySlot(playSpace, bookingDTO);

            return getBookingResponse(booking, email);

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while booking play space due to - " + e.getMessage());
        }
    }

    public BookingResponse updateBookingForCallback(Map<String, Object> callbackRequest) {
        log.info("Callback Request is ::: {}", callbackRequest);
        String paymentId = callbackRequest.getOrDefault("razorpay_order_id", "").toString();
        Booking booking = bookingRepository.findByPaymentId(paymentId);
        booking.setPaymentStatus(PaymentStatus.SUCCESS);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        User user = userRepository.findById(booking.getUserId()).get();

        return getBookingResponse(booking, user.getEmail());
    }

    private void updateAvailabilitySlot(PlaySpace playSpace, BookingDTO bookingDTO) {
        List<AvailabilitySlot> allSlots = playSpace.getAvailabilitySlots();
        Optional<AvailabilitySlot> slotOptional = getAvailabilitySlotByStartAndEndTime(allSlots, bookingDTO.getStartTime(), bookingDTO.getEndTime());
        if(slotOptional.isPresent()) {
            AvailabilitySlot slot = slotOptional.get();
            allSlots.remove(slot);
            slot.setBooked(true);
            allSlots.add(slot);
            playSpace.setAvailabilitySlots(allSlots);
            playSpaceRepository.save(playSpace);
        }
    }

    private boolean isSlotAvailable(PlaySpace playSpace, BookingDTO bookingDTO) {
        List<AvailabilitySlot> availabilitySlots = playSpace.getAvailabilitySlots();
        LocalDateTime start = bookingDTO.getStartTime();
        LocalDateTime end = bookingDTO.getEndTime();
        Optional<AvailabilitySlot> slot = getAvailabilitySlotByStartAndEndTime(availabilitySlots, start, end);

        return slot.isPresent() && !slot.get().isBooked();
    }

    public Optional<AvailabilitySlot> getAvailabilitySlotByStartAndEndTime(List<AvailabilitySlot> availabilitySlots, LocalDateTime start, LocalDateTime end) {
        return availabilitySlots.stream()
                .filter(availabilitySlot -> availabilitySlot.getStartTime().equals(start) && availabilitySlot.getEndTime().equals(end))
                .findFirst();
    }

    private String getEmailFromToken() {
        Claims claims = jwtUtil.getJWTTokenClaims(httpServletRequest);
        // typically the subject is the username or email
        System.out.println(claims.getSubject());
        return claims.getSubject();
    }

    public List<Booking> getAllBooking() {
        String email = getEmailFromToken();
        User user = userRepository.findByEmail(email);
        return bookingRepository.findAllByUserId(user.getId());
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
                .totalPrice(booking.getTotalPrice())
                .paymentId(booking.getPaymentId())
                .paymentStatus(booking.getPaymentStatus())
                .paymentReceiptId(booking.getPaymentReceiptId())
                .bookingStatus(booking.getBookingStatus())
                .build();
    }
}
