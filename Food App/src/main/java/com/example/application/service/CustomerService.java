package com.example.application.service;

import com.example.application.data.entity.user.Customer;
import com.example.application.data.repository.customerRepository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;


    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findCustomer(Integer userId) {
        return customerRepository.getById(userId);
    }

    public void deleteCustomer(Customer customer) {
        customerRepository.delete(customer);
    }

    public List<Customer> findAll(){
        return customerRepository.findAll();
    }



    public void saveCustomer(Customer customer) { //static
        if (customer == null) {
            System.err.println("Customer is NULL");
            return;
        }
        customerRepository.save(customer);
    }
}
