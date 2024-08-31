package com.itm.space.backendresources.controller;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void create() throws Exception {

        String userRequest = "{ " +
                "\"username\": \"maks\", " +
                "\"email\": \"maks@gmail.com\", " +
                "\"password\": \"maks\", " +
                "\"firstName\": \"Maks\", " +
                "\"lastName\": \"Maks\"" +
                "}";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequest))
                .andExpect(status().isOk());

        ArgumentCaptor<UserRequest> captor = ArgumentCaptor.forClass(UserRequest.class);
        verify(userService).createUser(captor.capture());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void getUserById() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponse userResponse = new UserResponse(
                "Maks",
                "Maks",
                "maks@gmail.com",
                List.of("ROLE_USER"),
                List.of("group1", "group2"));

        when(userService.getUserById(userId)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/" + userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{ " +
                        "\"firstName\": \"Maks\", " +
                        "\"lastName\": \"Maks\", " +
                        "\"email\": \"maks@gmail.com\", " +
                        "\"roles\": [\"ROLE_USER\"], " +
                        "\"groups\": [\"group1\", \"group2\"]" +
                        "}"));
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void hello() throws Exception {
        mockMvc.perform(get("/api/users/hello")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("user"));
    }
}