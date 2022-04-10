package pl.patrykbober.bloomer.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.patrykbober.bloomer.user.dto.RoleDto;
import pl.patrykbober.bloomer.user.request.UserRolesRequest;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.patrykbober.bloomer.common.util.BloomerTestUtils.asJsonString;

@SpringBootTest
@AutoConfigureMockMvc
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @WithMockUser
    @Test
    void returnAllRolesWhenUserAuthenticated() throws Exception {
        var role = new RoleDto(1L, "USER");
        var roleList = List.of(role);

        when(roleService.findAll()).thenReturn(roleList);

        mockMvc.perform(get("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles[0].id").value(1L))
                .andExpect(jsonPath("$.roles[0].name").value("USER"))
                .andDo(print());
    }

    @WithMockUser(roles = {"USER", "ADMIN"})
    @Test
    void successfullyAddRolesToUserWhenHasAdminRole() throws Exception {
        var request = new UserRolesRequest(List.of("ADMIN"));

        doNothing().when(roleService).addRolesToUser(anyLong(), any());

        mockMvc.perform(post("/users/{userId}/roles", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @WithMockUser(roles = {"USER", "ADMIN"})
    @Test
    void successfullyDeleteRolesFromUserWhenHasAdminRole() throws Exception {
        var request = new UserRolesRequest(List.of("USER"));

        doNothing().when(roleService).deleteRolesFromUser(anyLong(), any());

        mockMvc.perform(delete("/users/{userId}/roles", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

}
