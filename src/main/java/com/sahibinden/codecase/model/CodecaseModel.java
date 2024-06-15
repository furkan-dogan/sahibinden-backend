package com.sahibinden.codecase.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CodecaseModel {
    @Id
    @GeneratedValue
    private int ID;
    private String title;
    private String description;
    private String category;
    private String status;

}