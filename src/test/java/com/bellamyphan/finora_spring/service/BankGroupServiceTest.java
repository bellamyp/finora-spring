package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.BankGroupCreateDto;
import com.bellamyphan.finora_spring.dto.BankGroupDto;
import com.bellamyphan.finora_spring.entity.BankGroup;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.BankGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankGroupServiceTest {

    private BankGroupRepository bankGroupRepository;
    private NanoIdService nanoIdService;
    private BankGroupService service;

    private User mockUser;

    @BeforeEach
    void setUp() {
        bankGroupRepository = mock(BankGroupRepository.class);
        nanoIdService = mock(NanoIdService.class);
        service = new BankGroupService(bankGroupRepository, nanoIdService);

        mockUser = new User();
        mockUser.setId("user1");
        mockUser.setEmail("user@example.com");
    }

    // ----------------------------------------------------------------------
    // CREATE BANK GROUP
    // ----------------------------------------------------------------------
    @Test
    void createBankGroup_success() {
        BankGroupCreateDto request = new BankGroupCreateDto();
        request.setName("My Group");

        when(bankGroupRepository.existsByNameAndUser("My Group", mockUser))
                .thenReturn(false);

        when(nanoIdService.generateUniqueId(bankGroupRepository))
                .thenReturn("G123456789");

        BankGroup savedGroup = new BankGroup("G123456789", "My Group", mockUser);

        when(bankGroupRepository.save(any(BankGroup.class)))
                .thenReturn(savedGroup);

        BankGroupDto result = service.createBankGroup(request, mockUser);

        assertNotNull(result);
        assertEquals("G123456789", result.getId());
        assertEquals("My Group", result.getName());

        verify(bankGroupRepository).existsByNameAndUser("My Group", mockUser);
        verify(bankGroupRepository).save(any(BankGroup.class));
    }

    @Test
    void createBankGroup_duplicateName_throwsException() {
        BankGroupCreateDto request = new BankGroupCreateDto();
        request.setName("Duplicate");

        when(bankGroupRepository.existsByNameAndUser("Duplicate", mockUser))
                .thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.createBankGroup(request, mockUser)
        );

        assertEquals("Bank group name already exists for this user", ex.getMessage());
        verify(bankGroupRepository, never()).save(any());
    }

    // ----------------------------------------------------------------------
    // GET ALL BANK GROUPS
    // ----------------------------------------------------------------------
    @Test
    void getAllBankGroupsForCurrentUser_success() {
        BankGroup g1 = new BankGroup("G1", "Group One", mockUser);
        BankGroup g2 = new BankGroup("G2", "Group Two", mockUser);

        when(bankGroupRepository.findAllByUser(mockUser))
                .thenReturn(List.of(g1, g2));

        List<BankGroupDto> result = service.getAllBankGroupsForCurrentUser(mockUser);

        assertEquals(2, result.size());
        assertEquals("G1", result.get(0).getId());
        assertEquals("Group One", result.get(0).getName());
        assertEquals("G2", result.get(1).getId());
        assertEquals("Group Two", result.get(1).getName());

        verify(bankGroupRepository).findAllByUser(mockUser);
    }

    @Test
    void getAllBankGroupsForCurrentUser_emptyList() {
        when(bankGroupRepository.findAllByUser(mockUser))
                .thenReturn(List.of());

        List<BankGroupDto> result = service.getAllBankGroupsForCurrentUser(mockUser);

        assertTrue(result.isEmpty());
        verify(bankGroupRepository).findAllByUser(mockUser);
    }
}
