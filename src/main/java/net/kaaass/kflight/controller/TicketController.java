package net.kaaass.kflight.controller;

import net.kaaass.kflight.data.entry.EntryTicketOrder;
import net.kaaass.kflight.exception.BadRequestException;
import net.kaaass.kflight.exception.NotFoundException;
import net.kaaass.kflight.service.FlightService;
import net.kaaass.kflight.service.TicketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    @PostMapping("/order/{flightNo}/")
    public EntryTicketOrder orderByFlightNo(@PathVariable String flightNo,
                                            @RequestParam String phone) throws NotFoundException, BadRequestException {
        var flight = FlightService.findByFlightNo(flightNo)
                .orElseThrow(() -> new NotFoundException("未找到此航班！"));
        var order = new EntryTicketOrder(flight, phone);
        return TicketService.orderTicket(order);
    }

    @PostMapping("/order/id/{id}/")
    public EntryTicketOrder orderById(@PathVariable int id,
                                      @RequestParam String phone) throws NotFoundException, BadRequestException {
        var flight = FlightService.getById(id)
                .orElseThrow(() -> new NotFoundException("未找到此航班！"));
        var order = new EntryTicketOrder(flight, phone);
        return TicketService.orderTicket(order);
    }

    @PostMapping("/withdraw/{flightNo}/")
    public void withdrawByFlightNo(@PathVariable String flightNo,
                                   @RequestParam String phone) throws NotFoundException, BadRequestException {
        var flight = FlightService.findByFlightNo(flightNo)
                .orElseThrow(() -> new NotFoundException("未找到此航班！"));
        TicketService.withdrawTicket(flight, phone);
    }

    @PostMapping("/withdraw/id/{id}/")
    public void withdrawById(@PathVariable int id,
                             @RequestParam String phone) throws NotFoundException, BadRequestException {
        var flight = FlightService.getById(id)
                .orElseThrow(() -> new NotFoundException("未找到此航班！"));
        TicketService.withdrawTicket(flight, phone);
    }

    @GetMapping("/queue/")
    public List<EntryTicketOrder> withdrawById() {
        return TicketService.getTicketsInQueue();
    }
}
