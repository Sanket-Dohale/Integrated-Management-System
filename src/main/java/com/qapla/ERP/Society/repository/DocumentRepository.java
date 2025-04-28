package com.qapla.ERP.Society.repository;


//import com.example.document_manager.model.Document;
import com.qapla.ERP.Society.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByIsActive(Boolean isActive);

    List<Document> findByMemberId(Long memberId);

    List<Document> findByDocumentType(String documentType);
}
