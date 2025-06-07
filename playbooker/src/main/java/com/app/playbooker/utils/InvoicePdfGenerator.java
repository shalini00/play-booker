package com.app.playbooker.utils;

import com.app.playbooker.entity.Booking;
import com.app.playbooker.entity.BookingInvoice;
import com.app.playbooker.entity.PlaySpace;
import com.app.playbooker.entity.User;
import com.app.playbooker.exceptions.BookingNotFoundException;
import com.app.playbooker.repository.BookingRepository;
import com.app.playbooker.service.BookingService;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvoicePdfGenerator {

    @Autowired
    private BookingRepository bookingRepository;

    public ByteArrayOutputStream generateInvoicePdf(BookingInvoice invoice, String bookingId, User user, PlaySpace playSpace) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("INVOICE").setBold().setFontSize(16));
            document.add(new Paragraph("Invoice Number: " + invoice.getInvoiceNumber()));
            document.add(new Paragraph("Date: " + invoice.getGeneratedAt()));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Customer: " + user.getName()));
            document.add(new Paragraph("Email: " + user.getEmail()));
            document.add(new Paragraph("PlaySpace: " + playSpace.getName()));
            document.add(new Paragraph("Booking Time: " + booking.getStartTime() + " to " + booking.getEndTime()));
            document.add(new Paragraph("Amount Paid: ₹" + booking.getTotalPrice()));
            document.add(new Paragraph("Payment ID: " + booking.getPaymentId()));

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF invoice: " + e.getMessage(), e);
        }

        return outputStream;
    }
}


