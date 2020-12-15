package com.catwalk.publicapicatwalk.controller.web.admin;

import com.catwalk.publicapicatwalk.controller.AuthController;
import com.catwalk.publicapicatwalk.controller.GenericIntegrationTest;
import com.catwalk.publicapicatwalk.controller.ProfileController;
import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.catwalk.publicapicatwalk.dto.LoginRequest;
import com.catwalk.publicapicatwalk.model.User;
import com.catwalk.publicapicatwalk.model.constants.Sex;
import com.catwalk.publicapicatwalk.repository.UserRepository;
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

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserManagementControllerTest extends GenericIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    private static User oBasicUser, oAdminUser;

    private static String sBasicBearerToken, sAdminBearerToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        User oAdminUser = User.builder()
                .email("admin@admin.ro")
                .firstName("Admin")
                .lastName("Super")
                .greutate(0D)
                .inaltime(0D)
                .role("ROLE_ADMIN")
                .varsta(0)
                .sex(Sex.Altul)
                .password(encoder.encode("admin"))
                .isEnabled(true)
                .build();
        User oBasicUser = User.builder()
                .email("user@user.ro")
                .firstName("Admin")
                .lastName("Super")
                .greutate(0D)
                .inaltime(0D)
                .role("ROLE_USER")
                .varsta(0)
                .sex(Sex.Altul)
                .password(encoder.encode("user"))
                .isEnabled(true)
                .build();
        this.oAdminUser = userRepository.save(oAdminUser);
        this.oBasicUser = userRepository.save(oBasicUser);
        LoginRequest oUserLoginRequest = LoginRequest.builder().email(oBasicUser.getEmail()).password("user").build();
        LoginRequest oAdminLoginRequest = LoginRequest.builder().email(oAdminUser.getEmail()).password("admin").build();
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(AuthController.PATH + "/login", oUserLoginRequest))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());
        this.sBasicBearerToken = oJSONResponse.getJSONObject("data").get("token").toString();
        oResponse = mockMvc
                .perform(createPostRequest(AuthController.PATH + "/login", oAdminLoginRequest))
                .andReturn();
        oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());
        this.sAdminBearerToken = oJSONResponse.getJSONObject("data").get("token").toString();
    }

    @Test
    void shouldSuccesfullyGetAllUsers() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(UserManagementController.PATH, sAdminBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.SUCCESS.toString());
        assertThat(oJSONResponse.getJSONObject("data").getJSONArray("users").length()).isEqualTo(2);
    }

    @Test
    void shouldReturnForbiddenGetAllUsers() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(UserManagementController.PATH, sBasicBearerToken))
                .andReturn();

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldSuccesfullyGetUserByEmail() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(UserManagementController.PATH + "/" + oBasicUser.getEmail(), sAdminBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.SUCCESS.toString());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("email")).isEqualTo(oBasicUser.getEmail());
    }

    @Test
    void shouldReturnForbiddenGetUser() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(UserManagementController.PATH + "/" + oBasicUser.getEmail(), sBasicBearerToken))
                .andReturn();

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

}