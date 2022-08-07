package org.example.safebox.service.crypto;

import org.example.safebox.model.Safebox;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Qualifier("cryptoPasswordService")
public class CryptoPasswordService implements CryptoService{

    @Override
    public Safebox encryptContent(Safebox safebox) {
        safebox.setPassword(DigestUtils.md5Hex(safebox.getPassword()));
        return safebox;
    }

    @Override
    public Safebox decryptContent(Safebox safebox) {
        return safebox;
    }
}
