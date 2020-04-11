package net.kaaass.kflight.controller;

import net.kaaass.kflight.data.CityManager;
import net.kaaass.kflight.data.FlightManager;
import net.kaaass.kflight.data.Sorter;
import net.kaaass.kflight.data.entry.EntryFlight;
import net.kaaass.kflight.exception.BadRequestException;
import net.kaaass.kflight.exception.NotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @GetMapping("/from-to-date/")
    public List<EntryFlight> findAllByFromToAndDate(@RequestParam String from,
                                                    @RequestParam String to,
                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                    @RequestParam(defaultValue = "dpR") String sort)
            throws NotFoundException, BadRequestException {
        var fromCity = CityManager.findByName(from)
                .orElseThrow(() -> new NotFoundException("城市不存在！"));
        var toCity = CityManager.findByName(to)
                .orElseThrow(() -> new NotFoundException("城市不存在！"));
        var result = FlightManager.findAllByFromToAndDate(fromCity, toCity, date);
        Sorter.sortFlight(result, sort);
        return result;
    }

    @GetMapping("/between/")
    public List<EntryFlight> findBetween(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                         @RequestParam(defaultValue = "dpR") String sort)
            throws BadRequestException {
        var result = FlightManager.findBetween(start, end);
        Sorter.sortFlight(result, sort);
        return result;
    }

    @GetMapping("/from-date/")
    public List<EntryFlight> findAllByFromAndDate(@RequestParam String from,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                  @RequestParam(defaultValue = "dpR") String sort)
            throws NotFoundException, BadRequestException {
        var fromCity = CityManager.findByName(from)
                .orElseThrow(() -> new NotFoundException("城市不存在！"));
        var result = FlightManager.findByFromAndDate(fromCity, date);
        Sorter.sortFlight(result, sort);
        return result;
    }

    @GetMapping("/to-date/")
    public List<EntryFlight> findAllByToAndDate(@RequestParam String to,
                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                @RequestParam(defaultValue = "dpR") String sort)
            throws NotFoundException, BadRequestException {
        var toCity = CityManager.findByName(to)
                .orElseThrow(() -> new NotFoundException("城市不存在！"));
        var result = FlightManager.findByToAndDate(toCity, date);
        Sorter.sortFlight(result, sort);
        return result;
    }
}
