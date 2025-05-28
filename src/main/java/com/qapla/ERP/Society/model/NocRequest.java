

package com.qapla.ERP.Society.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NocRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentType;

    @Enumerated(EnumType.STRING)
    private Tower tower;

    private String flatNumber;

    private String bankName;

    private String owners;

    private String status; // Submitted / Withdrawn
}
