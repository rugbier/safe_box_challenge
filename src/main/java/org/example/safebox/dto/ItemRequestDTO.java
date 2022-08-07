package org.example.safebox.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Generated
public class ItemRequestDTO {
    @NotNull
    @NotEmpty
    private List<String> items;
}
