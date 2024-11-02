package ru.itmentor.spring.boot_security.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmentor.spring.boot_security.demo.Service.UserService;
import ru.itmentor.spring.boot_security.demo.Util.UserErrorResponse;
import ru.itmentor.spring.boot_security.demo.Util.UserNotFoundExeption;
import ru.itmentor.spring.boot_security.demo.models.User;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping()
    public User getCurrentUser() throws JsonProcessingException {
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User curentUser = userService.findOne(user.getId());
        return curentUser;
    }
    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleExeption(UserNotFoundExeption e){
        UserErrorResponse response =new UserErrorResponse("User с этим id не найден",
                System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
