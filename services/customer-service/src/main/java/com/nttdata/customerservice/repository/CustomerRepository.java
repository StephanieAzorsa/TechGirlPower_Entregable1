package com.nttdata.customerservice.repository;

import com.nttdata.customerservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    boolean existsByDni(String dni);

    boolean existsByDniAndIdNot(String dni, String id);

}

