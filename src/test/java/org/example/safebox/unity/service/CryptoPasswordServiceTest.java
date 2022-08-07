package org.example.safebox.unity.service;

import org.example.safebox.model.Safebox;
import org.example.safebox.service.crypto.CryptoPasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
public class CryptoPasswordServiceTest {

    private CryptoPasswordService cryptoPasswordService;

    @BeforeEach
    public void setup() {
        cryptoPasswordService = new CryptoPasswordService();
    }

    @Test
    public void shouldOnlyEncryptPasswordFromSafeBox(){

        Safebox safebox = Safebox.builder()
                .name("aName")
                .password("aPassword")
                .createdAt(LocalDateTime.of(2021,10, 01, 10, 15))
                .items(List.of())
                .id(1L)
                .attempts(3)
                .build();

        cryptoPasswordService.encryptContent(safebox);

        assertEquals("aName", safebox.getName());
        assertNotEquals("aPassword", safebox.getPassword());
        assertEquals(LocalDateTime.of(2021,10, 01, 10, 15), safebox.getCreatedAt());
        assertEquals(1L, safebox.getId());
        assertEquals(3, safebox.getAttempts());
    }

    @Test
    public void shouldNotDecryptAnythingFromSafeBox(){

        Safebox safebox = Safebox.builder()
                .name("aName")
                .password("aPassword")
                .createdAt(LocalDateTime.of(2021,10, 01, 10, 15))
                .items(List.of())
                .id(1L)
                .attempts(3)
                .build();

        cryptoPasswordService.decryptContent(safebox);

        assertEquals("aName", safebox.getName());
        assertEquals("aPassword", safebox.getPassword());
        assertEquals(LocalDateTime.of(2021,10, 01, 10, 15), safebox.getCreatedAt());
        assertEquals(1L, safebox.getId());
        assertEquals(3, safebox.getAttempts());
    }
}
