package com.nisum.controller;

import com.nisum.exception.BadRequestException;
import com.nisum.exception.EmailExistException;
import com.nisum.exception.InvalidEmailException;
import com.nisum.exception.UserNotExistException;
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
import static com.nisum.controller.constants.RestConstants.USERS_URI_BASE;

@RestController()
@RequestMapping("/"+BASE_API_VERSION+"/")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(USERS_URI_BASE)
    public List<User> getAll(){
        return userService.getAll();
    }

    @PostMapping(USERS_URI_BASE)
    public User createUser(@RequestBody User user) throws EmailExistException, BadRequestException, InvalidEmailException {
        return userService.createUser(user);
    }

    @PutMapping(USERS_URI_BASE)
    public User updateUser(@RequestBody User user) throws UserNotExistException, BadRequestException, InvalidEmailException {
        return userService.updateUser(user);
    }

}
