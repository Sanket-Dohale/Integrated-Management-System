package com.qapla.ERP.Society.service;

import com.qapla.ERP.Society.model.FacilityMember;
import com.qapla.ERP.Society.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BulkUploadService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private FacilityMemberService facilityMemberService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> processFile(MultipartFile file, boolean hasHeader) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.asList("csv", "json", "xml").contains(fileType)) {
            throw new IllegalArgumentException("Unsupported file format. Only CSV, JSON, and XML are supported");
        }

        List<FacilityMember> members;
        List<Map<String, String>> errorRecords = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            switch (fileType) {
                case "csv":
                    members = processCSV(inputStream, hasHeader, errorRecords);
                    break;
                case "json":
                    members = processJSON(inputStream, errorRecords);
                    break;
                case "xml":
                    members = processXML(inputStream, errorRecords);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported file format");
            }
        }

        // Save valid members
        int insertedCount = saveMembers(members, errorRecords);

        return prepareResults(fileName, members.size(), insertedCount, errorRecords);
    }

    private List<FacilityMember> processCSV(InputStream inputStream, boolean hasHeader,
                                            List<Map<String, String>> errorRecords) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            List<String[]> lines = reader.lines()
                    .map(line -> line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1))
                    .collect(Collectors.toList());

            if (lines.isEmpty()) {
                throw new IllegalArgumentException("CSV file is empty");
            }

            String[] headers;
            if (hasHeader) {
                headers = Arrays.stream(lines.remove(0))
                        .map(String::trim)
                        .toArray(String[]::new);
            } else {
                headers = new String[lines.get(0).length];
                for (int i = 0; i < headers.length; i++) {
                    headers[i] = "field" + (i + 1);
                }
            }

            List<FacilityMember> members = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String[] values = lines.get(i);
                if (values.length != headers.length) {
                    addErrorRecord(errorRecords, i + (hasHeader ? 2 : 1),
                            "Column count doesn't match header count", String.join(",", values));
                    continue;
                }

                Map<String, String> row = new HashMap<>();
                for (int j = 0; j < headers.length; j++) {
                    row.put(headers[j], values[j].trim().replaceAll("^\"|\"$", ""));
                }

                try {
                    members.add(createMemberFromMap(row));
                } catch (Exception e) {
                    addErrorRecord(errorRecords, i + (hasHeader ? 2 : 1), e.getMessage(), String.join(",", values));
                }
            }
            return members;
        }
    }

    private List<FacilityMember> processJSON(InputStream inputStream, List<Map<String, String>> errorRecords)
            throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String jsonContent = reader.lines().collect(Collectors.joining());

            List<Map<String, String>> jsonData;
            try {
                if (jsonContent.trim().startsWith("[")) {
                    jsonData = objectMapper.readValue(jsonContent, new TypeReference<List<Map<String, String>>>() {});
                } else {
                    Map<String, String> singleObject = objectMapper.readValue(jsonContent,
                            new TypeReference<Map<String, String>>() {});
                    jsonData = Collections.singletonList(singleObject);
                }
            } catch (Exception e) {
                throw new Exception("Invalid JSON format: " + e.getMessage());
            }

            List<FacilityMember> members = new ArrayList<>();
            for (int i = 0; i < jsonData.size(); i++) {
                try {
                    members.add(createMemberFromMap(jsonData.get(i)));
                } catch (Exception e) {
                    addErrorRecord(errorRecords, i + 1, e.getMessage(), jsonData.get(i).toString());
                }
            }
            return members;
        }
    }

    private List<FacilityMember> processXML(InputStream inputStream, List<Map<String, String>> errorRecords)
            throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // XXE protection
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new InputStreamReader(inputStream)));

        NodeList nodeList = document.getDocumentElement().getChildNodes();
        List<FacilityMember> members = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Map<String, String> row = new HashMap<>();

                NodeList childNodes = element.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        row.put(childNode.getNodeName(), childNode.getTextContent());
                    }
                }

                try {
                    members.add(createMemberFromMap(row));
                } catch (Exception e) {
                    addErrorRecord(errorRecords, i + 1, e.getMessage(), element.getTextContent());
                }
            }
        }
        return members;
    }

    private FacilityMember createMemberFromMap(Map<String, String> row) throws Exception {
        FacilityMember member = new FacilityMember();

        // Validate required fields
        validateRequiredField(row, "Staff_Name");
        validateRequiredField(row, "Role");
        validateRequiredField(row, "start_date");

        member.setStaff_Name(row.get("Staff_Name"));
        member.setRole(Role.valueOf(row.get("Role").toUpperCase()));
        member.setStart_date(parseDate(row.get("start_date")));

        // Optional fields
        if (row.containsKey("end_date") && !row.get("end_date").isEmpty()) {
            member.setEnd_date(parseDate(row.get("end_date")));
        }

        member.setIs_Active(row.containsKey("Is_Active") &&
                ("true".equalsIgnoreCase(row.get("Is_Active")) || "1".equals(row.get("Is_Active"))));

        if (row.containsKey("Action_Type") && !row.get("Action_Type").isEmpty()) {
            member.setAction_Type(row.get("Action_Type"));
        }

        if (row.containsKey("action_date") && !row.get("action_date").isEmpty()) {
            member.setAction_date(parseDate(row.get("action_date")));
        }

        if (row.containsKey("Action_By") && !row.get("Action_By").isEmpty()) {
            member.setAction_By(row.get("Action_By"));
        }

        return member;
    }

    private void validateRequiredField(Map<String, String> row, String fieldName) throws Exception {
        if (!row.containsKey(fieldName) || row.get(fieldName) == null || row.get(fieldName).isEmpty()) {
            throw new Exception(fieldName + " is required");
        }
    }

    private java.sql.Date parseDate(String dateString) throws Exception {
        try {
            return java.sql.Date.valueOf(LocalDate.parse(dateString, DATE_FORMATTER));
        } catch (Exception e) {
            throw new Exception("Invalid date format for value: " + dateString + ". Expected format: yyyy-MM-dd");
        }
    }

    private int saveMembers(List<FacilityMember> members, List<Map<String, String>> errorRecords) {
        int insertedCount = 0;
        for (FacilityMember member : members) {
            try {
                facilityMemberService.addMember(member);
                insertedCount++;
            } catch (Exception e) {
                addErrorRecord(errorRecords, -1, "Database error: " + e.getMessage(), member.toString());
            }
        }
        return insertedCount;
    }

    private Map<String, Object> prepareResults(String fileName, int totalRecords,
                                               int insertedCount, List<Map<String, String>> errorRecords) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("fileName", fileName);
        result.put("totalRecords", totalRecords + errorRecords.size());
        result.put("insertedCount", insertedCount);
        result.put("errorCount", errorRecords.size());
        result.put("errorRecords", errorRecords);
        return result;
    }

    private void addErrorRecord(List<Map<String, String>> errorRecords, int lineNumber,
                                String error, String record) {
        Map<String, String> errorRecord = new HashMap<>();
        errorRecord.put("line", lineNumber > 0 ? String.valueOf(lineNumber) : "N/A");
        errorRecord.put("error", error);
        errorRecord.put("record", record);
        errorRecords.add(errorRecord);
    }
}