
package com.example.bankproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bankproject.entities.User;
import com.example.bankproject.repositorys.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
 
       public User findByEmail(String email) {
           return userRepository.findByEmail(email);
       }

       public boolean isValidUser(String email, String password, String role) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password) && user.getRole().equals(role)) {
            return true;
        }
        return false;
    }
       public User save(User user) {
           return userRepository.save(user);
       }
       

       public List<User> getAllUsers(){
        return userRepository.findAll();
       }
       
       public User getUserById(Long id){
        return userRepository.findById(id)
        .orElseThrow(()-> new IllegalArgumentException("Invalid user Id:" + id));
       }
       
       public void deleteUserById(Long id)
       {
        userRepository.deleteById(id);
       }
}
