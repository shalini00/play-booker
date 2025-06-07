package com.app.playbooker.exceptions;

public class BookingNotFoundException extends BookingException {
    public BookingNotFoundException(String id) {
        super("Booking not found with ID: " + id);
    }
}
