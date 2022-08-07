package org.example.safebox.acceptance;

import org.example.safebox.dto.OpenSafeboxResponseDTO;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OpenSafeboxAcceptanceTest {
    @LocalServerPort
    int randomServerPort;

    @Test
    public void shouldOpenSafeboxWhenSafeboxExist() throws URISyntaxException {
        ResponseEntity<SafeboxResponseDTO> resultSafeboxCreation = AcceptanceTestUtils.apiCreateSafebox(randomServerPort, "AName", "a_password");

        Assertions.assertEquals(200, resultSafeboxCreation.getStatusCodeValue());

        ResponseEntity<OpenSafeboxResponseDTO> result = AcceptanceTestUtils.openSafebox(
                randomServerPort,
                resultSafeboxCreation.getBody().getId(),
                "AName",
                "a_password");

        Assertions.assertEquals(200, result.getStatusCodeValue());
        Assertions.assertFalse(result.getBody().getToken().isEmpty());
        Assertions.assertFalse(result.getBody().getToken().isBlank());
    }

    @Test
    public void shouldThrow404WhenTryOpenNonExistSafebox() throws URISyntaxException {
        try {
            AcceptanceTestUtils.openSafebox(randomServerPort, 11111L, "AName", "a_password");
            Assertions.fail();
        } catch (HttpClientErrorException ex) {
            Assertions.assertEquals(404, ex.getRawStatusCode());
            Assertions.assertTrue(ex.getResponseBodyAsString().contains("Safebox not found"));
        }
    }

    @Test
    public void shouldThrow401WhenTryOpenExistSafeboxWithWrongNameAndPassword() throws URISyntaxException {
        ResponseEntity<SafeboxResponseDTO> resultSafeboxCreation = AcceptanceTestUtils.apiCreateSafebox(randomServerPort, "AName", "a_password");

        Assertions.assertEquals(200, resultSafeboxCreation.getStatusCodeValue());

        try {
            AcceptanceTestUtils.openSafebox(
                    randomServerPort,
                    resultSafeboxCreation.getBody().getId(),
                    "AName1",
                    "a_password1");
            Assertions.fail();
        } catch (HttpClientErrorException ex) {
            Assertions.assertEquals(401, ex.getRawStatusCode());
        }
    }

    @Test
    public void shouldThrow423WhenTryOpenLockedSafeboxWithTheLastAttemptWithSuccess() throws URISyntaxException {
        ResponseEntity<SafeboxResponseDTO> resultSafeboxCreation = AcceptanceTestUtils.apiCreateSafebox(randomServerPort, "AName", "a_password");

        Assertions.assertEquals(200, resultSafeboxCreation.getStatusCodeValue());

        //We will lock the safebox
        for(int i = 0; i < AcceptanceTestUtils.MAX_LOCK_ATTEMPTS; i++) {
            try {
                AcceptanceTestUtils.openSafebox(
                        randomServerPort,
                        resultSafeboxCreation.getBody().getId(),
                        "AName1",
                        "a_password1");
                Assertions.fail();
            } catch (HttpClientErrorException ex) {
                Assertions.assertEquals(401, ex.getRawStatusCode());
            }
        }

        try {
            AcceptanceTestUtils.openSafebox(
                    randomServerPort,
                    resultSafeboxCreation.getBody().getId(),
                    "AName",
                    "a_password");
            Assertions.fail();
        } catch (HttpClientErrorException ex) {
            Assertions.assertEquals(423, ex.getRawStatusCode());
            Assertions.assertTrue(ex.getResponseBodyAsString().contains("Request safebox is locked"));
        }
    }

    @Test
    public void shouldThrow423WhenTryOpenLockedSafeboxAfterManyTriesWirhWrongPassword() throws URISyntaxException {
        ResponseEntity<SafeboxResponseDTO> resultSafeboxCreation = AcceptanceTestUtils.apiCreateSafebox(randomServerPort, "AName", "a_password");

        Assertions.assertEquals(200, resultSafeboxCreation.getStatusCodeValue());

        //We will lock the safebox
        for(int i = 0; i < AcceptanceTestUtils.MAX_LOCK_ATTEMPTS; i++) {
            try {
                AcceptanceTestUtils.openSafebox(
                        randomServerPort,
                        resultSafeboxCreation.getBody().getId(),
                        "AName1",
                        "a_password1");
                Assertions.fail();
            } catch (HttpClientErrorException ex) {
                Assertions.assertEquals(401, ex.getRawStatusCode());
            }
        }

        try {
            AcceptanceTestUtils.openSafebox(
                    randomServerPort,
                    resultSafeboxCreation.getBody().getId(),
                    "AName1",
                    "a_password1");
            Assertions.fail();
        } catch (HttpClientErrorException ex) {
            Assertions.assertEquals(423, ex.getRawStatusCode());
            Assertions.assertTrue(ex.getResponseBodyAsString().contains("Request safebox is locked"));
        }
    }
}
