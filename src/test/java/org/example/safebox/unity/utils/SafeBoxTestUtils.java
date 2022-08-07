package org.example.safebox.unity.utils;

import org.example.safebox.model.Content;
import org.example.safebox.model.Safebox;

import java.time.LocalDateTime;
import java.util.List;

public class SafeBoxTestUtils {
    public static Safebox createSafebox(String name, String password, LocalDateTime createdAt, List<Content> items,
                                        Long id, Integer attempts){
        return Safebox.builder()
                .name(name)
                .password(password)
                .createdAt(createdAt)
                .items(items)
                .id(id)
                .attempts(attempts)
                .build();
    }

    public static Content createContent(String content, Long id, LocalDateTime createdAt){
        return Content.builder()
                .id(id)
                .createdAt(createdAt)
                .content(content).build();
    }
}
