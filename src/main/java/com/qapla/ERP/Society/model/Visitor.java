//// src/main/java/com/qapla/ERP/Society/model/Visitor.java
//package com.qapla.ERP.Society.model;
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//public class Visitor {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String residentEmail;
//    private String name;
//    private String contact;
//    private LocalDateTime visitDateTime;
//    private String reason;
//
//    private boolean checkedIn;
//    private LocalDateTime checkInTime;
//
//    // Getters and setters
//
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getResidentEmail() { return residentEmail; }
//    public void setResidentEmail(String residentEmail) { this.residentEmail = residentEmail; }
//
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//
//    public String getContact() { return contact; }
//    public void setContact(String contact) { this.contact = contact; }
//
//    public LocalDateTime getVisitDateTime() { return visitDateTime; }
//    public void setVisitDateTime(LocalDateTime visitDateTime) { this.visitDateTime = visitDateTime; }
//
//    public String getReason() { return reason; }
//    public void setReason(String reason) { this.reason = reason; }
//
//    public boolean isCheckedIn() { return checkedIn; }
//    public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }
//
//    public LocalDateTime getCheckInTime() { return checkInTime; }
//    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }
//}

// src/main/java/com/qapla/ERP/Society/model/Visitor.java
package com.qapla.ERP.Society.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String residentEmail;
    private String name;
    private String contact;
    private LocalDateTime visitDateTime;
    private String reason;

    private boolean checkedIn;
    private LocalDateTime checkInTime;

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getResidentEmail() { return residentEmail; }
    public void setResidentEmail(String residentEmail) { this.residentEmail = residentEmail; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public LocalDateTime getVisitDateTime() { return visitDateTime; }
    public void setVisitDateTime(LocalDateTime visitDateTime) { this.visitDateTime = visitDateTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public boolean isCheckedIn() { return checkedIn; }
    public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }
}
