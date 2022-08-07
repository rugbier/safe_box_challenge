package org.example.safebox.unity.service;

import org.example.safebox.model.Safebox;
import org.example.safebox.service.crypto.CryptoService;
import org.example.safebox.service.user.UserService;
import org.example.safebox.repository.SafeboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private SafeboxRepository safeboxRepository;

    @Mock
    private CryptoService cryptoPasswordService;

    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(safeboxRepository, cryptoPasswordService);
    }

    @Test
    public void shouldReturnFalseWhenSafeboxIsNotFound() {
        when(safeboxRepository.findByName(any())).thenReturn(Optional.empty());

        assertFalse(userService.isUserAbleToOpenSafebox("aSafeBoxName","aPassword"));
    }

    @Test
    public void shouldReturnFalseWhenSafeboxIsFoundButPasswordsDontMatch() {
        Safebox safebox = Safebox.builder().password("anotherPassword").build();
        Safebox encryptedSafebox = Safebox.builder().password("aPassword").build();

        when(safeboxRepository.findByName(any())).thenReturn(Optional.of(safebox));
        when(cryptoPasswordService.encryptContent(any())).thenReturn(encryptedSafebox);

        assertFalse(userService.isUserAbleToOpenSafebox("aSafeBoxName","aPassword"));
    }

    @Test
    public void shouldReturnTrueWhenSafeboxIsFoundAndPasswordsMatch() {
        Safebox safebox = Safebox.builder().password("aPassword").build();
        Safebox encryptedSafebox = Safebox.builder().password("aPassword").build();

        when(safeboxRepository.findByName(any())).thenReturn(Optional.of(safebox));
        when(cryptoPasswordService.encryptContent(any())).thenReturn(encryptedSafebox);

        assertTrue(userService.isUserAbleToOpenSafebox("aSafeBoxName","aPassword"));
    }

}
