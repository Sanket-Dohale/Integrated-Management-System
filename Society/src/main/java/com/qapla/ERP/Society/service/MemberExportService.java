package com.qapla.ERP.Society.service;

import com.qapla.ERP.Society.model.Member;
import com.qapla.ERP.Society.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
public class MemberExportService {

    @Autowired
    private MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public void exportToCSV(List<Member> members, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=members.csv");

        PrintWriter writer = response.getWriter();
        writer.println("MemberId,MemberName,Tower,FlatNo,IsPrimary,IsActive,StartDate,EndDate");

        for (Member member : members) {
            writer.printf("%d,%s,%s,%s,%b,%b,%s,%s%n",
                    member.getMemberId(),
                    member.getMemberName(),
                    member.getTower(),
                    member.getFlatNo(),
                    member.isPrimary(),
                    member.isActive(),
                    member.getStartDate(),
                    member.getEndDate() != null ? member.getEndDate() : "N/A"
            );
        }
        writer.flush();
    }

    public void exportToJSON(List<Member> members, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=members.json");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(members);

        response.getWriter().write(json);
        response.getWriter().flush();
    }

    public void exportToXML(List<Member> members, HttpServletResponse response) throws IOException {
        response.setContentType("application/xml");
        response.setHeader("Content-Disposition", "attachment; filename=members.xml");

        XmlMapper xmlMapper = new XmlMapper();
        String xml = xmlMapper.writeValueAsString(members);

        response.getWriter().write(xml);
        response.getWriter().flush();
    }
}

