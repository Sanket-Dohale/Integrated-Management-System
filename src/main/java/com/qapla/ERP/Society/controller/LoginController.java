package com.qapla.ERP.Society.controller;

import com.qapla.ERP.Society.model.Member;
import com.qapla.ERP.Society.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private MemberRepository memberRepository;



    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        // Already logged in? Send to home
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        Member member = memberRepository.findByUsername(username);

        if (member != null && password.equals(member.getPassword())) {
            // Check if user has one of the allowed roles
            if (member.getRole().name().equals("CHAIRMAN") ||
                    member.getRole().name().equals("SECRETARY") ||
                    member.getRole().name().equals("TREASURER")) {

                session.setAttribute("loggedInUser", member);
                return "redirect:/";
            } else {
                model.addAttribute("error", "You don't have permission to access this system");
                return "login";
            }
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}