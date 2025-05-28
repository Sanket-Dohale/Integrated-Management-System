

package com.qapla.ERP.Society.repository;

import com.qapla.ERP.Society.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    @Query("SELECT FUNCTION('DATE', v.visitDateTime), COUNT(v) FROM Visitor v GROUP BY FUNCTION('DATE', v.visitDateTime)")
    List<Object[]> getDailyVisitorStats();

    @Query("SELECT FUNCTION('MONTH', v.visitDateTime), COUNT(v) FROM Visitor v GROUP BY FUNCTION('MONTH', v.visitDateTime)")
    List<Object[]> getMonthlyVisitorStats();

    @Query("SELECT FUNCTION('YEAR', v.visitDateTime), COUNT(v) FROM Visitor v GROUP BY FUNCTION('YEAR', v.visitDateTime)")
    List<Object[]> getYearlyVisitorStats();
}