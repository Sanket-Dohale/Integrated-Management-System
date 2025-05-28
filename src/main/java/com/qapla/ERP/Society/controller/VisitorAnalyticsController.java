package com.qapla.ERP.Society.controller;

import com.qapla.ERP.Society.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/analytics")
public class VisitorAnalyticsController {

    @Autowired
    private VisitorRepository visitorRepository;

    @GetMapping
    public String viewDashboard(Model model) {
        // Fetch raw stats from repository
        List<Object[]> dailyStats = visitorRepository.getDailyVisitorStats();
        List<Object[]> monthlyStats = visitorRepository.getMonthlyVisitorStats();
        List<Object[]> yearlyStats = visitorRepository.getYearlyVisitorStats();

        // Process daily stats: extract labels and counts
        List<String> dailyLabels = new ArrayList<>();
        List<Long> dailyCounts = new ArrayList<>();
        for (Object[] row : dailyStats) {
            dailyLabels.add(row[0].toString());  // date as String
            dailyCounts.add(((Number) row[1]).longValue());
        }

        // Process monthly stats: extract labels and counts
        List<String> monthlyLabels = new ArrayList<>();
        List<Long> monthlyCounts = new ArrayList<>();
        for (Object[] row : monthlyStats) {
            monthlyLabels.add("Month " + row[0].toString());
            monthlyCounts.add(((Number) row[1]).longValue());
        }

        // Process yearly stats: extract labels and counts
        List<String> yearlyLabels = new ArrayList<>();
        List<Long> yearlyCounts = new ArrayList<>();
        for (Object[] row : yearlyStats) {
            yearlyLabels.add(row[0].toString());
            yearlyCounts.add(((Number) row[1]).longValue());
        }

        // Add processed data to model attributes for Thymeleaf
        model.addAttribute("weeklyLabels", dailyLabels);
        model.addAttribute("weeklyCounts", dailyCounts);
        model.addAttribute("monthlyLabels", monthlyLabels);
        model.addAttribute("monthlyCounts", monthlyCounts);
        model.addAttribute("yearlyLabels", yearlyLabels);
        model.addAttribute("yearlyCounts", yearlyCounts);

        return "analytics_dashboard";
    }
}
