package net.kaaass.kflight.controller;

import net.kaaass.kflight.data.FlightManager;
import net.kaaass.kflight.data.TicketManager;
import net.kaaass.kflight.data.entry.EntryTicketOrder;
import net.kaaass.kflight.exception.BadRequestException;
import net.kaaass.kflight.exception.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    @PostMapping("/order/{flightNo}/")
    public EntryTicketOrder orderByFlightNo(@PathVariable String flightNo,
                                            @RequestParam String phone) throws NotFoundException, BadRequestException {
        var flight = FlightManager.findByFlightNo(flightNo)
                .orElseThrow(() -> new NotFoundException("未找到此航班！"));
        var order = new EntryTicketOrder(flight, phone);
        return TicketManager.orderTicket(order);
    }

    @PostMapping("/order/id/{id}/")
    public EntryTicketOrder orderById(@PathVariable String id,
                                      @RequestParam String phone) throws NotFoundException, BadRequestException {
        var flight = FlightManager.getById(Integer.parseInt(id))
                .orElseThrow(() -> new NotFoundException("未找到此航班！"));
        var order = new EntryTicketOrder(flight, phone);
        return TicketManager.orderTicket(order);
    }

    @PostMapping("/withdraw/{flightNo}/")
    public void withdrawByFlightNo(@PathVariable String flightNo,
                                   @RequestParam String phone) throws NotFoundException, BadRequestException {
        var flight = FlightManager.findByFlightNo(flightNo)
                .orElseThrow(() -> new NotFoundException("未找到此航班！"));
        TicketManager.withdrawTicket(flight, phone);
    }

    @PostMapping("/withdraw/id/{id}/")
    public void withdrawById(@PathVariable String id,
                             @RequestParam String phone) throws NotFoundException, BadRequestException {
        var flight = FlightManager.getById(Integer.parseInt(id))
                .orElseThrow(() -> new NotFoundException("未找到此航班！"));
        TicketManager.withdrawTicket(flight, phone);
    }

    @GetMapping("/queue/")
    public List<EntryTicketOrder> withdrawById() {
        return TicketManager.getTicketsInQueue();
    }
}
