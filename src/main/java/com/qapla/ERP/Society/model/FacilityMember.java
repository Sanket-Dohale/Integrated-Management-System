
package com.qapla.ERP.Society.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "Facility_Management")
public class FacilityMember {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int FM_id;

    @Column(nullable = false, length = 50)
    private String Staff_Name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role Role;



    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date start_date;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date end_date;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date action_date;

    private Boolean Is_Active;

    @Column(length = 30)
    private String Action_Type;


    @Column(length = 50)
    private String Action_By;

}

