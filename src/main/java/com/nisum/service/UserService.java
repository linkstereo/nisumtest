package com.nisum.service;

import com.nisum.exception.BadRequestException;
import com.nisum.exception.EmailExistException;
import com.nisum.exception.InvalidEmailException;
import com.nisum.exception.UserNotExistException;
import com.nisum.model.BearerToken;
import com.nisum.model.LoginDto;
import com.nisum.model.Phone;
import com.nisum.model.Role;
import com.nisum.model.RoleName;
import com.nisum.model.User;
import com.nisum.repository.PhoneRepository;
import com.nisum.repository.RoleRepository;
import com.nisum.repository.UserRepository;
import com.nisum.security.JwtUtilities;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtilities jwtUtilities;


    public List<User> getAll() {
        return userRepository.findAll();
    }

    private List<Phone> getPhones(User user) {
        return phoneRepository.findAllByUser(user);
    }

    public BearerToken register(User user) throws EmailExistException, BadRequestException, InvalidEmailException {
        var phones = user.getPhones();
        var exist = userRepository.findByEmail(user.getEmail());

        if ( isBlank(user.getEmail()))
            throw new BadRequestException("El correo no puede ser nulo o vacio");

        if (!EmailValidator.getInstance().isValid(user.getEmail()))
            throw new InvalidEmailException();

        if (exist.isPresent()){
            throw new EmailExistException();
        } else {
            user.setCreated(new Date());
        }

        Role role = roleRepository.findByRoleName(RoleName.USER);
        user.setRoles(new ArrayList<>(Arrays.asList(role)));

        var encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        user = userRepository.save(user);
        updatePhones(phones,user);
        user.setPhones(phones);
        userRepository.save(user);

        String token = jwtUtilities.generateToken(user.getEmail(),Collections.singletonList(role.getRoleName()));
        return new BearerToken(token , "Bearer ");
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

            var encodedPassword = passwordEncoder.encode(savedUser.get().getPassword());
            user.setPassword(encodedPassword);
            user.setId(savedUser.get().getId());
            user.setCreated(savedUser.get().getCreated());
            user.setActive(savedUser.get().isActive());
            user.setLastLogin(savedUser.get().getLastLogin());
            user.setRoles(savedUser.get().getRoles());
        }

        updatePhones(phones,user);

        user.setModified(new Date());
        user.setPhones(phones);

        return userRepository.save(user);
    }

    private Optional<User> getSavedUser(User user) throws BadRequestException, InvalidEmailException {
        if ( isNull(user.getId()) && isBlank(user.getEmail()))
            throw new BadRequestException("Amos campos (id y email) no pueder ser null o vacio");

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


    public BearerToken authenticate(LoginDto loginDto) {
        Authentication authentication= authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User savedUser = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<String> rolesNames = new ArrayList<>();
        savedUser.getRoles().forEach(r-> rolesNames.add(r.getRoleName()));
        String token = jwtUtilities.generateToken(savedUser.getEmail(),rolesNames);
        return new BearerToken(token , "Bearer ");
    }
}
