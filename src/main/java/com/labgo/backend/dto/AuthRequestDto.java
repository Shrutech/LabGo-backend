package com.labgo.backend.dto;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String name;
    private String email;
    private String password;
}