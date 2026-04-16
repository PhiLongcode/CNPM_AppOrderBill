package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.customer.model.Customer;
import com.giadinh.apporderbill.customer.model.LoyaltyConfig;
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

    @GetMapping("/loyalty-config")
    public ResponseEntity<LoyaltyConfigResponse> getLoyaltyConfig(
            @RequestHeader(value = "X-Username", required = false) String username) {
        authorizationService.requireView(username, "Manage Loyalty Config");
        LoyaltyConfig config = customerUseCases.reloadLoyaltyConfig();
        return ResponseEntity.ok(LoyaltyConfigResponse.from(config));
    }

    @PutMapping("/loyalty-config")
    public ResponseEntity<LoyaltyConfigResponse> updateLoyaltyConfig(
            @RequestHeader(value = "X-Username", required = false) String username,
            @RequestBody LoyaltyConfigRequest request) {
        authorizationService.requireOperate(username, "Manage Loyalty Config");
        LoyaltyConfig updated = customerUseCases.updateLoyaltyConfig(
                new LoyaltyConfig(
                        request.getEarnUnitAmount(),
                        request.getPointsPerUnit(),
                        request.getRedeemPointsRequired(),
                        request.getRedeemValue()
                )
        );
        return ResponseEntity.ok(LoyaltyConfigResponse.from(updated));
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

    public static class LoyaltyConfigRequest {
        private long earnUnitAmount;
        private int pointsPerUnit;
        private int redeemPointsRequired;
        private long redeemValue;

        public long getEarnUnitAmount() {
            return earnUnitAmount;
        }

        public int getPointsPerUnit() {
            return pointsPerUnit;
        }

        public int getRedeemPointsRequired() {
            return redeemPointsRequired;
        }

        public long getRedeemValue() {
            return redeemValue;
        }
    }

    public static class LoyaltyConfigResponse {
        private final long earnUnitAmount;
        private final int pointsPerUnit;
        private final int redeemPointsRequired;
        private final long redeemValue;

        private LoyaltyConfigResponse(long earnUnitAmount, int pointsPerUnit, int redeemPointsRequired, long redeemValue) {
            this.earnUnitAmount = earnUnitAmount;
            this.pointsPerUnit = pointsPerUnit;
            this.redeemPointsRequired = redeemPointsRequired;
            this.redeemValue = redeemValue;
        }

        public static LoyaltyConfigResponse from(LoyaltyConfig config) {
            return new LoyaltyConfigResponse(
                    config.getEarnUnitAmount(),
                    config.getPointsPerUnit(),
                    config.getRedeemPointsRequired(),
                    config.getRedeemValue()
            );
        }

        public long getEarnUnitAmount() {
            return earnUnitAmount;
        }

        public int getPointsPerUnit() {
            return pointsPerUnit;
        }

        public int getRedeemPointsRequired() {
            return redeemPointsRequired;
        }

        public long getRedeemValue() {
            return redeemValue;
        }
    }
}
