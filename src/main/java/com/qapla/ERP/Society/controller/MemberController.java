package com.qapla.ERP.Society.controller;

import com.qapla.ERP.Society.model.Member;
import com.qapla.ERP.Society.repository.MemberRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;



    // ✅ Pagination with error handling
    @GetMapping("/members")
    public String memberList(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, size);
        Page<Member> memberPage = memberRepository.findAll(pageable);

        if (page >= memberPage.getTotalPages() && memberPage.getTotalPages() > 0) {
            page = memberPage.getTotalPages() - 1;
            pageable = PageRequest.of(page, size);
            memberPage = memberRepository.findAll(pageable);
        }

        model.addAttribute("members", memberPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", memberPage.getTotalPages());
        model.addAttribute("pageSize", size);

        return "member-list";
    }

    @GetMapping("/add-member")
    public String showAddMemberForm(Model model) {
        model.addAttribute("member", new Member());
        return "add-member";
    }

    // ✅ Improved error handling while saving members
    @PostMapping("/save-member")
    public String saveMember(@Valid @ModelAttribute Member member,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "add-member";
        }

        if (member.getStartDate() == null) {
            member.setStartDate(LocalDate.now());
        }
        member.setActive(true);
        member.setActionDate(LocalDate.now());

        if (member.getActionBy() == null || member.getActionBy().isEmpty()) {
            member.setActionBy("System");
        }

        try {
            memberRepository.save(member);
            redirectAttributes.addFlashAttribute("successMessage", "Member saved successfully!");
            return "redirect:/members";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving member: " + e.getMessage());
            return "redirect:/add-member";
        }
    }

    @GetMapping("/member/{id}")
    public String viewMemberDetails(@PathVariable int id, Model model) {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isEmpty()) {
            throw new IllegalArgumentException("Invalid member Id: " + id);
        }
        model.addAttribute("member", member.get());
        return "member-details";
    }

    @GetMapping("/member/delete/{id}")
    public String showDeleteConfirmation(@PathVariable int id, Model model) {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isEmpty()) {
            throw new IllegalArgumentException("Invalid member Id: " + id);
        }
        model.addAttribute("member", member.get());
        return "delete-confirmation";
    }

    // ✅ Soft Delete Feature
    @PostMapping("/member/delete/{id}")
    public String deleteMember(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<Member> memberOptional = memberRepository.findById(id);
        if (memberOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Member not found!");
            return "redirect:/members";
        }

        Member member = memberOptional.get();
        member.setActive(false);
        member.setActionType("DELETE");
        member.setActionDate(LocalDate.now());

        memberRepository.save(member);

        redirectAttributes.addFlashAttribute("successMessage",
                "Member " + member.getMemberName() + " marked as inactive.");
        return "redirect:/members";
    }
}
