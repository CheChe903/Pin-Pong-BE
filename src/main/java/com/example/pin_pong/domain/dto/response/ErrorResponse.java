package com.example.pin_pong.domain.dto.response;


import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ErrorResponse {
    private String message;
}
