package com.app.playbooker.controller;

import com.app.playbooker.dto.BookingDTO;
import com.app.playbooker.dto.BookingResponse;
import com.app.playbooker.entity.Booking;
import com.app.playbooker.service.BookingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/v1/booking")
@Log4j2
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<BookingResponse> bookPlaySpace(@RequestBody BookingDTO bookingDTO) {
        return new ResponseEntity<>(bookingService.createBooking(bookingDTO), HttpStatusCode.valueOf(201));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Booking>> getAllBooking() {
        return ResponseEntity.ok(bookingService.getAllBooking());
    }

    @PostMapping("/payment-callback")
    public ResponseEntity<Void> paymentCallback(@RequestParam Map<String, Object> callbackRequest) {
        log.info("####### Started controller method ###########");
        bookingService.updateBookingForCallback(callbackRequest);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:3000/payment-success"))
                .build();
    }
}
