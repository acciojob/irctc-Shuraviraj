package com.driver.repository;

import com.driver.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger,Integer> {
}
