package edu.miu.cs.cs489.lab5.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppointmentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getAppointments_returnsJsonArray() {
        String url = "http://localhost:" + port + "/api/appointments";
        ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        // Expect an empty array or list representation when no appointments exist
        String body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.startsWith("[")).isTrue();
    }
}
