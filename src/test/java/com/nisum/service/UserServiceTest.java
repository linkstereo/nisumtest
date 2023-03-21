package com.nisum.service;

import com.nisum.exception.BadRequestException;
import com.nisum.exception.EmailExistException;
import com.nisum.exception.InvalidEmailException;
import com.nisum.model.BearerToken;
import com.nisum.model.LoginDto;
import com.nisum.model.Phone;
import com.nisum.model.User;
import com.nisum.repository.PhoneRepository;
import com.nisum.repository.RoleRepository;
import com.nisum.repository.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUpTest() {
        phoneRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllTest() {
    }

    /**
     * Se realiza una prueba del camino feliz, todos los parametros son cargados correctamente en User
     * y la respuesta contiene el token de la sesion
     * @throws EmailExistException
     * @throws BadRequestException
     * @throws InvalidEmailException
     */
    @Test
    void registerOkTest() throws EmailExistException, BadRequestException, InvalidEmailException {
        BearerToken bearerToken = userService.register(generateUser());

        Assert.assertNotNull(bearerToken);
        Assert.assertTrue(bearerToken.getAccessToken().length() > 0);
        Assert.assertTrue(bearerToken.getTokenType().contains("Bearer "));
    }

    /**
     * Se usa un email que no cumple con el formato. Se espera una exception InvalidEmailException
     */
    @Test
    void registerInvalidEmailErrorTest() {
        User user = generateUser();
        user.setEmail("aaii.com");

        Assert.assertThrows(InvalidEmailException.class, () -> userService.register(user));
    }

    /**
     * Se intenta registrar un usario con un email que ya existe, el resultado debe ser un EmailExistException
     * @throws EmailExistException
     * @throws BadRequestException
     * @throws InvalidEmailException
     */
    @Test
    void registerEmailExistErrorTest() throws EmailExistException, BadRequestException, InvalidEmailException {
        User user = generateUser();
        userService.register(user);

        Assert.assertThrows(EmailExistException.class, () -> userService.register(user));
    }

    /**
     * Se intenta registrar un usuario con email vacio o nulo, el resultdo debe ser un BadRequestException
     * @throws EmailExistException
     * @throws BadRequestException
     * @throws InvalidEmailException
     */
    @Test
    void registerEmailEmptyOrNullTest() throws EmailExistException, BadRequestException, InvalidEmailException {
        User user = generateUser();

        user.setEmail("");
        Assert.assertThrows( "Email cannot be null or empty", BadRequestException.class, () -> userService.register(user));

        user.setEmail(null);
        Assert.assertThrows( "Email cannot be null or empty", BadRequestException.class, () -> userService.register(user));
    }

    @Test
    void updateUserTest() {
    }

    /**
     * Para poder autenticar correctamente el flujo que se deb seguir es:
     * 1) Registramos el usuario
     * 2) Autenticamos con las mismas credenciales
     * @throws EmailExistException
     * @throws BadRequestException
     * @throws InvalidEmailException
     */
    @Test
    void authenticateOkTest() throws EmailExistException, BadRequestException, InvalidEmailException {

        userService.register(generateUser());

        var bearerToken = userService.authenticate(generateLoginDto());

        Assert.assertNotNull(bearerToken);
        Assert.assertTrue(bearerToken.getAccessToken().length() > 0);
        Assert.assertTrue(bearerToken.getTokenType().contains("Bearer "));
    }

    private User generateUser(){
        User user = new User();
        user.setName("Andres Marin Castelblanco");
        user.setEmail("linkstereo@gmail.com");
        user.setPassword("hardpass1234");

        var phones = new HashSet<Phone>();
        var phone1 = new Phone();
        phone1.setNumber("56953185");
        phone1.setCityCode("11");
        phone1.setCountryCode("54");
        phones.add(phone1);
        user.setPhones(phones);

        return user;
    }

    private LoginDto generateLoginDto(){
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("linkstereo@gmail.com");
        loginDto.setPassword("hardpass1234");

        return loginDto;
    }
}