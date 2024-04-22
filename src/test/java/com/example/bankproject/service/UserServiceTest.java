package com.example.bankproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import com.example.bankproject.entities.User;
import com.example.bankproject.repositorys.UserRepository;


@TestPropertySource("/application-test.properties")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Order(5)
    @Test
    public void testFindByEmail() {
        User user =new User(1L,"harish","harish@gmail.com","harish0417","user");
        String email = user.getEmail();
        User mockUser = new User();
        mockUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        User foundUser = userService.findByEmail(email);
        assertEquals(mockUser, foundUser);
       
    }
    @Order(6)
    @Test
    public void testFindByEmailNotFound() {
        User user =new User(1L,"harish","harish@gmail.com","harish0417","user");
        String email = user.getEmail();
        User mockUser = new User();
        mockUser.setEmail("xyz@gmail.com");
        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        User foundUser = userService.findByEmail(email);
       assertEquals(mockUser, foundUser);
    }

    @Order(7)
    @Test
    public void testIsValidUser() {
        String email = "harish@gmail.com";
        String password = "harish0417";
        String role = "user";
        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword(password);
        mockUser.setRole(role);
        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        boolean isValid = userService.isValidUser(email, password, role);
        assertTrue(isValid);
    }
    @Order(8)
    @Test
    public void testIsValidUserWithIncorrectPassword() {
        String email = "harish@gmail.com";
        String password = "harish0417";
        String role = "user";
        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword("harish"); 
        mockUser.setRole(role);
        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        boolean isValid = userService.isValidUser(email, password, role);
        assertFalse(isValid); 
    }

    @Order(1)
    @Test
    public void testSaveUser() {

       User user=new User();
       user.setUsername("Harish");
       user.setEmail("harish@gmail.com");
       user.setPassword("harish0417");
       user.setRole("user");

       User mockUser=new User();
       mockUser.setId(1L);
       mockUser.setUsername(user.getUsername());
       mockUser.setEmail(user.getEmail());
       mockUser.setPassword(user.getPassword());
       mockUser.setRole(user.getRole());
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        User result=userService.save(user);
        verify(userRepository, times(1)).save(user);
        assertEquals(1, result.getId());
        assertNotEquals(0, result.getId());
    }

    @Order(2)
    @Test
    public void testGetAllUsers() {
        
        User userOne =new User(1L,"harish","harish@gmail.com","harish0417","user");
        User userTwo =new User(1L,"harsha","harsha@gmail.com","harsha0417","admin");
        List<User> userList =new ArrayList<>(Arrays.asList(userOne,userTwo));
        when(userRepository.findAll()).thenReturn(userList);
        assertIterableEquals(userList, userService.getAllUsers());
        List<User> foundList = userService.getAllUsers();
        assertEquals(userList, foundList);
        assertEquals(2, foundList.size());
        assertEquals("harsha", foundList.get(1).getUsername());
    }
    @Order(3)
    @Test
    public void testGetUserById() {
        User user =new User(1L,"harish","harish@gmail.com","harish0417","user");
        Long id = user.getId();
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        User foundUser = userService.getUserById(id);
        assertEquals(user, foundUser);
        assertEquals("harish", foundUser.getUsername());
    }
    @Order(4)
    @Test
    public void testDeleteUserById() {
        User user =new User(1L,"harish","harish@gmail.com","harish0417","user");
        Long id = user.getId();
        userService.deleteUserById(id);
        verify(userRepository, times(1)).deleteById(id);
        assertFalse(userRepository.findById(id).isPresent());
    }
}
