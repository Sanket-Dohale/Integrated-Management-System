package com.qapla.ERP.Society.controller;

import com.qapla.ERP.Society.model.FacilityMember;
import com.qapla.ERP.Society.service.BulkUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class BulkUploadController {

    @Autowired
    private BulkUploadService bulkUploadService;

    @GetMapping("/bulkUpload")
    public String showBulkUploadForm() {
        return "bulkUpload";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "hasHeader", defaultValue = "true") boolean hasHeader,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/bulkUpload";
        }

        String fileName = file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if (!fileType.equals("csv") && !fileType.equals("json") && !fileType.equals("xml")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unsupported file format. Please upload CSV, JSON or XML.");
            return "redirect:/bulkUpload";
        }

        try {
            Map<String, Object> result = bulkUploadService.processFile(file, hasHeader);
            redirectAttributes.addFlashAttribute("uploadStats", result);
            redirectAttributes.addFlashAttribute("successMessage", "File processed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing file: " + e.getMessage());
        }

        return "redirect:/bulkUpload";
    }
}