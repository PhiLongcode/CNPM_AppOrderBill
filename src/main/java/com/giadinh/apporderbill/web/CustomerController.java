package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.customer.model.Customer;
import com.giadinh.apporderbill.customer.usecase.CustomerUseCases;
import com.giadinh.apporderbill.web.security.ApiAuthorizationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Customer management and loyalty points")
public class CustomerController {
    private final CustomerUseCases customerUseCases;
    private final ApiAuthorizationService authorizationService;

    public CustomerController(CustomerUseCases customerUseCases, ApiAuthorizationService authorizationService) {
        this.customerUseCases = customerUseCases;
        this.authorizationService = authorizationService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAll(@RequestHeader(value = "X-Username", required = false) String username,
                                                 @RequestParam(required = false) String keyword) {
        authorizationService.requireView(username, "Manage Customers");
        return ResponseEntity.ok(customerUseCases.getAll(keyword));
    }

    @PostMapping
    public ResponseEntity<Customer> create(@RequestHeader(value = "X-Username", required = false) String username,
                                           @RequestBody CustomerUpsertRequest request) {
        authorizationService.requireOperate(username, "Manage Customers");
        Customer customer = customerUseCases.create(request.getName(), request.getPhone(), request.getPoints());
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@RequestHeader(value = "X-Username", required = false) String username,
                                           @PathVariable Long id, @RequestBody CustomerUpsertRequest request) {
        authorizationService.requireOperate(username, "Manage Customers");
        return ResponseEntity.ok(customerUseCases.update(id, request.getName(), request.getPhone(), request.getPoints()));
    }

    @PostMapping("/points")
    public ResponseEntity<Customer> addPoints(@RequestHeader(value = "X-Username", required = false) String username,
                                              @RequestBody AddPointsRequest request) {
        authorizationService.requireOperate(username, "Manage Customers");
        return ResponseEntity.ok(customerUseCases.addPointsByPhone(request.getPhone(), request.getPointsToAdd()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader(value = "X-Username", required = false) String username,
                                       @PathVariable Long id) {
        authorizationService.requireOperate(username, "Manage Customers");
        customerUseCases.delete(id);
        return ResponseEntity.noContent().build();
    }

    public static class CustomerUpsertRequest {
        private String name;
        private String phone;
        private int points;

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public int getPoints() {
            return points;
        }
    }

    public static class AddPointsRequest {
        private String phone;
        private int pointsToAdd;

        public String getPhone() {
            return phone;
        }

        public int getPointsToAdd() {
            return pointsToAdd;
        }
    }
}
