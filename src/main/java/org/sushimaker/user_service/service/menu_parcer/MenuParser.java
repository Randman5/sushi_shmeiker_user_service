package org.sushimaker.user_service.service.menu_parcer;

import org.sushimaker.user_service.dto.menu.MenuCategory;

import java.util.List;

public interface MenuParser {

    List<MenuCategory> parseMenu();
}
