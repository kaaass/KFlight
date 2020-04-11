package net.kaaass.kflight.controller;

import net.kaaass.kflight.exception.NotFoundException;
import net.kaaass.kflight.service.CityService;
import net.kaaass.kflight.service.PlanService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/plan")
public class PlanController {

    @GetMapping("/")
    List<PlanService.FlightPlan> plan(@RequestParam String from,
                                      @RequestParam String to,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) throws NotFoundException {
        var fromCity = CityService.findByName(from)
                .orElseThrow(() -> new NotFoundException("城市不存在！"));
        var toCity = CityService.findByName(to)
                .orElseThrow(() -> new NotFoundException("城市不存在！"));
        return PlanService.plan(fromCity, toCity, date);
    }
}
