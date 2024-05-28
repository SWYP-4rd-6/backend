package com.swygbro.trip.backend.domain.user.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.domain.user.domain.UserRepository;
import com.swygbro.trip.backend.domain.user.dto.*;
import com.swygbro.trip.backend.domain.user.excepiton.UuidExpiredException;
import com.swygbro.trip.backend.global.jwt.TokenService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleOauthService {

    @Value("${google.client.id}")
    private String clientId;
    @Value("${google.client.secret}")
    private String secretPassword;
    @Value("${google.client.redirect}")
    private String redirectUri;
    @Value("${google.client.authorization_uri}")
    private String authorizationUri;
    @Value("${google.client.scope}")
    private String scope;

    private final UserRepository userRepository;
    private final UserService userService;
    private final TokenService tokenService;

    private final RestTemplate restTemplate;
    private final UserValidationService userValidationService;

    private final HttpSession httpSession;


    public String getGoogleLoginUrl() {
        return UriComponentsBuilder.fromUriString(authorizationUri)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scope)
                .build().toUriString();
    }

    public ResponseEntity<?> callback(String code) throws JsonProcessingException {
        System.out.println("code in code:" + code);
        String googleAccessToken = getGoogleAccessToken(code);
        GoogleUserInfo userInfo = getGoogleUserInfo(googleAccessToken);

        Optional<User> user = userRepository.findByEmail(userInfo.getEmail());

        // 등록된 회원이라면 로그인처리
        if (user.isPresent()) {
            return ResponseEntity.ok(
                    LoginResponseDto.builder()
                            .user(UserInfoDto.fromEntity(user.get()))
                            .token(tokenService.generateToken(userInfo.getEmail()))
                            .build());
        }

        UUID uuid = UUID.randomUUID();
        httpSession.setAttribute(String.valueOf(uuid), userInfo);
        System.out.println("uuid in code:" + uuid);
        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .body(new GoogleUserInfoDto(userInfo, uuid.toString()));
    }

    @Transactional
    public UserInfoDto createUser(CreateGoogleUserRequest dto) throws JsonProcessingException {
        userValidationService.checkUniqueUser(dto);
        System.out.println("userInfo in createUser:" + dto);

        GoogleUserInfo userInfo = (GoogleUserInfo) httpSession.getAttribute(dto.getUuid());
        System.out.println("userInfo in createUser:" + userInfo);
        if (userInfo == null) {
            throw new UuidExpiredException();
        }

        userValidationService.checkEmailDupl(userInfo.getEmail());
        
        User createdUser = userRepository.save(
                new User(dto, userInfo)
        );

        return UserInfoDto.builder()
                .id(createdUser.getId())
                .email(createdUser.getEmail())
                .nickname(createdUser.getNickname())
                .name(createdUser.getName())
                .birthdate(createdUser.getBirthdate())
                .gender(createdUser.getGender())
                .nationality(createdUser.getNationality())
                .build();
    }


    public String getGoogleAccessToken(String code) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://oauth2.googleapis.com/token")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", secretPassword);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity.post(uri).headers(headers).body(body);
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        return jsonNode.get("access_token").asText();
    }

    private GoogleUserInfo getGoogleUserInfo(String googleAccessToken) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://www.googleapis.com/oauth2/v2/userinfo")
                .queryParam("access_token", googleAccessToken)
                .encode()
                .build()
                .toUri();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        return new ObjectMapper().readValue(responseEntity.getBody(), GoogleUserInfo.class);
    }


}
