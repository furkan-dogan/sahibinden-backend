package com.sahibinden.codecase.model;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvertModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;

    @Size(min = 10, max = 50, message = "İlan başlığı en az 10, en fazla 50 karakter olmalıdır.\n")
    @Pattern(regexp = "^[A-Za-z0-9ğüşıöçĞÜŞİÖÇ].*", message = "İlan başlığı harf veya rakam ile başlamalıdır.\n")
    private String title;

    @Size(min = 20, max = 200, message = "İlan detay açıklaması en az 20, en fazla 200 karakter olmalıdır.")
    private String description;

    @Pattern(regexp = "^(Emlak|Vasıta|Alışveriş|Diğer)$", message = "Geçersiz ilan kategorisi.")
    private String category;

    private String status;
}
