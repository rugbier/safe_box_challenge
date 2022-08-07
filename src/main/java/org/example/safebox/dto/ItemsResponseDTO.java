package org.example.safebox.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Generated
public class ItemsResponseDTO {
    private List<String> items;
}
