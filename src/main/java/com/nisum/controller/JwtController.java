package com.nisum.controller;

import com.nisum.exception.BadRequestException;
import com.nisum.exception.EmailExistException;
import com.nisum.exception.InvalidEmailException;
import com.nisum.exception.UserNotExistException;
import com.nisum.model.BearerToken;
import com.nisum.model.LoginDto;
import com.nisum.model.User;
import com.nisum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.nisum.controller.constants.RestConstants.BASE_API_VERSION;
import static com.nisum.controller.constants.RestConstants.JWT_URI_BASE;
import static com.nisum.controller.constants.RestConstants.USERS_URI_BASE;

@RestController()
@RequestMapping("/"+BASE_API_VERSION+"/")
public class JwtController {

    @Autowired
    private UserService userService;

    @PostMapping(JWT_URI_BASE+"/register")
    public BearerToken register(@RequestBody User user) throws EmailExistException, BadRequestException, InvalidEmailException {
        return userService.register(user);
    }

    @PostMapping(JWT_URI_BASE+"/authenticate")
    public BearerToken authenticate(@RequestBody LoginDto login) {
        return userService.authenticate(login);
    }

}
