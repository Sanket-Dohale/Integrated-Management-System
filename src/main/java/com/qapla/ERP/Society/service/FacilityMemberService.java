

package com.qapla.ERP.Society.service;

import com.qapla.ERP.Society.model.FacilityMember;
import com.qapla.ERP.Society.repository.FacilityMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacilityMemberService {

    @Autowired
    private FacilityMemberRepository repository;

    // Fetch all members (without pagination)
    public List<FacilityMember> getAllMembers() {
        return repository.findAll();
    }

    // Fetch members with pagination
    public Page<FacilityMember> getAllMembers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    // Add a new member
    public FacilityMember addMember(FacilityMember member) {
        return repository.save(member);
    }


    // Delete a member by ID
    public void deleteMember(int id) {
        repository.deleteById(id);
    }

    // Search member by FM_id
    public FacilityMember searchMemberById(int fmId) {
        return repository.findByFMId(fmId);
    }

}

