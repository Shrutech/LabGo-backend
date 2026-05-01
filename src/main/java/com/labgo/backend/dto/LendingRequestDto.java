package com.labgo.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LendingRequestDto {

    @NotNull(message = "Equipment ID is required")
    private Long equipmentId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotBlank(message = "Issued to is required")
    private String issuedTo;

    private String contactNumber;

    @NotBlank(message = "Issued to role is required")
    @Pattern(regexp = "STUDENT|PROFESSOR|TECHNICIAN", message = "Role must be STUDENT, PROFESSOR, or TECHNICIAN")
    private String issuedToRole;

    private LocalDate issueDate;

    private LocalDate returnDate;

    private Long userId;
}
