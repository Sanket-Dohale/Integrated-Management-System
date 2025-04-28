package com.qapla.ERP.Society.service;


//import com.example.document_manager.model.Document;
//import com.example.document_manager.repository.DocumentRepository;
import com.qapla.ERP.Society.model.Document;
import com.qapla.ERP.Society.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    private DocumentRepository documentRepository;

    public void saveDocument(Document document) {
        try {
            documentRepository.save(document);
            logger.info("Document saved successfully: {}", document);
        } catch (Exception e) {
            logger.error("Error saving document: {}", e.getMessage());
            throw e;
        }
    }

    public List<Document> getAllDocuments() {
        try {
            List<Document> documents = documentRepository.findAll();
            logger.info("Retrieved {} documents", documents.size());
            return documents;
        } catch (Exception e) {
            logger.error("Error fetching documents: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void deleteDocument(Long id) {
        try {
            documentRepository.deleteById(id);
            logger.info("Document deleted successfully. ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting document: {}", e.getMessage());
            throw e;
        }
    }
}
