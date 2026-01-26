package org.sushimaker.user_service.dto.menu;

// DTO для категорий меню
public record MenuCategory(
        String href,
        String imagePath,
        String name
) {}
