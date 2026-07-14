package sync.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sync.dtos.CustomerArchiveDTO;
import sync.service.CustomerArchiveService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sync.entities.enums.Gender.FEMALE;
import static sync.entities.enums.Gender.MALE;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerArchiveControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerArchiveService customerArchiveService;

    @Test
    @SneakyThrows
    void shouldReturn_201_whenValidRequest() {
        // given
        CustomerArchiveDTO dto = CustomerArchiveDTO.builder()
                .customerId(UUID.randomUUID())
                .firstName("John")
                .lastName("DOE")
                .gender(MALE)
                .email("john.doe@google.com")
                .build();

        // when + then
        mockMvc.perform(post("/api/v1/archives/customers")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_archive:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // verify
        verify(customerArchiveService).saveArchivedCustomer(any());
    }

    @Test
    @SneakyThrows
    void shouldReturn_400_whenInvalidRequest() {
        // given
        CustomerArchiveDTO dto = CustomerArchiveDTO.builder()
                .customerId(UUID.randomUUID())
                .firstName("Aisha")
                .lastName("CAMARA")
                .gender(FEMALE)
                .email("")
                .build();

        // when + then
        mockMvc.perform(post("/api/v1/archives/customers")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_archive:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        // verify
        verify(customerArchiveService, never()).saveArchivedCustomer(any());
    }

    @Test
    @SneakyThrows
    void shouldReturn_401_whenNoToken() {
        // given
        CustomerArchiveDTO dto = CustomerArchiveDTO.builder()
                .customerId(UUID.randomUUID())
                .firstName("John")
                .lastName("DOE")
                .gender(MALE)
                .email("john.doe@google.com")
                .build();

        // when + then
        mockMvc.perform(post("/api/v1/archives/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        verify(customerArchiveService, never()).saveArchivedCustomer(any());
    }

    @Test
    @SneakyThrows
    void shouldReturn_403_whenMissingScope() {
        // given
        CustomerArchiveDTO dto = CustomerArchiveDTO.builder()
                .customerId(UUID.randomUUID())
                .firstName("John")
                .lastName("DOE")
                .gender(MALE)
                .email("john.doe@google.com")
                .build();

        // when + then
        mockMvc.perform(post("/api/v1/archives/customers")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_archive:read")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        verify(customerArchiveService, never()).saveArchivedCustomer(any());
    }
}
