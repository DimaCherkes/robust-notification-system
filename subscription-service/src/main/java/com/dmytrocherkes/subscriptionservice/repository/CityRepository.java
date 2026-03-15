package com.dmytrocherkes.subscriptionservice.repository;

import com.dmytrocherkes.subscriptionservice.model.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
}
