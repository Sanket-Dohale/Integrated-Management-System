package com.qapla.ERP.Society.repository;

import com.qapla.ERP.Society.model.GuestRoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRoomBookingRepository extends JpaRepository<GuestRoomBooking, Long> {
    List<GuestRoomBooking> findByStatus(String status);
}
