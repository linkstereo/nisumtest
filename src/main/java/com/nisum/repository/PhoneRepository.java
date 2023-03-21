package com.nisum.repository;

import com.nisum.model.Phone;
import com.nisum.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PhoneRepository extends CrudRepository<Phone, String> {

    List<Phone> findAllByUser(User user);
}
