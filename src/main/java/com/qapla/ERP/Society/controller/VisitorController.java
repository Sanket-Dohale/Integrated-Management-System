

package com.qapla.ERP.Society.controller;

import com.google.zxing.WriterException;
import com.qapla.ERP.Society.model.Visitor;
import com.qapla.ERP.Society.repository.VisitorRepository;
import com.qapla.ERP.Society.service.VisitorMailService;
import com.qapla.ERP.Society.util.QRCodeGenerator;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class VisitorController {

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private VisitorMailService mailService;

    @GetMapping("/pre-approve")
    public String showForm(Model model) {
        model.addAttribute("visitor", new Visitor());
        return "preapprove_form";
    }

    @PostMapping("/pre-approve")
    public String submitForm(@ModelAttribute Visitor visitor, Model model) throws IOException, WriterException, MessagingException {
        visitor.setCheckedIn(false);
        visitorRepository.save(visitor);

        String qrText = "http://localhost:8080/validate-checkin/" + visitor.getId();
        String qrDir = System.getProperty("user.dir") + "/qrcodes/";
        String fileName = "visitor_" + visitor.getId() + ".png";
        String qrPath = qrDir + fileName;

        // Ensure qrcodes directory exists
        Path dirPath = Paths.get(qrDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Generate QR code image
        QRCodeGenerator.generateQRCodeImage(qrText, qrPath);

        // Build HTML email body with visitor info
        String emailBody = "<h3>Guest Approved</h3>" +
                "<p><b>Name:</b> " + visitor.getName() + "</p>" +
                "<p><b>Visit Date & Time:</b> " + visitor.getVisitDateTime() + "</p>" +
                "<p><b>Reason:</b> " + visitor.getReason() + "</p>" +
                "<p>Please find the QR code attached. Show this QR code to the security guard at the gate.</p>";

        // Send email with QR code image attachment
        mailService.sendMailWithAttachment(visitor.getResidentEmail(), "Guest Approved", emailBody, qrPath);

        // Pass QR code image URL to Thymeleaf view
        model.addAttribute("qrCode", "/qrcodes/" + fileName);
        return "qr_display";
    }

    @GetMapping("/qrcodes/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveQRCode(@PathVariable String filename) throws IOException {
        Path file = Paths.get(System.getProperty("user.dir") + "/qrcodes/").resolve(filename);
        Resource resource = new UrlResource(file.toUri());

        if (resource.exists() || resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            throw new FileNotFoundException("QR code not found: " + filename);
        }
    }

    @GetMapping("/validate-checkin/{id}")
    @ResponseBody
    public String validateCheckIn(@PathVariable Long id) {
        Optional<Visitor> optionalVisitor = visitorRepository.findById(id);
        if (optionalVisitor.isPresent()) {
            Visitor visitor = optionalVisitor.get();
            if (!visitor.isCheckedIn()) {
                LocalDateTime now = LocalDateTime.now();
                if (now.isAfter(visitor.getVisitDateTime().minusHours(1)) && now.isBefore(visitor.getVisitDateTime().plusHours(4))) {
                    visitor.setCheckedIn(true);
                    visitor.setCheckInTime(now);
                    visitorRepository.save(visitor);

                    // Notify resident by email on guest check-in
                    mailService.sendSimpleMail(visitor.getResidentEmail(),
                            "Guest Checked-In",
                            "Guest " + visitor.getName() + " has checked in at " + now);
                    return "Check-in successful!";
                }
                return "Check-in time invalid!";
            } else {
                return "Already checked in.";
            }
        }
        return "Invalid visitor ID.";
    }

    @GetMapping("/visitors")
    public String listVisitors(Model model) {
        // Fetch all visitors from DB
        Iterable<Visitor> visitors = visitorRepository.findAll();
        model.addAttribute("visitors", visitors);
        return "visitor_list";
    }



    @GetMapping("/scan-checkin")
    public String showScannerPage() {
        return "scan_checkin";
    }

    @GetMapping("/visitor/delete/{id}")
    public String deleteVisitor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (visitorRepository.existsById(id)) {
            visitorRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Visitor deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Visitor not found.");
        }
        return "redirect:/visitors";
    }



}







//
//
