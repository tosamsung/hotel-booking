package com.hotelbooking.HotelBooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelbooking.HotelBooking.entity.employee.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

}
