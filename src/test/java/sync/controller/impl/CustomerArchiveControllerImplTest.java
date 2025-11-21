package sync.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sync.dtos.CustomerArchiveDTO;
import sync.service.CustomerArchiveService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sync.enums.Gender.FEMALE;
import static sync.enums.Gender.MALE;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerArchiveControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    CustomerArchiveService customerArchiveService;

    @Test
    @SneakyThrows
    void shouldReturn_200_whenValidRequest() {
        // given
        CustomerArchiveDTO dto = CustomerArchiveDTO.builder()
                .customerId(UUID.randomUUID())
                .firstName("John")
                .lastName("DOE")
                .gender(MALE)
                .email("john.doe@google.com")
                .build();

        //when + Then
        mockMvc.perform(post("/api/v1/archives/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // verify
        verify(customerArchiveService, times(1)).saveArchivedCustomer(any());
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        // verify
        verify(customerArchiveService, never()).saveArchivedCustomer(any());
    }

}
