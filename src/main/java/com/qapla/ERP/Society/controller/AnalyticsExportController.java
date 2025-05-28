package com.qapla.ERP.Society.controller;

import com.qapla.ERP.Society.model.Visitor;
import com.qapla.ERP.Society.repository.VisitorRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/analytics/export")
public class AnalyticsExportController {

    @Autowired
    private VisitorRepository visitorRepository;

    @GetMapping("/excel")
    public void exportExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=visitor_report.xlsx");

        List<Visitor> visitors = visitorRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Visitors");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Resident Email");
        header.createCell(1).setCellValue("Visitor Name");
        header.createCell(2).setCellValue("Contact");
        header.createCell(3).setCellValue("Visit DateTime");
        header.createCell(4).setCellValue("Checked In");
        header.createCell(5).setCellValue("Check In Time");

        int rowNum = 1;
        for (Visitor v : visitors) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(v.getResidentEmail());
            row.createCell(1).setCellValue(v.getName());
            row.createCell(2).setCellValue(v.getContact());
            row.createCell(3).setCellValue(v.getVisitDateTime().toString());
            row.createCell(4).setCellValue(v.isCheckedIn());
            row.createCell(5).setCellValue(v.getCheckInTime() != null ? v.getCheckInTime().toString() : "");
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/pdf")
    public void exportPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=visitor_report.pdf");

        List<Visitor> visitors = visitorRepository.findAll();

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Visitor Report").setBold().setFontSize(14).setMarginBottom(10));

        // Define column widths
        float[] columnWidths = {150f, 120f, 100f, 160f, 80f, 160f};
        com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(columnWidths);

        // Table headers
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Resident Email")));
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Visitor Name")));
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Contact")));
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Visit DateTime")));
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Checked In")));
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Check In Time")));

        // Table rows
        for (Visitor v : visitors) {
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(v.getResidentEmail())));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(v.getName())));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(v.getContact())));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(v.getVisitDateTime().toString())));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(v.isCheckedIn()))));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(
                    v.getCheckInTime() != null ? v.getCheckInTime().toString() : ""
            )));
        }

        doc.add(table);
        doc.close();
    }
}
