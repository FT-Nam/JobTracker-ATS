//package com.jobtracker.jobtracker_app.controllers;
//
//import static org.hamcrest.Matchers.is;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jobtracker.jobtracker_app.dto.requests.UserCreationRequest;
//import com.jobtracker.jobtracker_app.dto.requests.UserUpdateRequest;
//import com.jobtracker.jobtracker_app.dto.responses.user.UserResponse;
//import com.jobtracker.jobtracker_app.entities.Role;
//import com.jobtracker.jobtracker_app.services.UserService;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//public class UserProfileControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private UserService userService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private UserResponse response;
//    private UserResponse updateResponse;
//    private UserCreationRequest creationRequest;
//    private UserUpdateRequest updateRequest;
//    private Role role;
//
//    private static final String USER_CREATED_MESSAGE = "User create successfully";
//    private static final String USER_DELETED_MESSAGE = "User delete successfully";
//
//    @BeforeEach
//    void setup() {
//        role = Role.builder().id("role1").name("role").build();
//
//        creationRequest = UserCreationRequest.builder()
//                .email("user1@gmail.com")
//                .firstName("Nam")
//                .lastName("Phan")
//                .password("123456")
//                .roleId("role1")
//                .build();
//
//        updateRequest = UserUpdateRequest.builder().firstName("Nam1").build();
//
//        response = UserResponse.builder()
//                .id("user1")
//                .email("user1@gmail.com")
//                .firstName("Nam")
//                .lastName("Phan")
//                .roleName(role.getName())
//                .build();
//
//        updateResponse = UserResponse.builder()
//                .id("user1")
//                .email("user1@gmail.com")
//                .firstName("Nam1")
//                .lastName("Phan")
//                .roleName(role.getName())
//                .build();
//    }
//
//    @Test
//    @WithMockUser
//    void create_shouldReturnCreateUser() throws Exception {
//        when(userService.create(any())).thenReturn(response);
//
//        mockMvc.perform(post("/user")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(creationRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.email", is("user1@gmail.com")))
//                .andExpect(jsonPath("$.message", is(USER_CREATED_MESSAGE)));
//    }
//
//    @Test
//    @WithMockUser
//    void getAll_shouldReturnPageUser() throws Exception {
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<UserResponse> page = new PageImpl<>(List.of(response), pageable, 10);
//        when(userService.getAll(any(Pageable.class))).thenReturn(page);
//
//        mockMvc.perform(get("/user"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data[0].email", is("user1@gmail.com")));
//    }
//
//    @Test
//    @WithMockUser
//    void getById_shouldReturnUser() throws Exception {
//        when(userService.getById(anyString())).thenReturn(response);
//
//        mockMvc.perform(get("/user/user1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.email", is("user1@gmail.com")));
//    }
//
//    @Test
//    @WithMockUser
//    void update_shouldReturnUpdateUser() throws Exception {
//        when(userService.update(anyString(), any(UserUpdateRequest.class))).thenReturn(updateResponse);
//
//        mockMvc.perform(put("/user/user1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.email", is("user1@gmail.com")))
//                .andExpect(jsonPath("$.data.firstName", is("Nam1")));
//    }
//
//    @Test
//    @WithMockUser
//    void delete_shouldReturnSuccessMessage() throws Exception {
//        mockMvc.perform(delete("/user/user1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message", is(USER_DELETED_MESSAGE)));
//        verify(userService).delete("user1");
//    }
//}
