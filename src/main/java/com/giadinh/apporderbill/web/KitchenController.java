package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.kitchen.KitchenComponent;
import com.giadinh.apporderbill.kitchen.usecase.dto.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho Kitchen feature.
 */
@RestController
@RequestMapping("/api/kitchen")
public class KitchenController {

    private final KitchenComponent kitchenComponent;

    public KitchenController(KitchenComponent kitchenComponent) {
        this.kitchenComponent = kitchenComponent;
    }

    @PostMapping("/ticket")
    public ResponseEntity<PrintKitchenTicketOutput> printKitchenTicket(@RequestBody PrintKitchenTicketInput input) {
        return ResponseEntity.ok(kitchenComponent.printKitchenTicket(input));
    }

    @PostMapping("/ticket/selected")
    public ResponseEntity<PrintKitchenTicketOutput> printSelectedItems(@RequestBody PrintSelectedItemsInput input) {
        return ResponseEntity.ok(kitchenComponent.printSelectedItems(input));
    }
}
