package com.giadinh.apporderbill.customer.repository;

import com.giadinh.apporderbill.customer.model.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCustomerRepository implements CustomerRepository {
    private final ConcurrentHashMap<Long, Customer> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Customer> findByPhone(String phone) {
        if (phone == null) {
            return Optional.empty();
        }
        return store.values().stream()
                .filter(c -> phone.equals(c.getPhone()))
                .findFirst();
    }

    @Override
    public Customer save(Customer customer) {
        Long id = customer.getId();
        if (id == null || id <= 0) {
            id = seq.incrementAndGet();
            customer = new Customer(id, customer.getName(), customer.getPhone(), customer.getPoints());
        }
        store.put(id, customer);
        return customer;
    }

    @Override
    public void delete(Long id) {
        store.remove(id);
    }
}

