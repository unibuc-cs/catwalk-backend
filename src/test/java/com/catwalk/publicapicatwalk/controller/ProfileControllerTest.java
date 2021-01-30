package com.catwalk.publicapicatwalk.controller;

import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.catwalk.publicapicatwalk.dto.LoginRequest;
import com.catwalk.publicapicatwalk.dto.UserResponseDto;
import com.catwalk.publicapicatwalk.model.User;
import com.catwalk.publicapicatwalk.model.constants.Sex;
import com.catwalk.publicapicatwalk.repository.*;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest extends GenericIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    private static User oDummyUser;

    private static String sBearerToken;

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    AlimentationRepository alimentationRepository;

    @Autowired
    MediaRepository mediaRepository;

    @Autowired
    ScoreboardRepository scoreboardRepository;

    @BeforeEach
    void setUp() throws Exception {
        mediaRepository.deleteAll();
        exerciseRepository.deleteAll();
        scoreboardRepository.deleteAll();
        alimentationRepository.deleteAll();
        userRepository.deleteAll();
        User oDummyUser = User.builder()
                .email("user@catwalk.ro")
                .password(encoder.encode("Parola123"))
                .firstName("User")
                .lastName("Test")
                .role("ROLE_USER")
                .sex(Sex.Masculin)
                .greutate(60.5)
                .inaltime(1.75)
                .varsta(20)
                .isEnabled(true)
                .avatar("AVATAR_DEMO")
                .build();
        LoginRequest loginRequest = LoginRequest.builder().email("user@catwalk.ro").password("Parola123").build();
        this.oDummyUser = userRepository.save(oDummyUser);
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(AuthController.PATH + "/login", loginRequest))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());
        this.sBearerToken = oJSONResponse.getJSONObject("data").get("token").toString();
    }

    @Test
    void shouldReturnProfileInformationSuccessfully() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(ProfileController.PATH, sBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.SUCCESS.toString());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("id")).isEqualTo(oDummyUser.getId());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("email")).isEqualTo(oDummyUser.getEmail());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("firstName")).isEqualTo(oDummyUser.getFirstName());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("lastName")).isEqualTo(oDummyUser.getLastName());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("sex")).isEqualTo(oDummyUser.getSex().toString());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("inaltime")).isEqualTo(oDummyUser.getInaltime());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("greutate")).isEqualTo(oDummyUser.getGreutate());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("varsta")).isEqualTo(oDummyUser.getVarsta());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("avatar")).isEqualTo(oDummyUser.getAvatar());
    }

    @Test
    void shouldReturnUnauthorizedIfBearerIsMissingWhenRetrievingProfile() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(ProfileController.PATH))
                .andReturn();

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnUnauthorizedIfBearerIsInvalidWhenRetrievingProfile() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(ProfileController.PATH, "WRONG_TOKEN"))
                .andReturn();

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldUpdateProfileSuccessfully() throws Exception {
        // arrange
        UserResponseDto oRequest = UserResponseDto.builder()
                .email("user123@catwalk.ro")
                .firstName("Andreea")
                .lastName("Popescu")
                .sex(Sex.Feminin)
                .avatar("ALT_AVATAR")
                .varsta(24)
                .build();

        // act
        MvcResult oResponse = mockMvc
                .perform(createPutRequest(ProfileController.PATH, oRequest, sBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.SUCCESS.toString());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("id")).isEqualTo(oDummyUser.getId());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("email")).isEqualTo(oDummyUser.getEmail());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("firstName")).isEqualTo(oRequest.getFirstName());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("lastName")).isEqualTo(oRequest.getLastName());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("sex")).isEqualTo(oRequest.getSex().toString());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("inaltime")).isEqualTo(oDummyUser.getInaltime());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("greutate")).isEqualTo(oDummyUser.getGreutate());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("varsta")).isEqualTo(oRequest.getVarsta());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("user").get("avatar")).isEqualTo(oRequest.getAvatar());

        // DB-level assert
        Optional<User> oDbUser = userRepository.findByEmail(oDummyUser.getEmail());
        assertThat(oDbUser.get().getId()).isEqualTo(oDummyUser.getId());
        assertThat(oDbUser.get().getEmail()).isEqualTo(oDummyUser.getEmail());
        assertThat(oDbUser.get().getFirstName()).isEqualTo(oRequest.getFirstName());
        assertThat(oDbUser.get().getLastName()).isEqualTo(oRequest.getLastName());
        assertThat(oDbUser.get().getSex().toString()).isEqualTo(oRequest.getSex().toString());
        assertThat(oDbUser.get().getInaltime()).isEqualTo(oDummyUser.getInaltime());
        assertThat(oDbUser.get().getGreutate()).isEqualTo(oDummyUser.getGreutate());
        assertThat(oDbUser.get().getVarsta()).isEqualTo(oRequest.getVarsta());
        assertThat(oDbUser.get().getAvatar()).isEqualTo(oRequest.getAvatar());
    }

    @Test
    void shouldReturnUnauthorizedIfBearerIsMissingWhenUpdatingProfile() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createPutRequest(ProfileController.PATH, oDummyUser))
                .andReturn();

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnUnauthorizedIfBearerIsInvalidWhenUpdatingProfile() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createPutRequest(ProfileController.PATH, oDummyUser, "WRONG_TOKEN"))
                .andReturn();

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

}