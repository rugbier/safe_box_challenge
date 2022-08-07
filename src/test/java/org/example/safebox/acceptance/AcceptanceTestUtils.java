package org.example.safebox.acceptance;

import org.apache.commons.codec.binary.Base64;
import org.example.safebox.dto.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AcceptanceTestUtils {

    public final static Integer MAX_LOCK_ATTEMPTS = 3;

    public static HttpHeaders getBasicAuthHeaders(String safeboxName, String password){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");

        String auth = safeboxName + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(StandardCharsets.US_ASCII) );

        headers.add("Authorization", "Basic " + new String(encodedAuth));
        return headers;
    }

    public static HttpHeaders getBearerTokenHeaders(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }

    public static HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");
        return headers;
    }

    public static SafeboxRequestDTO createSafeboxRequestDTO(String safeboxName, String password){
        return SafeboxRequestDTO.builder()
                .name(safeboxName)
                .password(password)
                .build();
    }

    public static URI getURI(int serverPort, String path) throws URISyntaxException {
        final String baseUrl = "http://localhost:" + serverPort + "/safebox" + path;
        return new URI(baseUrl);
    }

    public static ResponseEntity<SafeboxResponseDTO> apiCreateSafebox(int serverPort, String safeboxName, String password) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<SafeboxRequestDTO> request = new HttpEntity<>(
                AcceptanceTestUtils.createSafeboxRequestDTO(safeboxName, password),
                AcceptanceTestUtils.getHeaders());

        return restTemplate.postForEntity(
                AcceptanceTestUtils.getURI(serverPort, "/"),
                request,
                SafeboxResponseDTO.class);
    }

    public static ResponseEntity<OpenSafeboxResponseDTO> openSafebox(int serverPort, Long safeboxId, String safeboxName, String password) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Void> request = new HttpEntity<>(AcceptanceTestUtils.getBasicAuthHeaders(safeboxName, password));

        return restTemplate.exchange(
                AcceptanceTestUtils.getURI(serverPort, "/" + safeboxId + "/open"), HttpMethod.GET,
                request,
                OpenSafeboxResponseDTO.class);
    }

    public static ResponseEntity<ItemsResponseDTO> addItemsToSafebox(int serverPort, Long safeboxId, String token, List<String> items) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<ItemRequestDTO> request = new HttpEntity<>(ItemRequestDTO.builder().items(items).build(),
                AcceptanceTestUtils.getBearerTokenHeaders(token));

        return restTemplate.exchange(
                AcceptanceTestUtils.getURI(serverPort, "/" + safeboxId + "/items"), HttpMethod.PUT,
                request,
                ItemsResponseDTO.class);
    }

    public static ResponseEntity<ItemsResponseDTO> getItemsFromSafebox(int serverPort, Long safeboxId, String token) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Void> request = new HttpEntity<>(AcceptanceTestUtils.getBearerTokenHeaders(token));

        return restTemplate.exchange(
                AcceptanceTestUtils.getURI(serverPort, "/" + safeboxId + "/items"), HttpMethod.GET,
                request,
                ItemsResponseDTO.class);
    }
}
