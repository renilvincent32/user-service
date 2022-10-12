package org.fimohealth.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fimohealth.user.controller.UserController;
import org.fimohealth.user.domain.Users;
import org.fimohealth.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository repository;

    @Test
    public void test_create_user() throws Exception {
        //GIVEN
        ObjectMapper mapper = new ObjectMapper();
        String userJson = "{\"email\" : \"user@test.com\", \"password\" : \"password\", \"age\" : 34}";
        Users savedUser = mapper.readValue(userJson, Users.class);
        when(repository.save(any(Users.class))).thenReturn(savedUser);

        //WHEN / THEN
        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is(savedUser.getEmail())))
                .andExpect(jsonPath("$.age", is(savedUser.getAge())))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void test_create_user_when_email_already_exists() throws Exception {
        //GIVEN
        ObjectMapper mapper = new ObjectMapper();
        String userJson = "{\"email\" : \"user@test.com\", \"password\" : \"password\", \"age\" : 34}";
        Users savedUser = mapper.readValue(userJson, Users.class);
        when(repository.save(any(Users.class))).thenReturn(savedUser);
        when(repository.findByEmail("user@test.com")).thenReturn(Optional.of(savedUser));

        //WHEN / THEN
        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].message", is("Email already exists!")));
    }

    @Test
    public void test_update_user() throws Exception {
        //GIVEN
        ObjectMapper mapper = new ObjectMapper();
        String userJson = "{\"email\" : \"userNew@test.com\", \"password\" : \"password\"}";
        Users newUser = mapper.readValue(userJson, Users.class);
        Users oldUser = new Users().setAge(34).setPassword("password").setEmail("user@test.com");
        when(repository.save(any(Users.class))).thenReturn(newUser);
        when(repository.findByEmail("user@test.com")).thenReturn(Optional.of(oldUser));

        //WHEN / THEN
        mockMvc.perform(patch("/api/user/{email}", "user@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("userNew@test.com")));
    }

    @Test
    public void test_update_user_when_age_is_patched() throws Exception {
        //GIVEN
        ObjectMapper mapper = new ObjectMapper();
        String userJson = "{\"email\" : \"userNew@test.com\", \"password\" : \"password\", \"age\" : 12}";
        Users newUser = mapper.readValue(userJson, Users.class);
        Users oldUser = new Users().setAge(34).setPassword("password").setEmail("user@test.com");
        when(repository.save(any(Users.class))).thenReturn(newUser);
        when(repository.findByEmail("user@test.com")).thenReturn(Optional.of(oldUser));

        //WHEN / THEN
        mockMvc.perform(patch("/api/user/{email}", "user@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("age")))
                .andExpect(jsonPath("$[0].message", is("Field cannot be updated")));
    }

    @Test
    public void test_delete_user() throws Exception {
        //GIVEN
        Users userToBeDeleted = new Users().setAge(34).setPassword("password").setEmail("user@test.com");
        when(repository.findByEmail("user@test.com")).thenReturn(Optional.of(userToBeDeleted));

        //WHEN / THEN
        mockMvc.perform(delete("/api/user/{email}", "user@test.com"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void test_get_users_in_given_age_range() throws Exception {
        //GIVEN
        Users user1 = new Users().setAge(33).setPassword("password1").setEmail("user1@test.com");
        Users user2 = new Users().setAge(13).setPassword("password2").setEmail("user2@test.com");
        when(repository.findAllUsersInGivenAgeGroup(12, 34)).thenReturn(Arrays.asList(user1, user2));

        //WHEN / THEN
        mockMvc.perform(get("/api/user/")
                        .param("minAge", "12")
                        .param("maxAge", "34"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", is("user1@test.com")))
                .andExpect(jsonPath("$[0].age", is(33)))
                .andExpect(jsonPath("$[1].email", is("user2@test.com")))
                .andExpect(jsonPath("$[1].age", is(13)));
    }

    @Test
    public void test_login_successful() throws Exception {
        //GIVEN
        String encodedPassword = Base64.getEncoder().encodeToString("password".getBytes());
        Users existingUser = new Users().setAge(33).setPassword("password").setEmail("user@test.com");
        when(repository.findByEmailAndPassword("user@test.com", encodedPassword))
                .thenReturn(Optional.of(existingUser));

        //WHEN
        MvcResult mvcResult = mockMvc.perform(get("/api/user/login")
                        .param("email", "user@test.com")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andReturn();

        //THEN
        assertEquals(mvcResult.getResponse().getContentAsString(), "Login Successful!");
    }

    @Test
    public void test_login_failed() throws Exception {
        //GIVEN
        String encodedPassword = Base64.getEncoder().encodeToString("password1".getBytes());
        Users existingUser = new Users().setAge(33).setPassword("password").setEmail("user@test.com");
        when(repository.findByEmailAndPassword("user@test.com", encodedPassword))
                .thenReturn(Optional.of(existingUser));

        //WHEN
        MvcResult mvcResult = mockMvc.perform(get("/api/user/login")
                        .param("email", "user@test.com")
                        .param("password", "password"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        //THEN
        assertEquals(mvcResult.getResponse().getContentAsString(), "Login failed!");
    }
}
