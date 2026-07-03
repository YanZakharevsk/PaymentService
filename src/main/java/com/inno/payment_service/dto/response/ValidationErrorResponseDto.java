package com.inno.payment_service.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrorResponseDto {

    private String dateTime;

    @NotNull(message = "Errors map must not be empty")
    private Map<String, String> errorsMap;

    private int status;
}
