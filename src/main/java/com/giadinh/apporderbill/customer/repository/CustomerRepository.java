package com.giadinh.apporderbill.customer.repository;

import com.giadinh.apporderbill.customer.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    List<Customer> findAll();
    Optional<Customer> findById(Long id);
    Optional<Customer> findByPhone(String phone);
    Customer save(Customer customer);
    void delete(Long id);
}

