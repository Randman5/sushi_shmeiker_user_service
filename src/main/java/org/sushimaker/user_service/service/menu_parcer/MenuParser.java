package org.sushimaker.user_service.service.menu_parcer;

import org.sushimaker.user_service.dto.menu.MenuCategory;
import org.sushimaker.user_service.dto.menu.MenuItem;

import java.util.HashMap;
import java.util.List;

public interface MenuParser {

    HashMap<MenuCategory, List<MenuItem>> parseMenu();
}
