package com.qapla.ERP.Society.controller;



import com.qapla.ERP.Society.model.Tower;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.qapla.ERP.Society.model.Member;
import com.qapla.ERP.Society.model.MemberCSV;
import com.qapla.ERP.Society.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileUploadController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/upload-members")
    public String showUploadForm(Model model) {
        return "upload-members";
    }

    @PostMapping("/upload-members")
    public String uploadCSVFile(@RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes) {

        // Validate file
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a CSV file to upload.");
            return "redirect:/upload-members";
        }

        List<Member> validMembers = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        int totalRecords = 0;

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            // Configure CSV to Member mapping
            HeaderColumnNameMappingStrategy<MemberCSV> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(MemberCSV.class);

            // Parse CSV file
            CsvToBean<MemberCSV> csvToBean = new CsvToBeanBuilder<MemberCSV>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<MemberCSV> csvMembers = csvToBean.parse();
            totalRecords = csvMembers.size();

            // Process each record
            for (MemberCSV csvMember : csvMembers) {
                try {
                    // Validate and convert to Member entity
                    Member member = validateAndConvert(csvMember);
                    validMembers.add(member);
                } catch (Exception e) {
                    errorMessages.add("Error in record: " + csvMember + " - " + e.getMessage());
                }
            }

            // Save valid members
            memberRepository.saveAll(validMembers);

            // Prepare result message
            String message = String.format(
                    "File processed successfully! Total records: %d, Successfully imported: %d, Failed: %d",
                    totalRecords, validMembers.size(), errorMessages.size());

            redirectAttributes.addFlashAttribute("message", message);

            if (!errorMessages.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
            }

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", "An error occurred while processing the CSV file: " + ex.getMessage());
        }

        return "redirect:/upload-members";
    }

    private Member validateAndConvert(MemberCSV csvMember) throws Exception {
        Member member = new Member();

        // Validate and set fields
        if (csvMember.getMemberName() == null || csvMember.getMemberName().isEmpty()) {
            throw new Exception("Member name is required");
        }
        member.setMemberName(csvMember.getMemberName());

        try {
            member.setTower(Tower.valueOf(csvMember.getTower()));
        } catch (IllegalArgumentException e) {
            throw new Exception("Invalid tower value: " + csvMember.getTower());
        }

        try {
            member.setFlatNo(Integer.parseInt(csvMember.getFlatNo()));
        } catch (NumberFormatException e) {
            throw new Exception("Invalid flat number: " + csvMember.getFlatNo());
        }

        member.setPrimary("Y".equalsIgnoreCase(csvMember.getPrimary()));
        member.setActive(true);
        member.setActionDate(LocalDate.now());
        member.setActionBy("CSV Import");

        // Set optional fields
        if (csvMember.getStartDate() != null && !csvMember.getStartDate().isEmpty()) {
            member.setStartDate(LocalDate.parse(csvMember.getStartDate()));
        }

        if (csvMember.getEndDate() != null && !csvMember.getEndDate().isEmpty()) {
            member.setEndDate(LocalDate.parse(csvMember.getEndDate()));
        }

        return member;
    }
}
