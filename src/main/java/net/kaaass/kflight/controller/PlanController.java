package net.kaaass.kflight.controller;

import net.kaaass.kflight.data.CityManager;
import net.kaaass.kflight.data.Planner;
import net.kaaass.kflight.exception.NotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/plan")
public class PlanController {

    @GetMapping("/")
    List<Planner.FlightPlan> plan(@RequestParam String from,
                                  @RequestParam String to,
                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) throws NotFoundException {
        var fromCity = CityManager.findByName(from)
                .orElseThrow(() -> new NotFoundException("城市不存在！"));
        var toCity = CityManager.findByName(to)
                .orElseThrow(() -> new NotFoundException("城市不存在！"));
        return Planner.plan(fromCity, toCity, date);
    }
}
