package com.vi5hnu.codesprout.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.vi5hnu.codesprout.Dto.GoogleTokenDataDto;
import com.vi5hnu.codesprout.exceptions.ApiException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.SignatureException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleService {
    @Value("${google.clientIds}")
    private List<String> googleClientIds;
    private static final String GOOGLE_REVOKE_ENDPOINT = "https://oauth2.googleapis.com/revoke";
    private static final GsonFactory GSON_FACTORY = new GsonFactory();
    private static final NetHttpTransport TRANSPORT = new NetHttpTransport();
    private final WebClient webClient;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;


    public Mono<Boolean> revokeAccessToken(String accessToken) {
        return webClient
                .post()
                .uri(GOOGLE_REVOKE_ENDPOINT,uriBuilder -> uriBuilder.queryParam("token", accessToken).build())
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return Mono.just(false);
                });
    }

    public GoogleTokenDataDto verifyAndGetData(@NonNull String idToken) throws SignatureException {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(GoogleService.TRANSPORT, GoogleService.GSON_FACTORY)
                    .setAudience(googleClientIds)
                    .build();


            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if(googleIdToken==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid id token");
            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            return GoogleTokenDataDto.builder()
                    .userId(payload.getSubject())
                    .email(payload.getEmail())
                    .emailVerified(payload.getEmailVerified())
                    .name((String) payload.get("name"))
                    .pictureUrl((String) payload.get("picture"))
                    .build();
        } catch (Exception e) {
            throw new SignatureException("Failed to fetch or parse Google's public keys.", e);
        }
    }
}
