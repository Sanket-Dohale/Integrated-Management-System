// com/qapla/ERP/Society/model/Tender.java
package com.qapla.ERP.Society.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "tenders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Opening date is required")
    @FutureOrPresent(message = "Opening date must be today or in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate openingDate;

    @NotNull(message = "Closing date is required")
    @Future(message = "Closing date must be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate closingDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = false;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "member_id")
//    @JoinColumn(name = "created_by", nullable = false)
    private Member createdBy;

//    @Column(nullable = false)
    @Column(name = "created_at")
    private LocalDate createdAt;

    // For file attachments
    private String documentPath;
}