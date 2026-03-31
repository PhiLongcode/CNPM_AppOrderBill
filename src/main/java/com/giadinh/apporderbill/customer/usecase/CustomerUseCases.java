package com.giadinh.apporderbill.customer.usecase;

import com.giadinh.apporderbill.customer.model.Customer;
import com.giadinh.apporderbill.customer.repository.CustomerRepository;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CustomerUseCases {
    private final CustomerRepository repository;

    public CustomerUseCases(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<Customer> getAll(String keyword) {
        String k = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return repository.findAll().stream()
                .filter(c -> k.isEmpty()
                        || (c.getName() != null && c.getName().toLowerCase(Locale.ROOT).contains(k))
                        || (c.getPhone() != null && c.getPhone().contains(k)))
                .collect(Collectors.toList());
    }

    public Customer create(String name, String phone, int points) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }
        if (repository.findByPhone(phone.trim()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại.");
        }
        return repository.save(new Customer(null, name, phone, points));
    }

    public Customer update(Long id, String name, String phone, int points) {
        Customer existing = repository.findById(id).orElseThrow();
        repository.findByPhone(phone).ifPresent(found -> {
            if (!found.getId().equals(id)) {
                throw new IllegalArgumentException("Số điện thoại đã tồn tại.");
            }
        });
        existing.setName(name);
        existing.setPhone(phone);
        existing.setPoints(points);
        return repository.save(existing);
    }

    public Customer addPointsByPhone(String phone, int pointsToAdd) {
        if (phone == null || phone.isBlank() || pointsToAdd <= 0) {
            return null;
        }
        Customer customer = repository.findByPhone(phone.trim())
                .orElseGet(() -> repository.save(new Customer(null, "Khách " + phone.trim(), phone.trim(), 0)));
        customer.setPoints(customer.getPoints() + pointsToAdd);
        return repository.save(customer);
    }

    public void delete(Long id) {
        repository.delete(id);
    }
}

