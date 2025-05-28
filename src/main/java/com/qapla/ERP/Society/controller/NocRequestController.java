
package com.qapla.ERP.Society.controller;


import com.qapla.ERP.Society.model.NocRequest;
import com.qapla.ERP.Society.model.Tower;
import com.qapla.ERP.Society.repository.NocRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import com.itextpdf.text.Image;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Controller
@RequestMapping("/noc")
public class NocRequestController {

    @Autowired
    private NocRequestRepository nocRequestRepository;

    @GetMapping("/request")
    public String showNocForm(Model model) {
        model.addAttribute("nocRequest", new NocRequest());
        model.addAttribute("towers", Tower.values());
        return "noc_request_form";
    }

    @PostMapping("/submit")
    public String submit(@ModelAttribute NocRequest nocRequest) {
        nocRequest.setDocumentType("Home Loan NOC");
        nocRequest.setStatus("Submitted");
        nocRequestRepository.save(nocRequest);
        return "redirect:/noc/all?success";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam("id") Long id) {
        NocRequest req = nocRequestRepository.findById(id).orElse(null);
        if (req != null && "Submitted".equals(req.getStatus())) {
            req.setStatus("Withdrawn");
            nocRequestRepository.save(req);
        }
        return "redirect:/noc/all?withdrawn";
    }

    @GetMapping("/all")
    public String viewAllNocRequests(Model model) {
        model.addAttribute("nocRequests", nocRequestRepository.findAll());
        return "noc_request_list";
    }

    @PostMapping("/updateStatus")
    @ResponseBody
    public String updateStatus(@RequestParam Long id, @RequestParam String status) {
        NocRequest req = nocRequestRepository.findById(id).orElse(null);
        if (req != null) {
            req.setStatus(status);
            nocRequestRepository.save(req);
            return "success";
        }
        return "fail";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadNocPdf(@PathVariable Long id) throws Exception {
        NocRequest req = nocRequestRepository.findById(id).orElse(null);
        if (req == null || !"Approved".equals(req.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        // Load logo image from URL
        String logoUrl = "https://img.freepik.com/premium-vector/unique-vector-design_517312-42844.jpg?w=826";
        URL url = new URL(logoUrl);
        Image logo = Image.getInstance(url);
        logo.scaleToFit(100, 100);
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);

        // Add title and subtitle
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        Paragraph title = new Paragraph("NO OBJECTION CERTIFICATE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        Paragraph subtitle = new Paragraph("TO WHOMSOEVER IT MAY CONCERN", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);
        document.add(Chunk.NEWLINE);

        // Get current date for "Issued Date"
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String formattedDate = currentDate.format(formatter);

        // Build body paragraph with bold fields
        Paragraph bodyParagraph = new Paragraph();
        bodyParagraph.setFont(bodyFont);
        bodyParagraph.add("This is to certify that Mr./Mrs. ");
        bodyParagraph.add(new Chunk(req.getOwners(), boldFont));
        bodyParagraph.add(", residing at Tower ");
        bodyParagraph.add(new Chunk(String.valueOf(req.getTower()), boldFont));
        bodyParagraph.add(", Flat No. ");
        bodyParagraph.add(new Chunk(req.getFlatNumber(), boldFont));
        bodyParagraph.add(",\n");
        bodyParagraph.add("is a bona fide member and resident of our housing society.\n\n");
        bodyParagraph.add("This No Objection Certificate (NOC) is being issued upon the member's request for submission to\n");
        bodyParagraph.add(new Chunk(req.getBankName(), boldFont));
        bodyParagraph.add(" in connection with their home loan application.\n\n");
        bodyParagraph.add("The Society hereby confirms that it has no objection to the member availing a home loan from the aforementioned bank.\n");
        bodyParagraph.add("This certificate is issued after due verification and subject to the condition that there are no outstanding dues\n");
        bodyParagraph.add("or compliance issues pending against the said member.\n\n");
        bodyParagraph.add("This NOC is issued without any liability on the part of the society and is intended solely for the purpose stated above.\n");

        document.add(bodyParagraph);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        document.add(new Paragraph("For and on behalf of the Managing Committee,", bodyFont));
        document.add(new Paragraph("Qapla Society", bodyFont));
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("_________________________", bodyFont));
        document.add(new Paragraph("Authorized Signatory", bodyFont));
        document.add(new Paragraph("Designation: Secretary / Chairman", bodyFont));

        // Issued Date with bold date value
        Paragraph issuedDate = new Paragraph();
        issuedDate.add(new Chunk("Issued Date: ", bodyFont));
        issuedDate.add(new Chunk(formattedDate, boldFont));
        document.add(issuedDate);

        document.close();

        byte[] pdfBytes = out.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("HomeLoan_NOC_" + req.getId() + ".pdf")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }


}




