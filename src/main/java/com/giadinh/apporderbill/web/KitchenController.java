package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.kitchen.KitchenComponent;
import com.giadinh.apporderbill.kitchen.usecase.dto.*;
import com.giadinh.apporderbill.web.security.ApiAuthorizationService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho Kitchen feature.
 */
@RestController
@RequestMapping("/api/v1/kitchen")
@Tag(name = "Kitchen", description = "Kitchen ticket printing APIs")
public class KitchenController {

    private final KitchenComponent kitchenComponent;
    private final ApiAuthorizationService authorizationService;

    public KitchenController(KitchenComponent kitchenComponent, ApiAuthorizationService authorizationService) {
        this.kitchenComponent = kitchenComponent;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/ticket")
    public ResponseEntity<PrintKitchenTicketOutput> printKitchenTicket(@RequestHeader(value = "X-Username", required = false) String username,
                                                                       @RequestBody PrintKitchenTicketInput input) {
        authorizationService.requireView(username, "Print Kitchen Ticket");
        return ResponseEntity.ok(kitchenComponent.printKitchenTicket(input));
    }

    @PostMapping("/ticket/selected")
    public ResponseEntity<PrintKitchenTicketOutput> printSelectedItems(@RequestHeader(value = "X-Username", required = false) String username,
                                                                       @RequestBody PrintSelectedItemsInput input) {
        authorizationService.requireView(username, "Print Kitchen Ticket");
        return ResponseEntity.ok(kitchenComponent.printSelectedItems(input));
    }
}
