package com.enzo.yxzapp;
import com.enzo.yxzapp.dto.common.PageResponse;
import com.enzo.yxzapp.dto.user.CreateUserRequest;
import com.enzo.yxzapp.dto.user.UpdateUserRequest;
import com.enzo.yxzapp.dto.user.UserResponse;
import com.enzo.yxzapp.enums.CorAdministradora;
import com.enzo.yxzapp.enums.Role;
import com.enzo.yxzapp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @MockitoBean
    private UserService userService;

    @Test
    void withoutAuth_shouldBlockAdminRoutes() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "ROOT")
    void list_asRoot_shouldReturn200() throws Exception {
        var item = new UserResponse(
                2L, "Ana", "ana@local.com", Role.USER, null, true,
                LocalDateTime.now().minusDays(1), LocalDateTime.now()
        );

        var page = new PageResponse<>(List.of(item), 1, 1, 0, 10);
        given(userService.list(any())).willReturn(page);

        mockMvc.perform(get("/admin/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].email").value("ana@local.com"));
    }

    @Test
    @WithMockUser(roles = "ROOT")
    void getById_asRoot_shouldReturn200() throws Exception {
        var resp = new UserResponse(
                2L, "Ana", "ana@local.com", Role.USER, null, true,
                LocalDateTime.now().minusDays(1), LocalDateTime.now()
        );

        given(userService.getById(2L)).willReturn(resp);

        mockMvc.perform(get("/admin/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("ana@local.com"));
    }

    @Test
    @WithMockUser(roles = "ROOT")
    void create_asRoot_shouldReturn200() throws Exception {
        var req = new CreateUserRequest(
                "Maria", "maria@local.com", "senha123", Role.ADMIN, CorAdministradora.ROSA
        );

        var resp = new UserResponse(
                10L, "Maria", "maria@local.com", Role.ADMIN, CorAdministradora.ROSA, true,
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().minusMinutes(1)
        );

        given(userService.create(any())).willReturn(resp);

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.corAdministradora").value("ROSA"));
    }

    @Test
    @WithMockUser(roles = "ROOT")
    void update_asRoot_shouldReturn200() throws Exception {
        var req = new UpdateUserRequest("Novo Nome", Role.USER, null, false);

        var resp = new UserResponse(
                2L, "Novo Nome", "ana@local.com", Role.USER, null, false,
                LocalDateTime.now().minusDays(1), LocalDateTime.now()
        );

        given(userService.update(eq(2L), any())).willReturn(resp);

        mockMvc.perform(patch("/admin/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Nome"))
                .andExpect(jsonPath("$.ativo").value(false));
    }
}
