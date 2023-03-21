package com.nisum.service;

import com.nisum.exception.BadRequestException;
import com.nisum.exception.EmailExistException;
import com.nisum.exception.InvalidEmailException;
import com.nisum.exception.UserNotExistException;
import com.nisum.model.Phone;
import com.nisum.model.User;
import com.nisum.repository.PhoneRepository;
import com.nisum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PhoneRepository phoneRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    private List<Phone> getPhones(User user) {
        return phoneRepository.findAllByUser(user);
    }

    public User createUser(final User user) throws EmailExistException, BadRequestException, InvalidEmailException {
        var phones = user.getPhones();
        var exist = userRepository.findByEmail(user.getEmail());

        if ( isBlank(user.getEmail()))
            throw new BadRequestException("Email cannot be null or empty");

        if (!EmailValidator.getInstance().isValid(user.getEmail()))
            throw new InvalidEmailException();

        if (exist.isPresent()){
            throw new EmailExistException();
        } else {
            user.setCreated(new Date());
        }

        userRepository.save(user);
        updatePhones(phones,user);
        user.setPhones(phones);

        return userRepository.save(user);
    }

    public User updateUser(User user) throws UserNotExistException, BadRequestException, InvalidEmailException {

        var phones = user.getPhones();
        var savedUser = getSavedUser(user);

        if (savedUser.isEmpty()){
            throw new UserNotExistException();
        }  else {
            phones.addAll(savedUser.get().getPhones());

            if (isBlank(user.getName())){
                user.setName(savedUser.get().getName());
            }

            user.setId(savedUser.get().getId());
            user.setCreated(savedUser.get().getCreated());
            user.setActive(savedUser.get().isActive());
            user.setLastLogin(savedUser.get().getLastLogin());
        }

        updatePhones(phones,user);

        user.setModified(new Date());
        user.setPhones(phones);

        return userRepository.save(user);
    }

    private Optional<User> getSavedUser(User user) throws BadRequestException, InvalidEmailException {
        if ( isNull(user.getId()) && isBlank(user.getEmail()))
            throw new BadRequestException("Both fields id and email cannot be empty or null");

        if (!EmailValidator.getInstance().isValid(user.getEmail()))
            throw new InvalidEmailException();

        Optional<User> savedUser = null;
        if (!isNull(user.getId()))
            savedUser = userRepository.findById(user.getId());
        else if (!isBlank(user.getEmail()))
            savedUser = userRepository.findByEmail(user.getEmail());
        else
            savedUser = Optional.empty();

        return savedUser;
    }

    private Iterable<Phone> updatePhones(Set<Phone> phones, User user){
        phones.stream().forEach( p -> p.setUser(user));
        return phoneRepository.saveAll(phones);
    }


}
