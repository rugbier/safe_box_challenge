package org.example.safebox.service.crypto;

import org.example.safebox.model.Safebox;
import org.example.safebox.security.AES;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Qualifier("cryptoContentService")
public class CryptoContentService implements CryptoService{
    private AES algorithm;

    @Override
    public Safebox encryptContent(Safebox safebox){
        safebox.getItems().forEach(c -> c.setContent(algorithm.encrypt(c.getContent())));
        return safebox;
    }

    @Override
    public Safebox decryptContent(Safebox safebox){
        safebox.getItems().forEach(c -> c.setContent(algorithm.decrypt(c.getContent())));
        return safebox;
    }
}
