package org.example.safebox.acceptance;

import org.example.safebox.dto.ItemsResponseDTO;
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
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/cleanDataBase.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class SafeboxItemsAcceptanceTest {
    @LocalServerPort
    int randomServerPort;

    @Test
    public void shouldAddItemsToAExistSafebox() throws URISyntaxException {
        ResponseEntity<SafeboxResponseDTO> resultSafeboxCreation = AcceptanceTestUtils.apiCreateSafebox(randomServerPort, "AName", "a_password");

        Assertions.assertEquals(200, resultSafeboxCreation.getStatusCodeValue());

        ResponseEntity<OpenSafeboxResponseDTO> resultOpenSafebox = AcceptanceTestUtils.openSafebox(
                randomServerPort,
                resultSafeboxCreation.getBody().getId(),
                "AName",
                "a_password");

        Assertions.assertEquals(200, resultOpenSafebox.getStatusCodeValue());

        ResponseEntity<ItemsResponseDTO> result = AcceptanceTestUtils.addItemsToSafebox(
                randomServerPort,
                resultSafeboxCreation.getBody().getId(),
                resultOpenSafebox.getBody().getToken(),
                List.of("Content 1", "Content 2"));

        Assertions.assertEquals(200, result.getStatusCodeValue());
        Assertions.assertEquals(2, result.getBody().getItems().size());

        Assertions.assertEquals("Content 1", result.getBody().getItems().get(0));
        Assertions.assertEquals("Content 2", result.getBody().getItems().get(1));
    }

    @Test
    public void shouldThrow401WhenAddItemsToASafeboxAndTokenIsMalformed() throws URISyntaxException {
        ResponseEntity<SafeboxResponseDTO> resultSafeboxCreation = AcceptanceTestUtils.apiCreateSafebox(randomServerPort, "AName", "a_password");

        Assertions.assertEquals(200, resultSafeboxCreation.getStatusCodeValue());

        ResponseEntity<OpenSafeboxResponseDTO> resultOpenSafebox = AcceptanceTestUtils.openSafebox(
                randomServerPort,
                resultSafeboxCreation.getBody().getId(),
                "AName",
                "a_password");

        Assertions.assertEquals(200, resultOpenSafebox.getStatusCodeValue());

        try {
            AcceptanceTestUtils.addItemsToSafebox(
                    randomServerPort,
                    resultSafeboxCreation.getBody().getId(),
                    resultOpenSafebox.getBody().getToken() + "ABCD",
                    List.of("Content 1", "Content 2"));
            Assertions.fail();
        } catch (HttpClientErrorException ex) {
            Assertions.assertEquals(401, ex.getRawStatusCode());
        }
    }

   @Test
    public void shouldThrow404WhenAddItemsToANonExistSafebox() throws URISyntaxException {
       ResponseEntity<SafeboxResponseDTO> resultSafeboxCreation = AcceptanceTestUtils.apiCreateSafebox(randomServerPort, "AName", "a_password");

       Assertions.assertEquals(200, resultSafeboxCreation.getStatusCodeValue());

       ResponseEntity<OpenSafeboxResponseDTO> resultOpenSafebox = AcceptanceTestUtils.openSafebox(
               randomServerPort,
               resultSafeboxCreation.getBody().getId(),
               "AName",
               "a_password");

       Assertions.assertEquals(200, resultOpenSafebox.getStatusCodeValue());

       try {
           AcceptanceTestUtils.addItemsToSafebox(
                   randomServerPort,
                   11111L,
                   resultOpenSafebox.getBody().getToken(),
                   List.of("Content 1", "Content 2"));
           Assertions.fail();
       } catch (HttpClientErrorException ex){
           Assertions.assertEquals(404, ex.getRawStatusCode());
           Assertions.assertTrue(ex.getResponseBodyAsString().contains("Safebox not found"));
       }
   }


    @Test
    public void shouldRetreiveItemsFromEmptySafebox() throws URISyntaxException {
        ResponseEntity<SafeboxResponseDTO> resultSafeboxCreation = AcceptanceTestUtils.apiCreateSafebox(randomServerPort, "AName", "a_password");

        Assertions.assertEquals(200, resultSafeboxCreation.getStatusCodeValue());

        ResponseEntity<OpenSafeboxResponseDTO> resultOpenSafebox = AcceptanceTestUtils.openSafebox(
                randomServerPort,
                resultSafeboxCreation.getBody().getId(),
                "AName",
                "a_password");

        Assertions.assertEquals(200, resultOpenSafebox.getStatusCodeValue());

        ResponseEntity<ItemsResponseDTO> result = AcceptanceTestUtils.getItemsFromSafebox(
                randomServerPort,
                resultSafeboxCreation.getBody().getId(),
                resultOpenSafebox.getBody().getToken());

        Assertions.assertEquals(200, result.getStatusCodeValue());
        Assertions.assertEquals(0, result.getBody().getItems().size());
    }

    @Test
    public void shouldRetreiveItemsFromNonEmptySafebox() throws URISyntaxException {
        ResponseEntity<SafeboxResponseDTO> resultSafeboxCreation = AcceptanceTestUtils.apiCreateSafebox(randomServerPort, "AName", "a_password");

        Assertions.assertEquals(200, resultSafeboxCreation.getStatusCodeValue());

        ResponseEntity<OpenSafeboxResponseDTO> resultOpenSafebox = AcceptanceTestUtils.openSafebox(
                randomServerPort,
                resultSafeboxCreation.getBody().getId(),
                "AName",
                "a_password");

        Assertions.assertEquals(200, resultOpenSafebox.getStatusCodeValue());

        ResponseEntity<ItemsResponseDTO> resultAddItems = AcceptanceTestUtils.addItemsToSafebox(
                randomServerPort,
                resultSafeboxCreation.getBody().getId(),
                resultOpenSafebox.getBody().getToken(),
                List.of("Content 1", "Content 2"));

        Assertions.assertEquals(200, resultAddItems.getStatusCodeValue());

        ResponseEntity<ItemsResponseDTO> result = AcceptanceTestUtils.getItemsFromSafebox(
                randomServerPort,
                resultSafeboxCreation.getBody().getId(),
                resultOpenSafebox.getBody().getToken());

        Assertions.assertEquals(200, result.getStatusCodeValue());
        Assertions.assertEquals(2, result.getBody().getItems().size());

        Assertions.assertEquals("Content 1", result.getBody().getItems().get(0));
        Assertions.assertEquals("Content 2", result.getBody().getItems().get(1));
    }

    @Test
    public void shouldThrow404WhenRetreiveItemsFromNonExistSafebox() throws URISyntaxException {
        ResponseEntity<SafeboxResponseDTO> resultSafeboxCreation = AcceptanceTestUtils.apiCreateSafebox(randomServerPort, "AName", "a_password");

        Assertions.assertEquals(200, resultSafeboxCreation.getStatusCodeValue());

        ResponseEntity<OpenSafeboxResponseDTO> resultOpenSafebox = AcceptanceTestUtils.openSafebox(
                randomServerPort,
                resultSafeboxCreation.getBody().getId(),
                "AName",
                "a_password");

        Assertions.assertEquals(200, resultOpenSafebox.getStatusCodeValue());

        ResponseEntity<ItemsResponseDTO> resultAddItems = AcceptanceTestUtils.addItemsToSafebox(
                randomServerPort,
                resultSafeboxCreation.getBody().getId(),
                resultOpenSafebox.getBody().getToken(),
                List.of("Content 1", "Content 2"));

        Assertions.assertEquals(200, resultAddItems.getStatusCodeValue());

        try {
            AcceptanceTestUtils.getItemsFromSafebox(
                    randomServerPort,
                    11111L,
                    resultOpenSafebox.getBody().getToken());
            Assertions.fail();
        } catch (HttpClientErrorException ex) {
            Assertions.assertEquals(404, ex.getRawStatusCode());
            Assertions.assertTrue(ex.getResponseBodyAsString().contains("Safebox not found"));
        }
    }

}

