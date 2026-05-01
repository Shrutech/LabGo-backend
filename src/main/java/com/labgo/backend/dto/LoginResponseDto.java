package com.labgo.backend.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String message;
    private Long id;
    private String email;
}
