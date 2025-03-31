package com.qapla.ERP.Society.repository;

import com.qapla.ERP.Society.model.FacilityMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface FacilityMemberRepository extends JpaRepository<FacilityMember, Integer> {

    @Query("SELECT fm FROM FacilityMember fm WHERE fm.FM_id = :fmId")
    FacilityMember findByFMId(@Param("fmId") int fmId);

    @Query("SELECT fm FROM FacilityMember fm WHERE fm.Staff_Name LIKE %:name%")
    List<FacilityMember> findByStaffNameContaining(@Param("name") String name);

}


