package org.sushimaker.user_service.dto.menu;

public record MenuItem (
        String name,
        String dataId,
        String categoryId,
        String ImagePath,
        String detailRef,
        String description,
        String price
) { }
