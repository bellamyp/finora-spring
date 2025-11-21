package com.bellamyphan.finora_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequestDto {
    private String name;
    private String email;
    private String password;
    private String role;
}
