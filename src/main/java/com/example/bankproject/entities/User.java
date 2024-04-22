package com.example.bankproject.entities;



import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
public class User {


       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;

       @NotBlank(message = "UserName is required")
       @Size(max = 100)
       @Column(unique = true)
       private String username;

       @NotBlank(message = "Email is required")
       @Email(message = "Invalid email format")
       @Column(unique = true)
       private String email;

       @NotBlank(message = "Password is required")
       @Size(min = 6, max = 100)
       private String password;

       private String role;


    public User(){

    }


    public User(Long id, @NotBlank(message = "UserName is required") @Size(max = 100) String username,
            @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
            @NotBlank(message = "Password is required") @Size(min = 6, max = 100) String password, String role) {
        
        this.id=id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }

    @Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id) &&
            Objects.equals(username, user.username) &&
            Objects.equals(email, user.email) &&
            Objects.equals(password, user.password) &&
            Objects.equals(role, user.role);
}

@Override
public int hashCode() {
    return Objects.hash(id, username, email, password, role);
}


   
}
