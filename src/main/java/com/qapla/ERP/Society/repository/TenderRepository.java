// com/qapla/ERP/Society/repository/TenderRepository.java
package com.qapla.ERP.Society.repository;

import com.qapla.ERP.Society.model.Tender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenderRepository extends JpaRepository<Tender, Long> {
    List<Tender> findAllByOrderByOpeningDateDesc();
    List<Tender> findByIsPublishedTrueOrderByOpeningDateDesc();
    List<Tender> findByCreatedBy_MemberId(Integer memberId);
}