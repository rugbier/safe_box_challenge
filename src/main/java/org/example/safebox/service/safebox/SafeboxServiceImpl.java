package org.example.safebox.service.safebox;

import org.example.safebox.dto.ItemRequestDTO;
import org.example.safebox.dto.ItemsResponseDTO;
import org.example.safebox.dto.SafeboxRequestDTO;
import org.example.safebox.dto.SafeboxResponseDTO;
import org.example.safebox.exceptions.ExistingSafeboxException;
import org.example.safebox.exceptions.LockedSafeboxException;
import org.example.safebox.exceptions.SafeboxNotFoundException;
import org.example.safebox.mapper.SafeboxMapper;
import org.example.safebox.model.Content;
import org.example.safebox.model.Safebox;
import org.example.safebox.repository.SafeboxRepository;
import org.example.safebox.service.crypto.CryptoService;
import org.example.safebox.service.token.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SafeboxServiceImpl implements SafeboxService{

    private SafeboxRepository safeBoxRepository;
    @Qualifier("cryptoContentService")
    private CryptoService cryptoContentService;
    @Qualifier("cryptoPasswordService")
    private CryptoService cryptoPasswordService;
    private TokenService tokenService;

    @Transactional
    private void persistSafebox(Safebox safebox) {
        safeBoxRepository.save(safebox);
    }

    private void setItemsInSafebox(ItemRequestDTO itemsRequest, Safebox safebox) {
        List<Content> list = itemsRequest.getItems()
                .stream()
                .map(content -> Content.builder().content(content).createdAt(LocalDateTime.now()).build())
                .collect(Collectors.toList());

        safebox.getItems().addAll(list);
    }

    public SafeboxResponseDTO createNewSafebox(SafeboxRequestDTO requestDTO) {

        if (safeBoxRepository.findByName(requestDTO.getName()).isPresent()) {
            throw new ExistingSafeboxException();
        }

        Safebox safebox = Safebox.builder()
                .name(requestDTO.getName())
                .password(requestDTO.getPassword())
                .attempts(3)
                .items(List.of())
                .createdAt(LocalDateTime.now()).build();

        safebox = cryptoPasswordService.encryptContent(safebox);
        safebox = safeBoxRepository.save(safebox);
        return SafeboxMapper.INSTANCE.safeboxToSafeboxResponseDTO(safebox);
    }

    public ItemsResponseDTO getItemsFromSafebox(Long id) {
        Safebox safebox = cryptoContentService
                .decryptContent(safeBoxRepository.findById(id).orElseThrow(SafeboxNotFoundException::new));

        return SafeboxMapper.INSTANCE.safeboxToItemsResponseDTO(safebox);
    }

    public ItemsResponseDTO addNewItemToSafebox(Long id, ItemRequestDTO itemsRequest) {
        Safebox safebox = safeBoxRepository.findById(id).orElseThrow(SafeboxNotFoundException::new);

        cryptoContentService.decryptContent(safebox);

        setItemsInSafebox(itemsRequest, safebox);

        cryptoContentService.encryptContent(safebox);
        persistSafebox(safebox);
        cryptoContentService.decryptContent(safebox);

        return SafeboxMapper.INSTANCE.safeboxToItemsResponseDTO(safebox);
    }

    public String openSafeBox(Long id) {
        Optional<Safebox> safebox = Optional.of(safeBoxRepository.findById(id)
                .orElseThrow(SafeboxNotFoundException::new));

        return safebox
                .filter(sb -> sb.getAttempts() > 0)
                .map(sb -> tokenService.generateToken(id))
                .orElseThrow(LockedSafeboxException::new);
    }

    public void lockSafebox(Long safeboxId) {
        Safebox safebox = safeBoxRepository.findById(safeboxId).orElseThrow(SafeboxNotFoundException::new);

        Optional.of(safebox)
                .filter(sb -> sb.getAttempts() > 0)
                .ifPresentOrElse(sb -> {
                    sb.setAttempts(sb.getAttempts() - 1);
                    safeBoxRepository.save(sb);
                }, () -> {
                    throw new LockedSafeboxException();
                });

    }
}
