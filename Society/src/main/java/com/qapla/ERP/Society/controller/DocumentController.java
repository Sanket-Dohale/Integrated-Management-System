package com.qapla.ERP.Society.controller;

import com.qapla.ERP.Society.model.Document;
import com.qapla.ERP.Society.service.CSVService;
import com.qapla.ERP.Society.service.DocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.ui.Model;

//import com.example.document_manager.model.Document;
//import com.example.document_manager.service.DocumentService;
//import com.example.document_manager.service.CSVService;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CSVService csvService;

    @GetMapping("")
    public String redirectToDocumentsList() {
        return "redirect:/documents/list";
    }

    @GetMapping("/add")
    public String showAddDocumentPage(Model model) {
        model.addAttribute("document", new Document());
        return "add-document";
    }

    @PostMapping("/save")
    public String saveDocument(@ModelAttribute Document document) {
        documentService.saveDocument(document);
        return "redirect:/documents/list";
    }

    @GetMapping("/list")
    public String viewDocuments(Model model) {
        List<Document> documents = documentService.getAllDocuments();
        model.addAttribute("documents", documents);
        return "member-document";
    }

    @PostMapping("/delete")
    public String deleteDocument(@RequestParam Long id) {
        documentService.deleteDocument(id);
        return "redirect:/documents/list";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportToCSV() throws IOException {
        List<Document> documents = documentService.getAllDocuments();
        byte[] csvData = csvService.exportDocuments(documents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("documents_export.csv")
                        .build());

        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }
}