

// com/qapla/ERP/Society/controller/TenderController.java
package com.qapla.ERP.Society.controller;

import com.qapla.ERP.Society.model.Member;
import com.qapla.ERP.Society.model.Role;
import com.qapla.ERP.Society.model.Tender;
import com.qapla.ERP.Society.repository.TenderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qapla.ERP.Society.service.MailService;   // ✅ Used MailService

import java.time.LocalDate;

@Controller
@RequestMapping("/tenders")
public class TenderController {

    @Autowired
    private MailService mailService;   // ✅ Inject MailService

    @Autowired
    private TenderRepository tenderRepository;

    @GetMapping("")
    public String listTenders(Model model, HttpSession session) {
        Member loggedInUser = (Member) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (loggedInUser.getRole() == Role.CHAIRMAN || loggedInUser.getRole() == Role.SECRETARY) {
            model.addAttribute("tenders", tenderRepository.findAllByOrderByOpeningDateDesc());
        } else {
            model.addAttribute("tenders", tenderRepository.findByIsPublishedTrueOrderByOpeningDateDesc());
        }
        return "tender-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        Member loggedInUser = (Member) session.getAttribute("loggedInUser");
        if (loggedInUser == null ||
                !(loggedInUser.getRole() == Role.CHAIRMAN || loggedInUser.getRole() == Role.SECRETARY)) {
            return "redirect:/tenders";
        }

        model.addAttribute("tender", new Tender());
        return "tender-create";
    }

    @PostMapping("/save")
    public String saveTender(@ModelAttribute Tender tender,
                             BindingResult result,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Member loggedInUser = (Member) session.getAttribute("loggedInUser");
        if (loggedInUser == null ||
                !(loggedInUser.getRole() == Role.CHAIRMAN || loggedInUser.getRole() == Role.SECRETARY)) {
            return "redirect:/tenders";
        }

        if (result.hasErrors()) {
            return "tender-create";
        }

        tender.setCreatedBy(loggedInUser);
        tender.setCreatedAt(LocalDate.now());
        tenderRepository.save(tender);

        mailService.sendTenderNotification(tender);  // ✅ Call MailService to send mail

        redirectAttributes.addFlashAttribute("successMessage", "Tender created successfully!");
        return "redirect:/tenders";
    }

    @PostMapping("/publish/{id}")
    public String publishTender(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Member loggedInUser = (Member) session.getAttribute("loggedInUser");
        if (loggedInUser == null ||
                !(loggedInUser.getRole() == Role.CHAIRMAN || loggedInUser.getRole() == Role.SECRETARY)) {
            return "redirect:/tenders";
        }

        Tender tender = tenderRepository.findById(id).orElse(null);
        if (tender != null) {
            tender.setIsPublished(true);
            tenderRepository.save(tender);
            redirectAttributes.addFlashAttribute("successMessage", "Tender published successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Tender not found!");
        }
        return "redirect:/tenders";
    }

    @GetMapping("/{id}")
    public String viewTender(@PathVariable Long id, Model model) {
        Tender tender = tenderRepository.findById(id).orElse(null);
        if (tender == null) {
            return "redirect:/tenders";
        }
        model.addAttribute("tender", tender);
        return "view-tender";
    }


}
