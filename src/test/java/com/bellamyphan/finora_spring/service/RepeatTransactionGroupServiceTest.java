package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.entity.RepeatTransactionGroup;
import com.bellamyphan.finora_spring.entity.TransactionGroup;
import com.bellamyphan.finora_spring.repository.RepeatTransactionGroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepeatTransactionGroupServiceTest {

    @Mock
    private RepeatTransactionGroupRepository repeatRepository;

    @InjectMocks
    private RepeatTransactionGroupService service;

    // ---------------------------------------------------
    // markAsRepeat — already exists
    // ---------------------------------------------------
    @Test
    void markAsRepeat_returnsExisting_whenAlreadyExists() {
        TransactionGroup group = new TransactionGroup();
        group.setId("G1");

        RepeatTransactionGroup existing = new RepeatTransactionGroup(group);

        when(repeatRepository.findById("G1")).thenReturn(Optional.of(existing));

        RepeatTransactionGroup result = service.markAsRepeat(group);

        assertSame(existing, result);
        verify(repeatRepository, never()).save(any());
    }

    // ---------------------------------------------------
    // markAsRepeat — creates new when not exists
    // ---------------------------------------------------
    @Test
    void markAsRepeat_createsNew_whenNotExists() {
        TransactionGroup group = new TransactionGroup();
        group.setId("G1");

        when(repeatRepository.findById("G1")).thenReturn(Optional.empty());

        RepeatTransactionGroup saved = new RepeatTransactionGroup(group);
        when(repeatRepository.save(any())).thenReturn(saved);

        RepeatTransactionGroup result = service.markAsRepeat(group);

        assertEquals(saved, result);
        verify(repeatRepository).save(any(RepeatTransactionGroup.class));
    }

    // ---------------------------------------------------
    // removeRepeat — removes when exists
    // ---------------------------------------------------
    @Test
    void removeRepeat_deletes_whenExists() {
        TransactionGroup group = new TransactionGroup();
        group.setId("G1");

        RepeatTransactionGroup repeat = new RepeatTransactionGroup(group);

        when(repeatRepository.findById("G1")).thenReturn(Optional.of(repeat));

        service.removeRepeat(group);

        verify(repeatRepository).delete(repeat);
    }

    // ---------------------------------------------------
    // removeRepeat — does nothing when not exists
    // ---------------------------------------------------
    @Test
    void removeRepeat_doesNothing_whenNotExists() {
        TransactionGroup group = new TransactionGroup();
        group.setId("G1");

        when(repeatRepository.findById("G1")).thenReturn(Optional.empty());

        service.removeRepeat(group);

        verify(repeatRepository, never()).delete(any());
    }

    // ---------------------------------------------------
    // exists()
    // ---------------------------------------------------
    @Test
    void exists_returnsValueFromRepository() {
        when(repeatRepository.existsById("G1")).thenReturn(true);

        boolean exists = service.exists("G1");

        assertTrue(exists);
        verify(repeatRepository).existsById("G1");
    }
}
