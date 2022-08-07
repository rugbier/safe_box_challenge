package org.example.safebox.unity.service;

import org.example.safebox.security.AES;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AESTest {
    @InjectMocks
    private AES aes;

    @BeforeEach
    public void setup() {
        aes = new AES();
    }

    @Test
    public void shouldEncryptString(){
        String aString = "abc123";

        assertEquals("5rnlsQfchpuaQRk5LYlBaw==", aes.encrypt(aString));
    }

    @Test
    public void shouldDecryptString(){
        String aString = "5rnlsQfchpuaQRk5LYlBaw==";

        assertEquals("abc123", aes.decrypt(aString));
    }
}
