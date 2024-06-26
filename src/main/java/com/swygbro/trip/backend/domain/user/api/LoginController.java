package com.swygbro.trip.backend.domain.user.api;

import com.swygbro.trip.backend.domain.user.application.LoginService;
import com.swygbro.trip.backend.domain.user.application.UserService;
import com.swygbro.trip.backend.domain.user.dto.CreateUserRequest;
import com.swygbro.trip.backend.domain.user.dto.LoginRequest;
import com.swygbro.trip.backend.domain.user.dto.LoginResponseDto;
import com.swygbro.trip.backend.domain.user.dto.UserInfoDto;
import com.swygbro.trip.backend.global.document.ValidationErrorResponse;
import com.swygbro.trip.backend.global.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Login", description = "로그인 관련 API")
public class LoginController {

    private final LoginService loginService;
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = """
            # 회원가입
                        
            회원을 생성합니다. 회원 가입 시 사용자 이메일, 닉네임, 이름, 전화번호, 국적, 성별, 비밀번호, 비밀번호 확인을 입력합니다.
            각 필드의 제약 조건은 다음과 같습니다.

            | 필드명 | 설명 | 제약조건 | 중복확인 | 예시 |
            |--------|------|----------|----------|------|
            | email | 사용자의 이메일 | 이메일 형식 | Y | email01@email.com |
            | nickname | 다른 사용자들에게 보이는 닉네임 | 4~20자 | Y | nickname01 |
            | name | 사용자의 이름 | 2~20자 | N | name01 |
            | phone | 사용자의 전화번호 | '-'를 제외한 숫자, null 가능 | Y | 01012345678 |
            | location | 사용자의 거주 지역 | 문자열(todo: 제약사항?), null 가능 | N | 서울 종로구 창신동 |
            | nationality | 사용자의 국적 | 영문3자 국가 코드 | N | KOR |
            | birthdate | 사용자의 생년월일 | `yyyy-MM-dd` 형식의 문자열 | N | 1990-01-01 |
            | gender | 성별 | Male, Female 중 하나 | N | Male |
            | password | 사용자의 비밀번호 | 영문(대소문자), 숫자, 특수문자를 포함한 8~32자 | N | password01! |
            | passwordCheck | 사용자의 비밀번호 확인 | password와 동일한 입력 | N | password01! |
             
            ## 응답
                        
            - 회원 가입 성공 시 `200` 코드와 함께 회원 기본 정보를 반환합니다.
            - 입력 양식에 오류가 있을 경우 `400` 에러를 반환합니다.
            - 중복된 값이 있을 경우 `409` 에러를 반환합니다.
             
            """)
    @ApiResponse(
            responseCode = "200",
            description = "생성한 계정 고유 번호를 반환합니다.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserInfoDto.class)))
    @ApiResponse(
            responseCode = "409",
            description = "입력 값 중 중복된 값이 있습니다.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(value = "{\n  \"status\": \"CONFLICT\",\n  \"message\": \"데이터 중복\"\n}")
            )
    )
    @ValidationErrorResponse
    public UserInfoDto createUser(@Valid @RequestBody CreateUserRequest dto) {
        return userService.createUser(dto);
    }

    @PostMapping("/login")
    @Operation(summary = "이메일 로그인", description = """
                        
            # 로그인
                        
            사용자의 이메일과 비밀번호를 입력하여 로그인합니다.
                        
            ## 응답
                        
            - 로그인 성공 시 `200` 코드와 함께 유저 정보 및 토큰을 반환합니다.
              - 유저 정보엔는 id, email, nickname, name, birthdate, gender, nationality가 포함되어 있습니다.
              - 토큰은 `accessToken`과 `refreshToken`으로 구성되어 있습니다.
            - 로그인 실패 시 `400` 에러를 반환합니다.
              - 계정이 존재하지 않거나 비밀번호가 일치하지 않을 경우 발생합니다.     
            """)
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공 시 유저 정보와 함께 토큰을 반환합니다.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = LoginResponseDto.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(value = """
                            {
                                "status": "BAD_REQUEST",
                                "message": "로그인에 실패했습니다."
                            }
                            """)
            )
    )
    public LoginResponseDto login(@Valid @RequestBody LoginRequest dto) {
        return loginService.login(dto);
    }

}
