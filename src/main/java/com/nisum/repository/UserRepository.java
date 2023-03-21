package com.nisum.repository;

import com.nisum.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

    //List<User> findAllByEmail(String email);
    Optional<User> findByEmail(String email);

    @Override
    List<User> findAll();


}
