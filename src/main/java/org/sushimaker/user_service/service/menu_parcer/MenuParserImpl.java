package org.sushimaker.user_service.service.menu_parcer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.sushimaker.user_service.dto.menu.MenuCategory;
import org.sushimaker.user_service.dto.menu.MenuItem;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MenuParserImpl implements MenuParser {

    @Value("${site.address}")
    private String address;

    private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("url\\(([^)]+)\\)");
    private static final Pattern CATEGORY_ID_PATTERN = Pattern.compile("[?&]catID=(\\d+)(?:&|$)");

    @Override
    public HashMap<MenuCategory, List<MenuItem>> parseMenu() {
        try {
            List<MenuCategory> categories = parseCategories();
            HashMap<MenuCategory, List<MenuItem>> menu = new HashMap<>();

            categories.forEach(category -> processCategory(category, menu));

            return menu;
        } catch (IOException ex) {
            return new HashMap<>();
        }
    }

    private void processCategory(MenuCategory category, HashMap<MenuCategory, List<MenuItem>> menu) {
        try {
            Document doc = fetchDocument(category.href());
            Elements menuItems = doc.select("main .catalog_pl.cpl_opener_right.cpl_opener_bottom");

            List<MenuItem> itemsOfCategory = menuItems.stream()
                    .map(this::convertToMenuItem)
                    .toList();

            menu.put(category, itemsOfCategory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process category: " + category, e);
        }
    }

    private MenuItem convertToMenuItem(Element menuItem) {
        return new MenuItem(
                extractName(menuItem),
                extractDataId(menuItem),
                extractCategoryId(menuItem),
                resolveUrl(extractImagePath(menuItem)),
                resolveUrl(extractDetailRef(menuItem)),
                extractDescription(menuItem),
                extractPrice(menuItem)
        );
    }

    private String extractName(Element menuItem) {
        return menuItem.attr("data-title");
    }

    private String extractDataId(Element menuItem) {
        return menuItem.attr("data-id");
    }

    private String extractCategoryId(Element menuItem) {
        return Optional.ofNullable(menuItem.selectFirst(".cat_basket.button_sm.almaBasketAdd"))
                .map(element -> element.attr("href"))
                .map(String::trim)
                .map(this::parseCategoryIdFromUrl)
                .orElse("");
    }

    private String parseCategoryIdFromUrl(String url) {
        Matcher matcher = CATEGORY_ID_PATTERN.matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractImagePath(Element menuItem) {
        return Optional.ofNullable(menuItem.getElementsByTag("img").first())
                .map(element -> element.attr("src"))
                .orElseThrow(() -> new IllegalArgumentException("Image element not found"));
    }

    private String extractDetailRef(Element menuItem) {
        return Optional.ofNullable(menuItem.getElementsByClass("cpl_more").first())
                .map(element -> element.attr("href"))
                .orElseThrow(() -> new IllegalArgumentException("Detail reference element not found"));
    }

    private String extractDescription(Element menuItem) {
        return Optional.ofNullable(menuItem.getElementsByClass("cpl_name").first())
                .map(Element::text)
                .orElse("");
    }

    private String extractPrice(Element menuItem) {
        return Optional.ofNullable(menuItem.getElementsByClass("cpl_cost_new").first())
                .map(Element::text)
                .map(String::trim)
                .map(price -> price.replace("₽", "").trim())
                .filter(price -> !price.isEmpty())
                .orElse("0");
    }

    private List<MenuCategory> parseCategories() throws IOException {
        Document screen = fetchDocument(address);
        return extractCategoryElements(screen).stream()
                .map(this::convertToMenuCategory)
                .toList();
    }

    private Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    private List<Element> extractCategoryElements(Document document) {
        Element midderContainer = document.selectFirst(".midder");
        return midderContainer != null
                ? midderContainer.select(".elem")
                : List.of();
    }

    private MenuCategory convertToMenuCategory(Element categoryElement) {
        return new MenuCategory(
                resolveUrl(categoryElement.attr("href")),
                resolveUrl(extractBackgroundImage(categoryElement)),
                extractCategoryName(categoryElement)
        );
    }

    private String extractBackgroundImage(Element element) {
        String style = Optional.ofNullable(element.selectFirst(".img"))
                .map(img -> img.attr("style"))
                .orElse("");

        Matcher matcher = IMAGE_URL_PATTERN.matcher(style);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractCategoryName(Element element) {
        return Optional.ofNullable(element.selectFirst(".elem_header"))
                .map(Element::text)
                .orElse("");
    }

    private String resolveUrl(String path) {
        return path != null ? address + path : null;
    }
}
