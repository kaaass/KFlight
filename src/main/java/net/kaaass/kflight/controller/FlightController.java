package net.kaaass.kflight.controller;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.kflight.data.DataLoader;
import net.kaaass.kflight.data.entry.EntryFlight;
import net.kaaass.kflight.exception.BadRequestException;
import net.kaaass.kflight.exception.NotFoundException;
import net.kaaass.kflight.service.FlightService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/flight")
public class FlightController {

    @GetMapping("/")
    List<EntryFlight> getAll() {
        return FlightService.getAll();
    }

    @PostMapping("/")
    EntryFlight addFlight(@RequestBody EntryFlight flight) {
        FlightService.addEntry(flight);
        return flight;
    }

    @GetMapping("/id/{id}/")
    EntryFlight getFlightById(@PathVariable int id) throws NotFoundException {
        return FlightService.getById(id)
                .orElseThrow(() -> new NotFoundException("未找到此ID的航班信息！"));
    }

    @GetMapping("/{flightNo}/")
    EntryFlight getFlightByNo(@PathVariable String flightNo) throws NotFoundException {
        return FlightService.findByFlightNo(flightNo)
                .orElseThrow(() -> new NotFoundException("航班不存在"));
    }

    @PostMapping("/id/{id}/")
    EntryFlight editFlight(@PathVariable int id, @RequestBody EntryFlight flight) throws NotFoundException {
        FlightService.updateById(id, flight);
        return flight;
    }

    @DeleteMapping("/id/{id}/")
    void removeFlight(@PathVariable int id) throws NotFoundException {
        var flight = FlightService.getById(id)
                .orElseThrow(() -> new NotFoundException("未找到此ID的航班信息！"));
        FlightService.removeEntry(flight);
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

    @PostMapping("/id/{id}/delay/")
    EntryFlight publishDelayById(@PathVariable int id,
                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime delayTo) throws NotFoundException {
        return FlightService.changeState(id, EntryFlight.State.DELAYED, delayTo);
    }

    @PostMapping("/id/{id}/cancel/")
    EntryFlight publishCancelById(@PathVariable int id) throws NotFoundException {
        return FlightService.changeState(id, EntryFlight.State.CANCELED, null);
    }
}
