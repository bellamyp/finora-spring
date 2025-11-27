package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Optional;

@Data
public class TransactionResponseDto {
    
    private String id;
    private String groupId;
    private String date;          // nullable
    private BigDecimal amount;    // nullable
    private String notes;         // nullable
    private String bankId;        // mandatory
    private String brandId;       // nullable
    private String locationId;    // nullable
    private String typeId;        // nullable
    private boolean posted;

    // ==========================
    // Convert Entity → DTO
    // ==========================
    public static TransactionResponseDto fromEntity(Transaction tx, boolean posted) {
        return Optional.ofNullable(tx).map(t -> {
            TransactionResponseDto dto = new TransactionResponseDto();
            dto.setId(t.getId());
            dto.setGroupId(tx.getGroup().getId());
            dto.setDate(t.getDate() != null ? t.getDate().toString() : null);
            dto.setAmount(t.getAmount());
            dto.setNotes(t.getNotes());
            dto.setBankId(t.getBank() != null ? t.getBank().getId() : null);
            dto.setBrandId(t.getBrand() != null ? t.getBrand().getId() : null);
            dto.setLocationId(t.getLocation() != null ? t.getLocation().getId() : null);
            dto.setTypeId(t.getType() != null ? t.getType().getType().name() : null);
            dto.setPosted(posted);
            return dto;
        }).orElse(null);
    }
}
