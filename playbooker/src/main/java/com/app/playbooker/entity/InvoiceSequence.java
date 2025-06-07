package com.app.playbooker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InvoiceSequence {
    @Id
    private int year;
    private int lastNumber;
}
