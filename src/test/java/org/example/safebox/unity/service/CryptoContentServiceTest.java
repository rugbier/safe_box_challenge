package org.example.safebox.unity.service;

import org.example.safebox.model.Content;
import org.example.safebox.model.Safebox;
import org.example.safebox.security.AES;
import org.example.safebox.service.crypto.CryptoContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CryptoContentServiceTest {
    @Mock
    private AES mockAlgorithm;

    private CryptoContentService cryptoContentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        cryptoContentService = new CryptoContentService(mockAlgorithm);
    }

    @Test
    public void shouldEncryptContentWhenSafeboxIsPassed(){
        when(mockAlgorithm.encrypt(any())).thenReturn("anEncryptedValue");

        Safebox safebox = Safebox.builder()
                .name("aName")
                .password("aPassword")
                .createdAt(LocalDateTime.of(2021,10, 01, 10, 15))
                .items(List.of(Content.builder().content("aContent").build()))
                .id(1L)
                .attempts(3)
                .build();

        cryptoContentService.encryptContent(safebox);

        assertEquals("anEncryptedValue", safebox.getItems().get(0).getContent());

    }

    @Test
    public void shouldDecryptContentWhenSafeboxIsPassed(){
        when(mockAlgorithm.decrypt(any())).thenReturn("aDecrypted");

        Safebox safebox = Safebox.builder()
                .name("aName")
                .password("aPassword")
                .createdAt(LocalDateTime.of(2021,10, 01, 10, 15))
                .items(List.of(Content.builder().content("aContent").build()))
                .id(1L)
                .attempts(3)
                .build();

        cryptoContentService.decryptContent(safebox);

        assertEquals("aDecrypted", safebox.getItems().get(0).getContent());

    }
}
