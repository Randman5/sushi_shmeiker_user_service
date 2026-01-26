package org.sushimaker.user_service.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class SimpleTestController {

    @GetMapping("/test")
    private List<String> getTestText() {

        try {
            Document doc = Jsoup.connect("https://sushimeiker.ru/").get();
            Elements menu = Objects.requireNonNull(doc.getElementsByClass("small_menu").first()).getElementsByTag("a");
            ArrayList<String> list = new ArrayList<>(3);
            for (Element menuLink : menu) {
                String href = menuLink.attr("href");
                String text = menuLink.text();
                list.add(text + "-" + href);
            }
            return list;
        } catch (IOException ex) {
            return List.of("parsing error");
        }

//        return "test text просто тестовый текст";
    }
}
