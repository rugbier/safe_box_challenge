package org.example.safebox.service.safebox;

import org.example.safebox.dto.ItemRequestDTO;
import org.example.safebox.dto.ItemsResponseDTO;
import org.example.safebox.dto.SafeboxRequestDTO;
import org.example.safebox.dto.SafeboxResponseDTO;


public interface SafeboxService {

    SafeboxResponseDTO createNewSafebox(SafeboxRequestDTO requestDTO);
    ItemsResponseDTO getItemsFromSafebox(Long id);
    ItemsResponseDTO addNewItemToSafebox(Long id, ItemRequestDTO itemsRequest);
    String openSafeBox(Long id);
    void lockSafebox(Long safeboxId);

}
