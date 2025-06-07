package com.app.playbooker.service;

import com.app.playbooker.entity.BookingInvoice;
import com.app.playbooker.entity.InvoiceSequence;
import com.app.playbooker.repository.InvoiceSequenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
public class InvoiceNumberService {

    @Autowired
    private InvoiceSequenceRepository invoiceSequenceRepository;

    @Transactional
    public String generateInvoiceNumber() {
        int currentYear = Year.now().getValue();

        InvoiceSequence invoiceSequence = invoiceSequenceRepository.findById(currentYear)
                .orElseGet(() -> {
                    InvoiceSequence newSeq = new InvoiceSequence();
                    newSeq.setYear(currentYear);
                    newSeq.setLastNumber(0);
                    return newSeq;
                });

        int nextNumber = invoiceSequence.getLastNumber() + 1;
        invoiceSequence.setLastNumber(nextNumber);
        invoiceSequenceRepository.save(invoiceSequence);

        // Format as: INV-2024-001
        return String.format("INV-%d-%03d", currentYear, nextNumber);
    }
}
