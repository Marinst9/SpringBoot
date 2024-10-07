package com.luv2code.demo.controller;


import com.luv2code.demo.entity.Users;
import com.luv2code.demo.entity.UsersType;
import com.luv2code.demo.services.UsersService;
import com.luv2code.demo.services.UsersTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class UsersController {

    private final UsersTypeService usersTypeService;

    private final UsersService usersService;

    @Autowired
    public UsersController(UsersTypeService usersTypeService, UsersService usersService) {
        this.usersTypeService = usersTypeService;
        this.usersService = usersService;
    }

    @GetMapping("/register")
    public String register(Model model){
        List<UsersType> usersTypes = usersTypeService.getAll();
        model.addAttribute("getAllTypes", usersTypes);
        model.addAttribute("user", new Users());
        return "register";
    }


    @PostMapping("/register/new")
    public String userRegistration(@Valid Users users){
        usersService.addNew(users);
        return "redirect:/dashboard/";
    }

//    @PostMapping("/register/new")
//    public String userRegistration(@Valid Users users, Model model){
//
//        Optional<Users> optionalUsers = usersService.getUserByEmail(users.getEmail());
//        if (optionalUsers.isPresent()) {
//            model.addAttribute("error", "Email Already registered," +
//                    " try to login or register with other email.");
//            List<UsersType> usersTypes = usersTypeService.getAll();
//            model.addAttribute("getAllTypes", usersTypes);
//            model.addAttribute("user", new Users());
//            return "register";
//        }
////        System.out.println("User:: "+users);
//        usersService.addNew(users);
//        return "dashboard";
//    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        // redirect to homepage
        return "redirect:/";
    }
}
