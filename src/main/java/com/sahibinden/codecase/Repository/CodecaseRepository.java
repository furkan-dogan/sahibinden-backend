package com.sahibinden.codecase.repository;

import com.sahibinden.codecase.model.CodecaseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodecaseRepository extends JpaRepository<CodecaseModel, Integer> {
    List<CodecaseModel> findByCategoryAndTitleAndDescription(String category, String title, String description);
    long countByStatus(String status);

}