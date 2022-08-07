package org.example.safebox.unity.service;

import org.example.safebox.dto.ItemRequestDTO;
import org.example.safebox.dto.ItemsResponseDTO;
import org.example.safebox.dto.SafeboxRequestDTO;
import org.example.safebox.dto.SafeboxResponseDTO;
import org.example.safebox.model.Content;
import org.example.safebox.model.Safebox;
import org.example.safebox.service.crypto.CryptoService;
import org.example.safebox.service.token.TokenService;
import org.example.safebox.exceptions.ExistingSafeboxException;
import org.example.safebox.exceptions.LockedSafeboxException;
import org.example.safebox.exceptions.SafeboxNotFoundException;
import org.example.safebox.repository.SafeboxRepository;
import org.example.safebox.service.safebox.SafeboxService;
import org.example.safebox.service.safebox.SafeboxServiceImpl;
import org.example.safebox.unity.utils.SafeBoxTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SafeboxServiceTest {

    @Mock
    private SafeboxRepository safeBoxRepository;

    @Mock(name = "cryptoContentService")
    private CryptoService cryptoContentService;

    @Mock(name = "cryptoPasswordService")
    private CryptoService cryptoPasswordService;

    @Mock
    private TokenService tokenService;

    private SafeboxService safeboxService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        safeboxService = new SafeboxServiceImpl(safeBoxRepository, cryptoContentService, cryptoPasswordService, tokenService);
    }

    @Test
    public void shouldOpenSafeboxWhenSafeboxExist(){
        Safebox safebox = SafeBoxTestUtils.createSafebox("aName","aPassword", LocalDateTime.now(), List.of(),1L,3);
        when(safeBoxRepository.findById(any())).thenReturn(Optional.of(safebox));
        when(tokenService.generateToken(any())).thenReturn("a_generated_token");

        Assertions.assertEquals("a_generated_token", safeboxService.openSafeBox(1L));

        verify(safeBoxRepository, times(1)).findById(anyLong());
        verify(tokenService, times(1)).generateToken(anyLong());
    }

    @Test
    public void shouldThrowExceptionWhenSafeboxNotExist(){
        when(safeBoxRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(SafeboxNotFoundException.class, () -> safeboxService.openSafeBox(1L));

        verify(safeBoxRepository, times(1)).findById(anyLong());
    }

    @Test
    public void shouldGetItemsWhenSafeboxExist(){
        Content content = SafeBoxTestUtils.createContent("A Content",1L ,LocalDateTime.now());
        Safebox safebox = SafeBoxTestUtils.createSafebox("aName","aPassword", LocalDateTime.now(), List.of(content),1L,3);
        when(safeBoxRepository.findById(any())).thenReturn(Optional.of(safebox));
        when(cryptoContentService.decryptContent(any())).thenReturn(safebox);

        ItemsResponseDTO returnedDto = safeboxService.getItemsFromSafebox(1L);

        Assertions.assertEquals(1, returnedDto.getItems().size());
        Assertions.assertEquals("A Content", returnedDto.getItems().get(0));

        verify(safeBoxRepository, times(1)).findById(anyLong());
        verify(cryptoContentService, times(1)).decryptContent(any(Safebox.class));
    }

    @Test
    public void shouldThrowExceptionWhenSafeboxExist(){
        when(safeBoxRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(SafeboxNotFoundException.class, () -> safeboxService.getItemsFromSafebox(1L));

        verify(safeBoxRepository, times(1)).findById(anyLong());
    }

    @Test
    public void shouldCreateSafeboxWhenNameOfSafeboxNotExist(){
        SafeboxRequestDTO request = SafeboxRequestDTO.builder().name("AName").build();
        Safebox safebox = SafeBoxTestUtils.createSafebox("aName","aPassword", LocalDateTime.now(), List.of(),1L,3);

        when(safeBoxRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(safeBoxRepository.save(any(Safebox.class))).thenReturn(safebox);
        when(cryptoPasswordService.encryptContent(any(Safebox.class))).thenReturn(safebox);

        SafeboxResponseDTO returnedValue = safeboxService.createNewSafebox(request);
        Assertions.assertEquals(safebox.getId(), returnedValue.getId());

        verify(safeBoxRepository, times(1)).findByName(anyString());
        verify(safeBoxRepository, times(1)).save(any());
        verify(cryptoPasswordService, times(1)).encryptContent(any(Safebox.class));

    }

    @Test
    public void shouldThrowExceptionWhenNameOfSafeboxExist(){
        SafeboxRequestDTO request = SafeboxRequestDTO.builder().name("AName").build();
        Safebox safebox = SafeBoxTestUtils.createSafebox("aName","aPassword", LocalDateTime.now(), List.of(),1L,3);

        when(safeBoxRepository.findByName(anyString())).thenReturn(Optional.of(safebox));

        Assertions.assertThrows(ExistingSafeboxException.class, () -> safeboxService.createNewSafebox(request));

        verify(safeBoxRepository, times(1)).findByName(anyString());
    }

    @Test
    public void shouldAddItemsWhenSafeboxExist(){
        ItemRequestDTO request = ItemRequestDTO.builder().items(List.of("Content1", "Content2")).build();
        Content content = SafeBoxTestUtils.createContent("A Content",1L ,LocalDateTime.now());
        ArrayList<Content> contentList = new ArrayList<>();
        contentList.add(content);
        Safebox safebox = SafeBoxTestUtils.createSafebox("aName","aPassword", LocalDateTime.now(), contentList,1L,3);

        when(safeBoxRepository.findById(anyLong())).thenReturn(Optional.of(safebox));
        when(cryptoContentService.decryptContent(any(Safebox.class))).thenReturn(safebox);
        when(cryptoContentService.encryptContent(any(Safebox.class))).thenReturn(safebox);
        when(safeBoxRepository.save(any(Safebox.class))).thenReturn(safebox);

        ItemsResponseDTO response = safeboxService.addNewItemToSafebox(1L, request);

        Assertions.assertEquals(safebox.getItems().size(), response.getItems().size());

        Assertions.assertTrue(safebox.getItems().stream().anyMatch(sb -> sb.getContent().equals("Content1")));
        Assertions.assertTrue(safebox.getItems().stream().anyMatch(sb -> sb.getContent().equals("Content2")));

        verify(safeBoxRepository, times(1)).findById(anyLong());
        verify(cryptoContentService, times(2)).decryptContent(any(Safebox.class));
        verify(safeBoxRepository, times(1)).save(any(Safebox.class));
        verify(cryptoContentService, times(1)).encryptContent(any(Safebox.class));

    }

    @Test
    public void shouldThrowExceptionWhenAddItemsAndSafeboxNotExist(){
        ItemRequestDTO request = ItemRequestDTO.builder().items(List.of("Content1","Content2")).build();

        when(safeBoxRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(SafeboxNotFoundException.class, () -> safeboxService.addNewItemToSafebox(1L, request));

        verify(safeBoxRepository, times(1)).findById(anyLong());
    }


    @Test
    public void shouldNotLockBoxWhenSafeboxExistAndHasThreeAttempts(){
        Safebox safebox = SafeBoxTestUtils.createSafebox("aName","aPassword", LocalDateTime.now(), List.of(),1L,3);

        when(safeBoxRepository.findById(any())).thenReturn(Optional.of(safebox));
        when(safeBoxRepository.save(any(Safebox.class))).thenReturn(safebox);

        safeboxService.lockSafebox(1L);

        Assertions.assertEquals(2, safebox.getAttempts());
        verify(safeBoxRepository, times(1)).findById(anyLong());
        verify(safeBoxRepository, times(1)).save(any(Safebox.class));
    }

    @Test
    public void shouldNotLockBoxWhenSafeboxExistAndHasTwoAttempts(){
        Safebox safebox = SafeBoxTestUtils.createSafebox("aName","aPassword", LocalDateTime.now(), List.of(),1L,2);

        when(safeBoxRepository.findById(any())).thenReturn(Optional.of(safebox));
        when(safeBoxRepository.save(any(Safebox.class))).thenReturn(safebox);

        safeboxService.lockSafebox(1L);

        Assertions.assertEquals(1, safebox.getAttempts());
        verify(safeBoxRepository, times(1)).findById(anyLong());
        verify(safeBoxRepository, times(1)).save(any(Safebox.class));
    }

    @Test
    public void shouldNotLockBoxWhenSafeboxExistAndHasOneAttempts(){
        Safebox safebox = SafeBoxTestUtils.createSafebox("aName","aPassword", LocalDateTime.now(), List.of(),1L,1);

        when(safeBoxRepository.findById(any())).thenReturn(Optional.of(safebox));
        when(safeBoxRepository.save(any(Safebox.class))).thenReturn(safebox);

        safeboxService.lockSafebox(1L);

        Assertions.assertEquals(0, safebox.getAttempts());
        verify(safeBoxRepository, times(1)).findById(anyLong());
        verify(safeBoxRepository, times(1)).save(any(Safebox.class));
    }

    @Test
    public void shouldThrowLockExceptionWhenSafeboxExistAndHasNoneAttempts(){
        Safebox safebox = SafeBoxTestUtils.createSafebox("aName","aPassword", LocalDateTime.now(), List.of(),1L,0);

        when(safeBoxRepository.findById(any())).thenReturn(Optional.of(safebox));

        Assertions.assertThrows(LockedSafeboxException.class, () -> safeboxService.lockSafebox(1L));

        Assertions.assertEquals(0, safebox.getAttempts());
        verify(safeBoxRepository, times(1)).findById(anyLong());
    }

    @Test
    public void shouldThrowExceptionWhenTryToLockBoxAndSafeboxNotExist(){
        when(safeBoxRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(SafeboxNotFoundException.class, () -> safeboxService.lockSafebox(1L));

        verify(safeBoxRepository, times(1)).findById(anyLong());
    }
}
