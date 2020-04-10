package net.kaaass.kflight.controller;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.kflight.data.DataLoader;
import net.kaaass.kflight.data.FlightManager;
import net.kaaass.kflight.data.entry.EntryFlight;
import net.kaaass.kflight.exception.BadRequestException;
import net.kaaass.kflight.exception.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/flight")
public class FlightController {

    @GetMapping("/")
    List<EntryFlight> getAll() {
        return FlightManager.getAll();
    }

    @PostMapping("/")
    EntryFlight addFlight(@RequestBody EntryFlight flight) {
        FlightManager.addEntry(flight);
        return flight;
    }

    @GetMapping("/id/{id}/")
    EntryFlight getFlightById(@PathVariable int id) throws NotFoundException {
        try {
            return FlightManager.getById(id);
        } catch (IndexOutOfBoundsException e) {
            throw new NotFoundException("航班ID不存在");
        }
    }

    @PostMapping("/id/{id}/")
    EntryFlight editFlight(@PathVariable int id, @RequestBody EntryFlight flight) throws NotFoundException {
        try {
            FlightManager.updateById(id, flight);
        } catch (IndexOutOfBoundsException e) {
            throw new NotFoundException("航班ID不存在");
        }
        return flight;
    }

    @DeleteMapping("/id/{id}/")
    void removeFlight(@PathVariable int id) throws NotFoundException {
        try {
            FlightManager.removeEntry(FlightManager.getById(id));
        } catch (IndexOutOfBoundsException e) {
            throw new NotFoundException("航班ID不存在");
        }
    }

    @GetMapping("/import/")
    void getFlightById(@RequestParam String filepath) throws BadRequestException {
        try {
            DataLoader.loadFlightFromJsonFile(filepath);
        } catch (IOException e) {
            log.warn("打开文件失败", e);
            throw new BadRequestException("文件打开失败！请检查文件是否存在。");
        }
    }
}
