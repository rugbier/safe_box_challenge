package org.example.safebox.mapper;

import org.example.safebox.dto.ItemsResponseDTO;
import org.example.safebox.dto.SafeboxResponseDTO;
import org.example.safebox.model.Content;
import org.example.safebox.model.Safebox;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.stream.Collectors;

@Mapper
public interface SafeboxMapper {

    SafeboxMapper INSTANCE = Mappers.getMapper(SafeboxMapper.class);

    @Mapping(source = "id", target = "id")
    SafeboxResponseDTO safeboxToSafeboxResponseDTO(Safebox safebox);

    default ItemsResponseDTO safeboxToItemsResponseDTO(Safebox safebox){
        return ItemsResponseDTO.builder().items(safebox.getItems().stream()
                .map(Content::getContent)
                .collect(Collectors.toList())).build();
    }
}
