package org.example.safebox.service.crypto;

import org.example.safebox.model.Safebox;

public interface CryptoService {

    Safebox encryptContent(Safebox safebox);
    Safebox decryptContent(Safebox safebox);

}
