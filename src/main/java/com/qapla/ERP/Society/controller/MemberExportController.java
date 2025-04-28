package com.qapla.ERP.Society.controller;

import com.qapla.ERP.Society.model.Member;
import com.qapla.ERP.Society.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MemberExportController {

    private final MemberRepository memberRepository;

    public MemberExportController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/export/members")
    public ResponseEntity<byte[]> exportMembers(@RequestParam String format) throws Exception {
        List<Member> members = memberRepository.findAll();

        String fileContent;
        String fileName;
        MediaType mediaType;

        if (!"csv".equalsIgnoreCase(format) && !"json".equalsIgnoreCase(format) && !"xml".equalsIgnoreCase(format)) {
            return ResponseEntity.badRequest().body("Invalid format. Use csv, json, or xml.".getBytes());
        }

        switch (format.toLowerCase()) {
            case "csv":
                fileName = "members.csv";
                mediaType = MediaType.valueOf("text/csv");
                fileContent = exportToCSV(members);
                break;
            case "json":
                fileName = "members.json";
                mediaType = MediaType.APPLICATION_JSON;
                fileContent = new ObjectMapper().writeValueAsString(members);
                break;
            case "xml":
                fileName = "members.xml";
                mediaType = MediaType.APPLICATION_XML;
                fileContent = new XmlMapper().writeValueAsString(members);
                break;
            default:
                return ResponseEntity.badRequest().body("Invalid format. Use csv, json, or xml.".getBytes());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(fileContent.getBytes(StandardCharsets.UTF_8).length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent.getBytes(StandardCharsets.UTF_8));
    }

    private String exportToCSV(List<Member> members) {
        StringBuilder sb = new StringBuilder();
        sb.append("MemberID,MemberName,Tower,FlatNo,IsPrimary,IsActive,StartDate,EndDate\n");
        for (Member member : members) {
            sb.append(member.getMemberId()).append(",")
                    .append(member.getMemberName()).append(",")
                    .append(member.getTower()).append(",")
                    .append(member.getFlatNo()).append(",")
                    .append(member.isPrimary() ? "Yes" : "No").append(",")
                    .append(member.isActive() ? "Active" : "Inactive").append(",")
                    .append(member.getStartDate()).append(",")
                    .append(member.getEndDate() != null ? member.getEndDate() : "N/A").append("\n");
        }
        return sb.toString();
    }
}

