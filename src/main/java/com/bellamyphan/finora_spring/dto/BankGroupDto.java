package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.entity.BankGroup;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class BankGroupDto {
    String id;
    String name;

    public static BankGroupDto fromEntity(BankGroup group) {
        return Optional.ofNullable(group)
                .map(b -> new BankGroupDto(b.getId(), b.getName()))
                .orElse(null);
    }
}
