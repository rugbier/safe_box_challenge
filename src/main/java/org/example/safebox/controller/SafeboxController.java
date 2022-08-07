package org.example.safebox.controller;

import org.example.safebox.dto.*;
import org.example.safebox.service.safebox.SafeboxService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/safebox")
@AllArgsConstructor
@Validated
public class SafeboxController {

    private SafeboxService safeboxService;

    @PostMapping("/")
    public ResponseEntity<SafeboxResponseDTO> createSafebox(@Valid @RequestBody SafeboxRequestDTO safeboxRequest) {
        return ResponseEntity.ok(SafeboxResponseDTO.builder()
                .id(safeboxService.createNewSafebox(safeboxRequest).getId())
                .build());
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<ItemsResponseDTO> getSafeboxItems(@PathVariable("id") Long safeboxId){
        return ResponseEntity.ok(safeboxService.getItemsFromSafebox(safeboxId));
    }

    @PutMapping("/{id}/items")
    public ResponseEntity<ItemsResponseDTO> addSafeboxItems(@PathVariable("id") Long safeboxId, @Valid @RequestBody ItemRequestDTO itemsRequest){
        return ResponseEntity.ok(safeboxService.addNewItemToSafebox(safeboxId, itemsRequest));
    }

    @GetMapping("/{id}/open")
    public ResponseEntity<OpenSafeboxResponseDTO> openSafebox(@PathVariable("id") Long safeboxId){
        return ResponseEntity.ok(OpenSafeboxResponseDTO.builder().token(safeboxService.openSafeBox(safeboxId)).build());
    }

}
