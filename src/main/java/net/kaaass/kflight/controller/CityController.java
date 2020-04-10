package net.kaaass.kflight.controller;

import net.kaaass.kflight.data.CityManager;
import net.kaaass.kflight.data.entry.EntryCity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/city")
public class CityController {

    @GetMapping("/")
    List<EntryCity> getAll() {
        return CityManager.getAll();
    }
}
