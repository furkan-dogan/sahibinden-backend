package com.sahibinden.codecase.repository;

import com.sahibinden.codecase.model.AdvertModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvertRepository extends JpaRepository<AdvertModel, Integer> {
    List<AdvertModel> findByCategoryAndTitleAndDescription(String category, String title, String description);
    long countByStatus(String status);

}