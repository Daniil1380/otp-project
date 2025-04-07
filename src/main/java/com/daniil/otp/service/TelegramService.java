package com.daniil.otp.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class TelegramService {

    @Value("${telegram.token}")
    private String token;

    public void sendCode(String destination, String code) {
        String message = String.format(destination + ", your confirmation code is: %s", code);
        String url = String.format("%s?chat_id=%s&text=%s",
                "https://api.telegram.org/bot" + token + "/getUpdates",
                destination,
                urlEncode(message));

        sendTelegramRequest(url);
    }
    private void sendTelegramRequest(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
            }
        } catch (IOException ignored) {
        }
    }
    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

}
