package com.qapla.ERP.Society.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Document name is required")
    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;  // Default value

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // Custom constructor without ID for creation
    public Document(String documentName, String documentType, Long memberId) {
        this.documentName = documentName;
        this.documentType = documentType;
        this.memberId = memberId;
        this.isActive = true; // Default active status
    }

    // Alternative constructor with active status
    public Document(String documentName, String documentType, Boolean isActive, Long memberId) {
        this.documentName = documentName;
        this.documentType = documentType;
        this.isActive = isActive != null ? isActive : true;
        this.memberId = memberId;
    }
}

