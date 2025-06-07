package com.app.playbooker.service;

import com.app.playbooker.entity.Booking;
import com.app.playbooker.entity.BookingInvoice;
import com.app.playbooker.entity.PlaySpace;
import com.app.playbooker.entity.User;
import com.app.playbooker.exceptions.InvoiceNotFoundException;
import com.app.playbooker.repository.BookingInvoiceRepository;
import com.app.playbooker.utils.InvoicePdfGenerator;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.twilio.twiml.voice.Play;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingInvoiceService {

    @Autowired
    private BookingInvoiceRepository bookingInvoiceRepository;

    @Autowired
    private InvoiceNumberService invoiceNumberService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PlaySpaceService playSpaceService;

    @Autowired
    private InvoicePdfGenerator invoicePdfGenerator;

    public BookingInvoice generateInvoice(Booking booking) {
        String invoiceNumber = invoiceNumberService.generateInvoiceNumber();

        BookingInvoice bookingInvoice = new BookingInvoice();
        User user = customUserDetailsService.getUserById(booking.getUserId());
        PlaySpace playSpace = playSpaceService.getPlaySpaceObjectById(booking.getPlaySpaceId());

        bookingInvoice.setInvoiceNumber(invoiceNumber);
        bookingInvoice.setGeneratedAt(LocalDateTime.now());
        bookingInvoice.setBookingId(booking.getId());
        bookingInvoice.setSent(false);
        bookingInvoice.setAmount(booking.getTotalPrice());
        bookingInvoice.setUserName(user.getUsername());
        bookingInvoice.setPlaySpaceName(playSpace.getName());
        bookingInvoice.setPlaySpaceId(booking.getPlaySpaceId());

        return bookingInvoiceRepository.save(bookingInvoice);
    }

    public ByteArrayOutputStream getInvoiceMetaData(String bookingId) {
        BookingInvoice invoice = getInvoiceByBookingId(bookingId);
        User user = customUserDetailsService.getUserByUsername(invoice.getUserName());
        PlaySpace playSpace = playSpaceService.getPlaySpaceObjectById(invoice.getPlaySpaceId());

        return invoicePdfGenerator.generateInvoicePdf(invoice, bookingId,
                user, playSpace);
    }

    public BookingInvoice getInvoiceByBookingId(String id) {
        return bookingInvoiceRepository.findByBookingId(id).orElseThrow(() -> new InvoiceNotFoundException("Invoice not found for this booking id: " + id));
    }
}
