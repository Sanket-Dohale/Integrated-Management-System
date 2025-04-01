
package com.qapla.ERP.Society.controller;

import com.qapla.ERP.Society.model.FacilityMember;
import com.qapla.ERP.Society.service.FacilityMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Controller
public class FacilityMemberController {

    @Autowired
    private FacilityMemberService service;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/addMember")
    public String showAddMemberForm(Model model) {
        model.addAttribute("member", new FacilityMember());
        return "addMember";
    }

    @PostMapping("/saveMember")
    public String saveMember(@ModelAttribute FacilityMember member, RedirectAttributes redirectAttributes) {
        service.addMember(member);
        redirectAttributes.addFlashAttribute("successMessage", "Member saved successfully!");
        return "redirect:/showMembers?page=0";
    }

    @GetMapping("/showMembers")
    public String showMembers(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 10; // Number of members per page
        Page<FacilityMember> membersPage = service.getAllMembers(page, pageSize);

        // Add paginated members and pagination details to the model
        model.addAttribute("members", membersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", membersPage.getTotalPages());
        return "showMembers";
    }

    @GetMapping("/deleteMember/{id}")
    public String deleteMember(@PathVariable int id, RedirectAttributes redirectAttributes) {
        service.deleteMember(id); // Delete the member by ID
        redirectAttributes.addFlashAttribute("successMessage", "Member deleted successfully!");
        return "redirect:/showMembers?page=0";
    }

    @GetMapping("/searchMember")
    public String searchMember(@RequestParam int fmId, Model model) {
        FacilityMember member = service.searchMemberById(fmId);
        model.addAttribute("member", member);
        return "memberDetails";
    }

    @GetMapping("/exportMembers")
    public ResponseEntity<String> exportMembers(@RequestParam String format) {
        List<FacilityMember> members = service.getAllMembers();
        String data;
        String contentType;
        String fileName = "members." + format;

        switch (format.toLowerCase()) {
            case "csv":
                data = convertToCsv(members);
                contentType = "text/csv";
                break;
            case "json":
                data = convertToJson(members);
                contentType = "application/json";
                break;
            case "xml":
                data = convertToXml(members);
                contentType = "application/xml";
                break;
            default:
                return ResponseEntity.badRequest().body("Unsupported format");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(data);
    }

    private String convertToCsv(List<FacilityMember> members) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Staff Name,Role,Start Date,End Date,Is Active,Action Type,Action Date,Action By\n");

        for (FacilityMember member : members) {
            csv.append(member.getFM_id()).append(",")
                    .append(escapeCsv(member.getStaff_Name())).append(",")
                    .append(member.getRole()).append(",")
                    .append(member.getStart_date()).append(",")
                    .append(member.getEnd_date() != null ? member.getEnd_date() : "").append(",")
                    .append(member.getIs_Active() != null ? (member.getIs_Active() ? "Yes" : "No") : "").append(",")
                    .append(escapeCsv(member.getAction_Type())).append(",")
                    .append(member.getAction_date() != null ? member.getAction_date() : "").append(",")
                    .append(escapeCsv(member.getAction_By())).append("\n");
        }
        return csv.toString();
    }

    private String escapeCsv(String input) {
        if (input == null) return "";
        if (input.contains(",") || input.contains("\"") || input.contains("\n")) {
            return "\"" + input.replace("\"", "\"\"") + "\"";
        }
        return input;
    }

    private String convertToJson(List<FacilityMember> members) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");

        for (int i = 0; i < members.size(); i++) {
            FacilityMember member = members.get(i);
            json.append("  {\n")
                    .append("    \"id\": ").append(member.getFM_id()).append(",\n")
                    .append("    \"staffName\": \"").append(escapeJson(member.getStaff_Name())).append("\",\n")
                    .append("    \"role\": \"").append(member.getRole()).append("\",\n")
                    .append("    \"startDate\": \"").append(member.getStart_date()).append("\",\n")
                    .append("    \"endDate\": \"").append(member.getEnd_date() != null ? member.getEnd_date() : "").append("\",\n")
                    .append("    \"isActive\": ").append(member.getIs_Active() != null ? member.getIs_Active() : "null").append(",\n")
                    .append("    \"actionType\": \"").append(escapeJson(member.getAction_Type())).append("\",\n")
                    .append("    \"actionDate\": \"").append(member.getAction_date() != null ? member.getAction_date() : "").append("\",\n")
                    .append("    \"actionBy\": \"").append(escapeJson(member.getAction_By())).append("\"\n")
                    .append("  }");

            if (i < members.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("]");
        return json.toString();
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String convertToXml(List<FacilityMember> members) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<members>\n");

        for (FacilityMember member : members) {
            xml.append("  <member>\n")
                    .append("    <id>").append(member.getFM_id()).append("</id>\n")
                    .append("    <staffName>").append(escapeXml(member.getStaff_Name())).append("</staffName>\n")
                    .append("    <role>").append(member.getRole()).append("</role>\n")
                    .append("    <startDate>").append(member.getStart_date()).append("</startDate>\n")
                    .append("    <endDate>").append(member.getEnd_date() != null ? member.getEnd_date() : "").append("</endDate>\n")
                    .append("    <isActive>").append(member.getIs_Active() != null ? member.getIs_Active() : "").append("</isActive>\n")
                    .append("    <actionType>").append(escapeXml(member.getAction_Type())).append("</actionType>\n")
                    .append("    <actionDate>").append(member.getAction_date() != null ? member.getAction_date() : "").append("</actionDate>\n")
                    .append("    <actionBy>").append(escapeXml(member.getAction_By())).append("</actionBy>\n")
                    .append("  </member>\n");
        }
        xml.append("</members>");
        return xml.toString();
    }

    private String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

}






