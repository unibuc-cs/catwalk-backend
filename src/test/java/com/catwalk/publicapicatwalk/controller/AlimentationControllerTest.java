package com.catwalk.publicapicatwalk.controller;

import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.catwalk.publicapicatwalk.dto.AlimentationReqDto;
import com.catwalk.publicapicatwalk.dto.ExerciseReqDto;
import com.catwalk.publicapicatwalk.dto.LoginRequest;
import com.catwalk.publicapicatwalk.dto.MediaReqDto;
import com.catwalk.publicapicatwalk.model.Alimentation;
import com.catwalk.publicapicatwalk.model.Exercise;
import com.catwalk.publicapicatwalk.model.Media;
import com.catwalk.publicapicatwalk.model.User;
import com.catwalk.publicapicatwalk.model.constants.Sex;
import com.catwalk.publicapicatwalk.repository.AlimentationRepository;
import com.catwalk.publicapicatwalk.repository.ExerciseRepository;
import com.catwalk.publicapicatwalk.repository.MediaRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AlimentationControllerTest extends GenericIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AlimentationRepository alimentationRepository;

    @Autowired
    MediaRepository mediaRepository;

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    PasswordEncoder encoder;

    private static User oDummyUser;

    private static String sBearerToken, sAnotherBearerToken;

    private static Alimentation oAlimentation;

    private static Media oMedia;

    @BeforeEach
    void setUp() throws Exception {
        mediaRepository.deleteAll();
        alimentationRepository.deleteAll();
        exerciseRepository.deleteAll();
        userRepository.deleteAll();
        User oDummyUser = User.builder().email("user@catwalk.ro").password(encoder.encode("Parola123"))
                .firstName("User").lastName("Test").role("ROLE_USER").sex(Sex.Masculin)
                .greutate(60.5).inaltime(1.75).varsta(20).isEnabled(true)
                .build();
        User oAnotherDummyUser = User.builder().email("user2@catwalk.ro").password(encoder.encode("Parola123"))
                .firstName("User").lastName("Test").role("ROLE_USER").sex(Sex.Masculin)
                .greutate(60.5).inaltime(1.75).varsta(20).isEnabled(true)
                .build();
        userRepository.save(oAnotherDummyUser);
        LoginRequest loginRequest = LoginRequest.builder().email("user@catwalk.ro").password("Parola123").build();
        LoginRequest anotherLoginRequest = LoginRequest.builder().email("user2@catwalk.ro").password("Parola123").build();
        this.oDummyUser = userRepository.save(oDummyUser);
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(AuthController.PATH + "/login", loginRequest))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());
        this.sBearerToken = oJSONResponse.getJSONObject("data").get("token").toString();
        oResponse = mockMvc
                .perform(createPostRequest(AuthController.PATH + "/login", anotherLoginRequest))
                .andReturn();
        oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());
        this.sAnotherBearerToken = oJSONResponse.getJSONObject("data").get("token").toString();
        Alimentation oAlimentation = Alimentation.builder().name("Apple").noCalories(10).score(100).user(this.oDummyUser).build();
        this.oAlimentation = alimentationRepository.save(oAlimentation);
        Media oMed = Media.builder().url("something here").alimentation(this.oAlimentation).build();
        this.oMedia = mediaRepository.save(oMed);
    }

    @Test
    void shouldSuccessfullyReturnAllAlimentationsForUser() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(AlimentationController.PATH + "/my", sBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.SUCCESS.toString());
        assertThat(oJSONResponse.getJSONObject("data").getJSONArray("alimentations").length()).isEqualTo(1);
    }

    @Test
    void shouldSuccessfullyReturnOneAlimentationForUserById() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(AlimentationController.PATH + "/my/" + oAlimentation.getId(), sBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.SUCCESS.toString());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("alimentation").get("id")).isEqualTo(oAlimentation.getId());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("alimentation").get("name")).isEqualTo(oAlimentation.getName());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("alimentation").getJSONArray("media").length()).isEqualTo(1);
    }

    @Test
    void shouldReturnErrorOneAlimentationForUserByIdWhenIdNotFound() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(AlimentationController.PATH + "/my/" + "asdfgh", sBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.FAIL.toString());
    }

    @Test
    void shouldReturnErrorOneAlimentationForUserByIdWhenIdNotMatchesTheUser() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(ExercisesController.PATH + "/my/" + oAlimentation.getId(), sAnotherBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.FAIL.toString());
    }

    @Test
    void shouldSuccessfullyCreateAlimentationForUser() throws Exception {
        // arrange
        AlimentationReqDto oReq = AlimentationReqDto.builder().name("Apple").noCalories(60).score(80).build();

        // act
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(AlimentationController.PATH + "/my", oReq, sBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.SUCCESS.toString());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("alimentation").get("name")).isEqualTo(oReq.getName());
    }

    @Test
    void shouldSuccessfullyAddMediaToAlimentation() throws Exception {
        // arrange
        MediaReqDto oReq = MediaReqDto.builder().url("google").build();

        // act
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(AlimentationController.PATH + "/my/" + oAlimentation.getId(), oReq, sBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.SUCCESS.toString());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("alimentation").getJSONArray("media").length()).isEqualTo(2);
    }

    @Test
    void shouldReturnErrorAddMediaAlimentationForUserByIdWhenIdNotMatchesTheUser() throws Exception {
        // arrange
        MediaReqDto oReq = MediaReqDto.builder().url("google").build();

        // act
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(AlimentationController.PATH + "/my/" + oAlimentation.getId(), oReq, sAnotherBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.FAIL.toString());
    }

}