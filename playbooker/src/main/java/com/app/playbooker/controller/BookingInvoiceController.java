package com.app.playbooker.controller;

import com.app.playbooker.entity.BookingInvoice;
import com.app.playbooker.repository.BookingInvoiceRepository;
import com.app.playbooker.service.BookingInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invoice")
public class BookingInvoiceController {

    @Autowired
    private BookingInvoiceService bookingInvoiceService;

    @GetMapping("/download/{bookingId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable String bookingId) {
        BookingInvoice bookingInvoice = bookingInvoiceService.getInvoiceByBookingId(bookingId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(bookingInvoice.getInvoiceNumber() + ".pdf")
                .build());

        return new ResponseEntity<>(bookingInvoiceService.getInvoiceMetaData(bookingId).toByteArray(), headers, HttpStatus.OK);
    }

}
