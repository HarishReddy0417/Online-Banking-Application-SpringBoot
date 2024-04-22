package com.example.bankproject.controller;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;
import com.example.bankproject.entities.User;
import com.example.bankproject.repositorys.UserRepository;
import com.example.bankproject.service.UserService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest

public class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

@Order(1)
@Test
void testGetAllUsers() throws Exception{
        
        User userOne =new User(1L,"harish","harish@gmail.com","harish0417","user");

        User userTwo =new User(2L,"harsha","harsha@gmail.com","harsha0417","admin");

        List<User> users=new ArrayList<>(Arrays.asList(userOne,userTwo));

        when(userService.getAllUsers()).thenReturn(users);

        assertIterableEquals(users, userService.getAllUsers());

        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                    .andExpect(status().isOk()).andReturn();

        ModelAndView mav =mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "userList");

        List<User> userList =  (List<User>) mav.getModel().get("users");
        assertNotNull(userList);
        assertEquals(2, userList.size());
        assertEquals("userList", mav.getViewName());

        }

@Order(2)
@Test
void testGetUserById() throws Exception {
   
    User user = new User(1L, "harish", "harish@gmail.com", "harish0417", "user");
    when(userService.getUserById(1L)).thenReturn(user);

    
    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
            .andExpect(status().isOk())
            .andReturn();

   
    ModelAndView mav = mvcResult.getModelAndView();
    User retrievedUser = (User) mav.getModel().get("user");
    assertNotNull(retrievedUser);
    assertEquals(user.getId(), retrievedUser.getId());
    assertEquals(user.getUsername(), retrievedUser.getUsername());
    assertEquals(user.getEmail(), retrievedUser.getEmail());
    assertEquals(user.getPassword(), retrievedUser.getPassword());
    assertEquals(user.getRole(), retrievedUser.getRole());

    assertEquals("user", mav.getViewName());
}

@Order(3)
@Test
void testCreateUserForm()throws Exception{

        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get("/users/new"))
                    .andExpect(status().isOk()).andReturn();
        
        ModelAndView mav=mvcResult.getModelAndView();
        User user=(User) mav.getModel().get("user");
        assertNotNull(user);
        assertEquals("create_user", mav.getViewName());
    }

@Order(4)
@Test
void testSaveUser() throws Exception {
    User user = new User(1L, "john_doe", "john.doe@example.com", "password", "user");

    when(userService.save(any(User.class))).thenReturn(user);

    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("username", user.getUsername())
            .param("email", user.getEmail())
            .param("password", user.getPassword())
            .param("role", user.getRole()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/users"));

   
    User result=userService.save(user);
    verify(userService, times(1)).save(user);
    assertEquals(1, result.getId());
    assertNotEquals(0, result.getId());
}


@Order(5)
@Test
void testSaveUser_ValidUser_RedirectsToUserList() throws Exception {
   
    User user = new User();
    user.setUsername("harish");
    user.setEmail("harish@gmail.com");
    user.setPassword("harish0417");
    user.setRole("user");

    when(userService.save(any(User.class))).thenReturn(user);

    mockMvc.perform(post("/users")
            .param("username", user.getUsername())
            .param("email", user.getEmail())
            .param("password", user.getPassword())
            .param("role", user.getRole()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/users"))
            .andExpect(view().name("redirect:/users"));

    verify(userService).save(user);
}


@Order(6)
@Test
void testSaveUser_InvalidUser_ReturnsCreateUserFormPage() throws Exception {
    User user = new User();
    user.setEmail("harish@gmail.com");
    user.setPassword("harish0417");
    user.setRole("admin");

    when(userService.save(any(User.class))).thenReturn(user);

    mockMvc.perform(post("/users")
            .param("email", user.getEmail())
            .param("password", user.getPassword())
            .param("role", user.getRole()))
            .andExpect(status().isOk())
            .andExpect(view().name("create_user"));

    verify(userService, never()).save(user);
}

@Order(7)
@Test
void testEditUserForm() throws Exception {
  
    User user = new User();
    user.setId(1L);
    user.setUsername("harish");

    when(userService.getUserById(1L)).thenReturn(user);

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/edit/1"))
            .andExpect(status().isOk())
            .andReturn();

    ModelAndView mav = mvcResult.getModelAndView();
    User returnedUser = (User) mav.getModel().get("user");
    assertNotNull(returnedUser);
    assertEquals(1L, returnedUser.getId().longValue());
    assertEquals("harish", returnedUser.getUsername());
    assertEquals("editUser", mav.getViewName());
}

@Order(8)
@Test
void testUpdateUser() throws Exception {
  
    User user = new User(1L, "harish", "harish@gmail.com", "harish0417", "user");

    when(userService.save(any(User.class))).thenReturn(user);

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/edit1")
            .param("id", "1")
            .flashAttr("user", user))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/users"))
            .andReturn();

    verify(userService, times(1)).save(any(User.class));

    ModelAndView mav = mvcResult.getModelAndView();
    assertEquals("redirect:/users", mav.getViewName());
}
@Order(9)
@Test
void testDeleteUser() throws Exception {
    doNothing().when(userService).deleteUserById(anyLong());

    mockMvc.perform(MockMvcRequestBuilders.get("/users/delete/1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/users"));

    verify(userService, times(1)).deleteUserById(1L);
}

@Order(10)
@Test
void testSearchAccount() throws Exception {
        User userOne = new User(1L, "harish", "harish@gmail.com", "harish0417", "user");
        User userTwo = new User(2L, "harsha", "harsha@gmail.com", "harsha0417", "admin");
        List<User> users = Arrays.asList(userOne, userTwo);

        when(userRepository.findByUsername("harish")).thenReturn(users);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/search")
                .param("username", "harish"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        List<User> userList = (List<User>) mav.getModel().get("users");
        assertNotNull(userList);
        assertEquals(2, userList.size());

        assertEquals("userList", mav.getViewName());
    }


}
