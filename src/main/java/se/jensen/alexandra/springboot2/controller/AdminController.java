package se.jensen.alexandra.springboot2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * En REST-controller som visar en sida för ADMIN-användare.
 * Alla anrop till denna controller börjar med /admin
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    /**
     * Returnerar en enkel text som representerar admin-sidan.
     *
     * @return "Admin page"
     */
    @GetMapping
    public String getAdminPage() {
        return "Admin page";
    }
}
