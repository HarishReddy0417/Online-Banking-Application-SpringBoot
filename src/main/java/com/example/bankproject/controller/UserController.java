package com.example.bankproject.controller;


import java.sql.SQLException;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.bankproject.entities.ForgotPasswordForm;
import com.example.bankproject.entities.User;
import com.example.bankproject.repositorys.UserRepository;
import com.example.bankproject.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.sql.Connection;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

   
    @Autowired
    private UserRepository userRepository;

    private Connection connection;
    
    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/home")
    public String home(){
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        userService.save(user);
        return "redirect:/login";
    }

   

    @GetMapping("/login")
    public String login(HttpServletRequest request) throws SQLException {
        if (connection == null || connection.isClosed()) {
           
        }
        
        return "login";
    }
    


    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                         @RequestParam("password") String password, 
                         @RequestParam("role") String role,
                         Model model){

        if (userService.isValidUser(email, password,role)) {
            if (role.equals("user")) {
                return "userpage";
            } else if (role.equals("admin")) {
                return "admin";
            }
        }
        else{
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
        return "login";
         
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        HttpSession session = request.getSession(false); 
        if (session != null) {
            session.invalidate(); 
        }
        if (connection != null && !connection.isClosed()) {
            connection.close(); 
        }
        return "redirect:/login?logout"; 
    }
    
    
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("forgotPasswordForm", new ForgotPasswordForm());
        return "forgot-password-form";
    }
    
    @PostMapping("/forgot-password")
    public String processForgotPasswordForm(@ModelAttribute("forgotPasswordForm") @Valid ForgotPasswordForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "forgot-password-form";
        }
    
        String email = form.getEmail();
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "forgot-password-form";
        }
    
        model.addAttribute("user", user);
        return "password-retrieval";
    }
    

    @GetMapping("/users")
    public String listUsers(Model model){
        model.addAttribute("users", userService.getAllUsers());
        return "userList";
    }

    @GetMapping("/users/{id}")
    public String getUserById(@PathVariable("id") Long id, Model model){
        User user =userService.getUserById(id);
        model.addAttribute("user", user);
        return "user";
    }
    //new added
    @GetMapping("/users/new")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "create_user";
    }

    //new added

    @PostMapping("/users")
    public String saveUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "create_user";
        }
        userService.save(user);
        return "redirect:/users";
    }


    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "editUser"; 
    }

    @PostMapping("/users/edit{id}")
    public String updateUser( @PathVariable  Long id ,@Valid @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "editUser";
        }
        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) 
    {
        userService.deleteUserById(id);
        return "redirect:/users" ;
    }

    @GetMapping("/search")
    public String searchAccount(@RequestParam("username") String username, Model model) {
    List<User> users = userRepository.findByUsername(username);
    model.addAttribute("users", users);
    return "userList";
}




}
    





