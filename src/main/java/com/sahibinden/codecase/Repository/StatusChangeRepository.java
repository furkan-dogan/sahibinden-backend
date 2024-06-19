package com.sahibinden.codecase.repository;

import com.sahibinden.codecase.model.StatusChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusChangeRepository extends JpaRepository<StatusChange, Integer> {
    List<StatusChange> findByAdvertIdOrderByChangeTimeAsc(int advertId);
}
