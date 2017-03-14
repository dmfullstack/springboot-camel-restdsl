package gr.iserm.java.spring.customer;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

import static java.util.Arrays.*;

@Component
public class CustomerTransformationService {

    public Collection<Customer> allCustomers() {
        return asList(randomCustomer(), randomCustomer());
    }

    public Customer echoCustomer(Customer customer) {
        return customer;
    }

    public Customer newCustomer() {
        return randomCustomer();
    }

    private Customer randomCustomer() {
        return new Customer(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

}
