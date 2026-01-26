package org.sushimaker.user_service.service.menu_parcer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.sushimaker.user_service.dto.menu.MenuCategory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MenuParserImpl implements MenuParser {

    @Value("${site.address}")
    String address;

    private static final Pattern URL_PATTERN = Pattern.compile("url\\(([^)]+)\\)");

    @Override
    public List<MenuCategory> parseMenu() {
        try {
//            Document doc = Jsoup.connect(address).get();
//            Elements menu = Objects.requireNonNull(doc.getElementsByClass("small_menu").first()).getElementsByTag("a");
//            ArrayList<String> list = new ArrayList<>(3);
//            for (Element menuLink : menu) {
//                String href = menuLink.attr("href");
//                String text = menuLink.text();
//                list.add(text + "-" + href);
//            }
            return parseCategories();
        } catch (IOException ex) {
            return List.of(/*"parsing error"*/);
        }
    }

    private List<MenuCategory> parseCategories() throws IOException {
        Document screen = fetchDocument();
        return extractCategoryElements(screen).stream()
                .map(this::convertToMenuCategory)
                .toList();
    }

    private Document fetchDocument() throws IOException {
        return Jsoup.connect(address).get();
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
        String style = Objects.requireNonNull(element.selectFirst(".img")).attr("style");
        Matcher matcher = URL_PATTERN.matcher(style);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractCategoryName(Element element) {
        return Objects.requireNonNull(element.selectFirst(".elem_header")).text();
    }

    private String resolveUrl(String path) {
        return path != null ? address + path : null;
    }


}
