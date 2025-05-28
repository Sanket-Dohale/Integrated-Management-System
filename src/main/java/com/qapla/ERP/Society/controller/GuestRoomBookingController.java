package com.qapla.ERP.Society.controller;

import com.qapla.ERP.Society.model.GuestRoomBooking;
import com.qapla.ERP.Society.repository.GuestRoomBookingRepository;
import com.qapla.ERP.Society.service.GuestMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/guest-room")
public class GuestRoomBookingController {

    @Autowired
    private GuestRoomBookingRepository bookingRepo;

    @Autowired
    private GuestMailService mailService;

    @GetMapping("/book")
    public String showBookingForm(Model model) {
        model.addAttribute("booking", new GuestRoomBooking());
        return "guest-room/book";
    }

    @PostMapping("/book")
    public String submitBooking(@ModelAttribute GuestRoomBooking booking) {
        booking.setStatus("PENDING");
        bookingRepo.save(booking);

        mailService.sendEmail("admin@example.com", "New Guest Room Booking",
                "Booking requested by flat " + booking.getFlatNumber());

        return "redirect:/guest-room/status";
    }

    @GetMapping("/status")
    public String viewStatus(Model model) {
        model.addAttribute("bookings", bookingRepo.findAll());
        return "guest-room/status";
    }

    @GetMapping("/admin")
    public String viewForAdmin(Model model) {
        model.addAttribute("bookings", bookingRepo.findByStatus("PENDING"));
        return "guest-room/admin";
    }

//    @PostMapping("/approve/{id}")
//    public String approve(@PathVariable Long id) {
//        GuestRoomBooking booking = bookingRepo.findById(id).orElseThrow();
//        booking.setStatus("APPROVED");
//        bookingRepo.save(booking);
//
//        mailService.sendEmail(booking.getEmail(), "Booking Approved",
//                "Your guest room booking has been approved.");
//        return "redirect:/guest-room/admin";
//    }
//
//    @PostMapping("/reject/{id}")
//    public String reject(@PathVariable Long id) {
//        GuestRoomBooking booking = bookingRepo.findById(id).orElseThrow();
//        booking.setStatus("REJECTED");
//        bookingRepo.save(booking);
//
//        mailService.sendEmail(booking.getEmail(), "Booking Rejected",
//                "Your guest room booking has been rejected.");
//        return "redirect:/guest-room/admin";
//    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id) {
        GuestRoomBooking booking = bookingRepo.findById(id).orElseThrow();
        booking.setStatus("APPROVED");
        bookingRepo.save(booking);

        String subject = "Notification: Guest Room Booking Approved";
        String message = String.format(
                "Dear %s,\n\n" +
                        "We are pleased to inform you that your guest room booking request has been approved. Below are the details of your booking:\n\n" +
                        "Flat Number: %s\n" +
                        "Guest Name: %s\n" +
                        "Purpose: %s\n" +
                        "Start Date: %s\n" +
                        "End Date: %s\n\n" +
                        "Please ensure that you and your guest adhere to all society rules during the stay.\n\n" +
                        "Should you have any questions or require further assistance, please do not hesitate to contact the society office.\n\n" +
                        "Thank you for your cooperation.\n\n" +
                        "Best regards,\n" +
                        "Society Management Team",
                booking.getGuestName(),
                booking.getFlatNumber(),
                booking.getGuestName(),
                booking.getPurpose(),
                booking.getStartDate(),
                booking.getEndDate()
        );

        mailService.sendEmail(booking.getEmail(), subject, message);

        return "redirect:/guest-room/admin";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id) {
        GuestRoomBooking booking = bookingRepo.findById(id).orElseThrow();
        booking.setStatus("REJECTED");
        bookingRepo.save(booking);

        String subject = "Notification: Guest Room Booking Request Declined";
        String message = String.format(
                "Dear %s,\n\n" +
                        "We regret to inform you that your guest room booking request has been declined. The details of the booking are as follows:\n\n" +
                        "Flat Number: %s\n" +
                        "Guest Name: %s\n" +
                        "Purpose: %s\n" +
                        "Start Date: %s\n" +
                        "End Date: %s\n\n" +
                        "If you believe this decision was made in error or if you have any questions, please contact the society office for further clarification.\n\n" +
                        "Thank you for your understanding.\n\n" +
                        "Sincerely,\n" +
                        "Society Management Team",
                booking.getGuestName(),
                booking.getFlatNumber(),
                booking.getGuestName(),
                booking.getPurpose(),
                booking.getStartDate(),
                booking.getEndDate()
        );

        mailService.sendEmail(booking.getEmail(), subject, message);

        return "redirect:/guest-room/admin";
    }


}
