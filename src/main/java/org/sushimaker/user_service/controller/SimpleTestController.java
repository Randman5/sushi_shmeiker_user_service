package org.sushimaker.user_service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sushimaker.user_service.dto.menu.MenuCategory;
import org.sushimaker.user_service.dto.menu.MenuItem;
import org.sushimaker.user_service.service.menu_parcer.MenuParser;

import java.util.HashMap;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class SimpleTestController {

    private final MenuParser parser;

    @GetMapping("/test")
    public ResponseEntity<?> getTestText() {
        log.info("GET /api/v1/test called");
        try {
            HashMap<MenuCategory, List<MenuItem>> menu = parser.parseMenu();
            return new ResponseEntity<>(menu, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
