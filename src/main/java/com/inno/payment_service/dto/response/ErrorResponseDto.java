package com.inno.payment_service.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {

    private String dateTime;

    @NotBlank(message = "Description must not be blank")
    private String description;

    private int status;

}
