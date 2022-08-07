package org.example.safebox.acceptance;

import org.example.safebox.dto.SafeboxResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URISyntaxException;

@Sql(scripts = "/cleanDataBase.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreateSafeboxAcceptanceTest {

    @LocalServerPort
    int randomServerPort;

    @Test
    public void shouldCreateSafeboxWhenNameNotExist() throws URISyntaxException {
        ResponseEntity<SafeboxResponseDTO> result = AcceptanceTestUtils.apiCreateSafebox(randomServerPort, "AName", "a_password");

        Assertions.assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    public void shouldThrowSafeboxWhenNameNotExist() throws URISyntaxException {
        ResponseEntity<SafeboxResponseDTO> result = AcceptanceTestUtils.apiCreateSafebox(randomServerPort, "AName", "a_password");

        Assertions.assertEquals(200, result.getStatusCodeValue());

        try {
            AcceptanceTestUtils.apiCreateSafebox(randomServerPort, "AName", "a_password");
            Assertions.fail();
        } catch (HttpClientErrorException ex){
            Assertions.assertEquals(409, ex.getRawStatusCode());
            Assertions.assertTrue(ex.getResponseBodyAsString().contains("Safebox already exists"));
        }

    }

}
