package com.app.playbooker.controller;

import com.app.playbooker.dto.BookingDTO;
import com.app.playbooker.dto.BookingResponse;
import com.app.playbooker.dto.PaginationData;
import com.app.playbooker.dto.ResultPageData;
import com.app.playbooker.service.BookingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Map;

import static com.app.playbooker.utils.AppConstants.ROLE_ADMIN;
import static com.app.playbooker.utils.AppConstants.ROLE_USER;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/v1/booking")
@Log4j2
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Secured({ROLE_ADMIN, ROLE_USER})
    @PostMapping("/create")
    public ResponseEntity<BookingResponse> bookPlaySpace(@RequestBody BookingDTO bookingDTO) {
        return new ResponseEntity<>(bookingService.createBooking(bookingDTO), HttpStatusCode.valueOf(201));
    }

    @Secured({ROLE_ADMIN, ROLE_USER})
    @GetMapping("/getAll/user={userId}")
    public ResponseEntity<ResultPageData<BookingResponse>> getAllBooking(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BookingResponse> bookingResponsePage = bookingService.getAllBookingByUserId(userId, page, size);
        PaginationData paginationData = PaginationData.builder()
                .currentPage(bookingResponsePage.getNumber())
                .totalCount(bookingResponsePage.getTotalElements())
                .totalPages(bookingResponsePage.getTotalPages())
                .count(bookingResponsePage.getNumberOfElements())
                .build();

        ResultPageData<BookingResponse> bookingResponseResultPageData = new ResultPageData<>();
        bookingResponseResultPageData.setPaginationData(paginationData);
        bookingResponseResultPageData.setResults(bookingResponsePage.getContent());

        return ResponseEntity.ok(bookingResponseResultPageData);
    }

    @Secured({ROLE_ADMIN, ROLE_USER})
    @GetMapping("/get/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @Secured({ROLE_ADMIN})
    @GetMapping("/getAll/playSpace={playSpaceId}")
    public ResponseEntity<ResultPageData<BookingResponse>> getAllBookingsByPlaySpaceId(
            @PathVariable String playSpaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BookingResponse> bookingResponsePage = bookingService.getAllBookingsByPlaySpaceId(playSpaceId, page, size);

        PaginationData paginationData = PaginationData.builder()
                .currentPage(bookingResponsePage.getNumber())
                .totalCount(bookingResponsePage.getTotalElements())
                .totalPages(bookingResponsePage.getTotalPages())
                .count(bookingResponsePage.getNumberOfElements())
                .build();

        ResultPageData<BookingResponse> bookingResponseResultPageData = new ResultPageData<>();
        bookingResponseResultPageData.setPaginationData(paginationData);
        bookingResponseResultPageData.setResults(bookingResponsePage.getContent());

        return ResponseEntity.ok(bookingResponseResultPageData);
    }

    @Secured({ROLE_ADMIN})
    @GetMapping("/getAll")
    public ResponseEntity<ResultPageData<BookingResponse>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BookingResponse> bookingResponsePage = bookingService.getAllBookings(page, size);

        PaginationData paginationData = PaginationData.builder()
                .currentPage(bookingResponsePage.getNumber())
                .totalCount(bookingResponsePage.getTotalElements())
                .totalPages(bookingResponsePage.getTotalPages())
                .count(bookingResponsePage.getNumberOfElements())
                .build();

        ResultPageData<BookingResponse> bookingResponseResultPageData = new ResultPageData<>();
        bookingResponseResultPageData.setPaginationData(paginationData);
        bookingResponseResultPageData.setResults(bookingResponsePage.getContent());

        return ResponseEntity.ok(bookingResponseResultPageData);
    }

    @Secured({ROLE_ADMIN, ROLE_USER})
    @PostMapping("/payment-callback")
    public ResponseEntity<Void> paymentCallback(@RequestParam Map<String, Object> callbackRequest) {
        log.info("####### Started controller method ###########");
        bookingService.updateBookingForCallback(callbackRequest);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:3000/payment-success"))
                .build();
    }

    @Secured({ROLE_ADMIN, ROLE_USER})
    @PatchMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable String id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @Secured({ROLE_ADMIN})
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable String id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
