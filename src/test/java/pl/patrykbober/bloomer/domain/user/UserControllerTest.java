package pl.patrykbober.bloomer.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.patrykbober.bloomer.domain.user.request.CreateUserRequest;
import pl.patrykbober.bloomer.domain.user.request.RegisterUserRequest;
import pl.patrykbober.bloomer.domain.user.request.SelfUpdateUserRequest;
import pl.patrykbober.bloomer.domain.user.request.UpdateUserRequest;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.patrykbober.bloomer.util.BloomerTestUtils.asJsonString;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @WithMockUser
    @Test
    public void returnAllUsersWhenUserAuthenticated() throws Exception {
        var user = new UserDto(1L, "user1@bloomer.com", "user", "bloomer", true);
        var userList = List.of(user);

        when(userService.findAll()).thenReturn(userList);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users", hasSize(1)))
                .andExpect(jsonPath("$.users[0].id").value(1L))
                .andExpect(jsonPath("$.users[0].email").value("user1@bloomer.com"))
                .andExpect(jsonPath("$.users[0].firstName").value("user"))
                .andExpect(jsonPath("$.users[0].lastName").value("bloomer"))
                .andExpect(jsonPath("$.users[0].active").value(true))
                .andDo(print());
    }

    @WithMockUser
    @Test
    public void returnUserByIdWhenUserAuthenticated() throws Exception {
        var user = new UserDto(1L, "user1@bloomer.com", "user", "bloomer", true);

        when(userService.findById(any())).thenReturn(user);

        mockMvc.perform(get("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @WithAnonymousUser
    @Test
    public void successfullyRegisterUser() throws Exception {
        var request = new RegisterUserRequest("newuser@bloomer.com", "new", "user", "passwd");

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @WithMockUser(roles = {"USER", "ADMIN"})
    @Test
    public void successfullyCreateUserWhenHasAdminRole() throws Exception {
        var request = new CreateUserRequest("newuser@bloomer.com", "new", "user", "passwd", true, List.of("USER", "ADMIN"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @WithMockUser(roles = {"USER", "ADMIN"})
    @Test
    public void successfullyUpdateUserWhenHasAdminRole() throws Exception {
        var user = new UserDto(1L, "user1@bloomer.com", "newFirstName", "newLastName", true);
        var request = new UpdateUserRequest("newFirstName", "newLastName", null, null, null);

        when(userService.update(any(), any(UpdateUserRequest.class))).thenReturn(user);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("newFirstName"))
                .andExpect(jsonPath("$.lastName").value("newLastName"))
                .andExpect(jsonPath("$.email").value("user1@bloomer.com"))
                .andExpect(jsonPath("$.active").value(true))
                .andDo(print());
    }

    @WithMockUser(authorities = {"USER", "ADMIN"})
    @Test
    public void successfullyDeleteUserWhenHasAdminRole() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @WithMockUser(username = "user1@bloomer.com")
    @Test
    public void returnCurrentlyLoggedInUser() throws Exception {
        var user = new UserDto(1L, "user1@bloomer.com", "user", "bloomer", true);

        when(userService.findByEmail(any())).thenReturn(user);

        mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("user"))
                .andExpect(jsonPath("$.lastName").value("bloomer"))
                .andExpect(jsonPath("$.email").value("user1@bloomer.com"))
                .andExpect(jsonPath("$.active").value(true))
                .andDo(print());
    }

    @WithMockUser(username = "user1@bloomer.com")
    @Test
    public void successfullyUpdateCurrentUser() throws Exception {
        var user = new UserDto(1L, "user1@bloomer.com", "newFirstName", "newLastName", true);
        var request = new SelfUpdateUserRequest("newFirstName", "newLastName");

        when(userService.update(any(), any(SelfUpdateUserRequest.class))).thenReturn(user);

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("newFirstName"))
                .andExpect(jsonPath("$.lastName").value("newLastName"))
                .andExpect(jsonPath("$.email").value("user1@bloomer.com"))
                .andExpect(jsonPath("$.active").value(true))
                .andDo(print());
    }

    @WithMockUser(username = "user1@bloomer.com")
    @Test
    public void successfullyDeleteCurrentUser() throws Exception {
        mockMvc.perform(delete("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

}
