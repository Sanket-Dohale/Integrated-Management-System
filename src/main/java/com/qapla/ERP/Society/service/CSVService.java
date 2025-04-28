package com.qapla.ERP.Society.service;


//import com.example.document_manager.model.Document;
import com.qapla.ERP.Society.model.Document;
import org.apache.commons.csv.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class CSVService {

    private static final String TYPE = "text/csv";

    public String processCSV(MultipartFile file) {
        List<String> errorLogs = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            List<Document> documents = parseCSV(inputStream, errorLogs);
            // Save documents and error logs (implementation depends on your setup)
            Files.write(Paths.get("error.log"), errorLogs);
            return "File processed successfully! Records: " + documents.size();
        } catch (Exception e) {
            return "Failed to process file: " + e.getMessage();
        }
    }

    public byte[] exportDocuments(List<Document> documents) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(
                     new OutputStreamWriter(out),
                     CSVFormat.DEFAULT.withHeader(
                             "ID", "Document Name", "Document Type", "Is Active", "Member ID"
                     ))) {

            for (Document doc : documents) {
                csvPrinter.printRecord(
                        doc.getId(),
                        doc.getDocumentName(),
                        doc.getDocumentType(),
                        doc.getIsActive(),
                        doc.getMemberId()
                );
            }

            csvPrinter.flush();
            return out.toByteArray();
        }
    }

    private List<Document> parseCSV(InputStream inputStream, List<String> errorLogs) throws IOException {
        List<Document> documents = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord record : csvParser) {
                try {
                    Document doc = new Document(
                            record.get("Document Name"),
                            record.get("Document Type"),
                            Boolean.parseBoolean(record.get("Is Active")),
                            Long.parseLong(record.get("Member ID"))
                    );
                    documents.add(doc);
                } catch (Exception e) {
                    errorLogs.add("Error processing record: " + record + " - " + e.getMessage());
                }
            }
        }
        return documents;
    }
}
