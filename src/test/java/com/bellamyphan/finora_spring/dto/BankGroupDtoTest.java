package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.entity.BankGroup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankGroupDtoTest {

    @Test
    void testConstructorAndGetters() {
        BankGroupDto dto = new BankGroupDto("G1", "Group A");

        assertEquals("G1", dto.getId());
        assertEquals("Group A", dto.getName());
    }

    @Test
    void fromEntity_validEntity_returnsDto() {
        BankGroup entity = new BankGroup();
        entity.setId("BG100");
        entity.setName("Savings Accounts");

        BankGroupDto dto = BankGroupDto.fromEntity(entity);

        assertNotNull(dto);
        assertEquals("BG100", dto.getId());
        assertEquals("Savings Accounts", dto.getName());
    }

    @Test
    void fromEntity_nullEntity_returnsNull() {
        BankGroupDto dto = BankGroupDto.fromEntity(null);
        assertNull(dto);
    }
}
