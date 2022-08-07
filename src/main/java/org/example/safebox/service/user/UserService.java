package org.example.safebox.service.user;

import org.example.safebox.model.Safebox;
import org.example.safebox.repository.SafeboxRepository;
import org.example.safebox.service.crypto.CryptoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private SafeboxRepository safeboxRepository;
    @Qualifier("cryptoPasswordService")
    private CryptoService cryptoPasswordService;

    public Boolean isUserAbleToOpenSafebox(String safeboxName, String password){
        return safeboxRepository.findByName(safeboxName)
                .map(
                        sb -> cryptoPasswordService.encryptContent(Safebox.builder()
                                .password(password)
                                .build()
                        ).getPassword().equals(sb.getPassword())).orElse(false);
    }
}
