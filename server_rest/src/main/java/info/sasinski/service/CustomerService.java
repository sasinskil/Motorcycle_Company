package info.sasinski.service;

import info.sasinski.entity.Customer;
import info.sasinski.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor

@Service
public class CustomerService {

    final CustomerRepository _customerRepository;

    public Customer findCustomerById(long id) {
        return _customerRepository.findById(id).orElse(null);
    }

    public List<Customer> findAll() {
        return _customerRepository.findAll();
    }

    public long countCustomers() { return _customerRepository.count(); }

    public void removeCustomer(long id) {
        _customerRepository.deleteById(id);
    }

    public void saveCustomer(Customer customer) {
        _customerRepository.save(customer);
    }
}
