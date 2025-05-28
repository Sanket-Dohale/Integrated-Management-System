

package com.qapla.ERP.Society.repository;

import com.qapla.ERP.Society.model.NocRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NocRequestRepository extends JpaRepository<NocRequest, Long> {

    @Query("SELECT r FROM NocRequest r WHERE " +
            "LOWER(r.tower) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.flatNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.owners) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.bankName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<NocRequest> findBySearchKeyword(@Param("keyword") String keyword);

}

