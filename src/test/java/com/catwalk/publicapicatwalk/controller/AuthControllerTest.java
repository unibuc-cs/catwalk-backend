package com.catwalk.publicapicatwalk.controller;

import com.catwalk.publicapicatwalk.controller.web.ErrorCode;
import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.catwalk.publicapicatwalk.dto.LoginRequest;
import com.catwalk.publicapicatwalk.dto.SignupRequest;
import com.catwalk.publicapicatwalk.model.User;
import com.catwalk.publicapicatwalk.repository.UserRepository;
import com.catwalk.publicapicatwalk.security.jwt.JwtUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest extends GenericIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        // empty table
        userRepository.deleteAll();

        // create dummy user and admin
        User oDummyUser = User.builder()
                .email("user@catwalk.ro")
                .password(encoder.encode("Parola123"))
                .firstName("User")
                .lastName("Test")
                .role("USER")
                .build();
        User oAdminDummyUser = User.builder()
                .email("admin@catwalk.ro")
                .password(encoder.encode("Parola123"))
                .firstName("User")
                .lastName("Test")
                .role("ADMIN")
                .build();
        userRepository.saveAll(Arrays.asList(oDummyUser, oAdminDummyUser));
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        // arrange
        SignupRequest oRequestBody = SignupRequest.builder()
                .email("register_successfully@catwalk.ro")
                .password("Parola123")
                .firstName("User")
                .lastName("Test")
                .build();

        // act
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(AuthController.PATH + "/register", oRequestBody))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.SUCCESS.toString());
    }

    @Test
    void shouldThrowErrorIfEmailAlreadyExistsWhenRegisters() throws Exception {
        // arrange
        SignupRequest oRequestBody = SignupRequest.builder()
                .email("user@catwalk.ro")
                .password("Parola123")
                .firstName("User")
                .lastName("Test")
                .build();

        // act
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(AuthController.PATH + "/register", oRequestBody))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.ERROR.toString());
        assertThat(oJSONResponse.get("message")).isEqualTo(ErrorCode.EMAIL_NOT_UNIQUE.getMessage());
    }

    @Test
    void shouldThrowValidationErrorForWrongValuesWhenRegisters() throws Exception {
        // arrange
        SignupRequest oRequestBody = SignupRequest.builder()
                .email("validation_error@catwalk.ro")
                .password("1234")
                .lastName("User")
                .build();

        // act
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(AuthController.PATH + "/register", oRequestBody))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.FAIL.toString());
        assertThat(oJSONResponse.getJSONObject("data").get("firstName")).isEqualTo("Prenumele este obligatoriu");
        assertThat(oJSONResponse.getJSONObject("data").get("password")).isEqualTo("Parola trebuie sa aiba intre 6 si 32 de caractere");
    }

    @Test
    void shouldSuccessfullyLogin() throws Exception {
        // arrange
        LoginRequest oRequestBody = LoginRequest.builder()
                .email("user@catwalk.ro")
                .password("Parola123")
                .build();

        // act
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(AuthController.PATH + "/login", oRequestBody))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());
        String token = oJSONResponse.getJSONObject("data").get("token").toString();

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.SUCCESS.toString());
        assertThat(jwtUtils.getEmail(token)).isEqualTo("user@catwalk.ro");
        assertThat(jwtUtils.getName(token)).isEqualTo("User Test");
        assertThat(jwtUtils.getRole(token)).isEqualTo("USER");
    }

    @Test
    void shouldThrowValidationErrorForWrongValuesWhenLogins() throws Exception {
        // arrange
        LoginRequest oRequestBody = LoginRequest.builder().build();

        // act
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(AuthController.PATH + "/login", oRequestBody))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.FAIL.toString());
        assertThat(oJSONResponse.getJSONObject("data").get("email")).isEqualTo("Adresa de e-mail este obligatorie");
        assertThat(oJSONResponse.getJSONObject("data").get("password")).isEqualTo("Parola este obligatorie");
    }

    @Test
    void shouldThrowErrorIfWrongCredentialsWhenLogins() throws Exception {
        // arrange
        LoginRequest oRequestBody = LoginRequest.builder()
                .email("user@catwalk.ro")
                .password("parola_gresita")
                .build();

        // act
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(AuthController.PATH + "/login", oRequestBody))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.ERROR.toString());
        assertThat(oJSONResponse.get("message")).isEqualTo(ErrorCode.WRONG_CREDENTIALS.getMessage());
    }

}