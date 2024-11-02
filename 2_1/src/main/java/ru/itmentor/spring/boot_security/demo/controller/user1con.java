package ru.itmentor.spring.boot_security.demo.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.itmentor.spring.boot_security.demo.Service.UserService;
import ru.itmentor.spring.boot_security.demo.models.User;

@Controller
@RequestMapping("/user")
public class user1con {

    private final UserService userService;

    public user1con(UserService userService) {
        this.userService = userService;
    }
    @GetMapping()
    public String show(Model model) {
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", userService.findOne(user.getId()));
        return "user/show";
    }
}
