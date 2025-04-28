package com.qapla.ERP.Society.repository;

import com.qapla.ERP.Society.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    Member findByUsername(String username);

    // Find all members ordered by MemberId in ascending order
    List<Member> findAllByOrderByMemberIdAsc();

    // Find members with pagination
    Page<Member> findAll(Pageable pageable);
}
