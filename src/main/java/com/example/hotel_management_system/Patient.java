package com.example.hotel_management_system;

import java.time.LocalDate;

public record Patient(
        String name,
        int age,
        String gender,
        String phone,
        String diagnosis,
        LocalDate admissionDate) {


}
