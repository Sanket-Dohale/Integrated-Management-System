package com.qapla.ERP.Society.model;

import com.opencsv.bean.CsvBindByName;

public class MemberCSV {

    @CsvBindByName(column = "Member Name")
    private String memberName;

    @CsvBindByName(column = "Tower")
    private String tower;

    @CsvBindByName(column = "Flat No")
    private String flatNo;

    @CsvBindByName(column = "Primary Member")
    private String primary;

    @CsvBindByName(column = "Start Date")
    private String startDate;

    @CsvBindByName(column = "End Date")
    private String endDate;

    // Getters and Setters
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getTower() { return tower; }
    public void setTower(String tower) { this.tower = tower; }
    public String getFlatNo() { return flatNo; }
    public void setFlatNo(String flatNo) { this.flatNo = flatNo; }
    public String getPrimary() { return primary; }
    public void setPrimary(String primary) { this.primary = primary; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    @Override
    public String toString() {
        return String.format("MemberCSV{memberName='%s', tower='%s', flatNo='%s'}",
                memberName, tower, flatNo);
    }
}

