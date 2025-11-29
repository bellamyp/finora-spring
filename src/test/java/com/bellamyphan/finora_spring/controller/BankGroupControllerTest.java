package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.BankGroupCreateDto;
import com.bellamyphan.finora_spring.dto.BankGroupDto;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.BankGroupService;
import com.bellamyphan.finora_spring.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BankGroupControllerTest {

    @Mock
    private BankGroupService bankGroupService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private BankGroupController controller;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ----------------------------------------------------------------------
    // GET /api/bank-groups
    // ----------------------------------------------------------------------
    @Test
    void getAllBankGroupsForCurrentUser_success() throws Exception {
        User user = new User();
        user.setId("user123");

        List<BankGroupDto> groups = List.of(
                new BankGroupDto("G1", "Group A"),
                new BankGroupDto("G2", "Group B")
        );

        when(jwtService.getCurrentUser()).thenReturn(user);
        when(bankGroupService.getAllBankGroupsForCurrentUser(user)).thenReturn(groups);

        mockMvc.perform(get("/api/bank-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("G1"))
                .andExpect(jsonPath("$[0].name").value("Group A"))
                .andExpect(jsonPath("$[1].id").value("G2"))
                .andExpect(jsonPath("$[1].name").value("Group B"));

        verify(jwtService).getCurrentUser();
        verify(bankGroupService).getAllBankGroupsForCurrentUser(user);
    }

    // ----------------------------------------------------------------------
    // POST /api/bank-groups
    // ----------------------------------------------------------------------
    @Test
    void createBankGroup_success() throws Exception {
        User user = new User();
        user.setId("user123");

        BankGroupCreateDto createDto = new BankGroupCreateDto("My New Group");

        BankGroupDto responseDto = new BankGroupDto("G100", "My New Group");

        when(jwtService.getCurrentUser()).thenReturn(user);
        when(bankGroupService.createBankGroup(any(BankGroupCreateDto.class), eq(user)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/bank-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("G100"))
                .andExpect(jsonPath("$.name").value("My New Group"));

        verify(jwtService).getCurrentUser();
        verify(bankGroupService).createBankGroup(any(BankGroupCreateDto.class), eq(user));
    }

    // ----------------------------------------------------------------------
    // POST validation error
    // ----------------------------------------------------------------------
    @Test
    void createBankGroup_validationError_returnsBadRequest() throws Exception {
        // invalid because name = ""
        BankGroupCreateDto invalidDto = new BankGroupCreateDto("");

        mockMvc.perform(post("/api/bank-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bankGroupService);
    }
}
