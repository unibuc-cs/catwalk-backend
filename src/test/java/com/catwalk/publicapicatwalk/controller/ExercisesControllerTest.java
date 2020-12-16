package com.catwalk.publicapicatwalk.controller;

import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.catwalk.publicapicatwalk.dto.ExerciseReqDto;
import com.catwalk.publicapicatwalk.dto.LoginRequest;
import com.catwalk.publicapicatwalk.dto.MediaReqDto;
import com.catwalk.publicapicatwalk.model.Exercise;
import com.catwalk.publicapicatwalk.model.Media;
import com.catwalk.publicapicatwalk.model.User;
import com.catwalk.publicapicatwalk.model.constants.Sex;
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
class ExercisesControllerTest extends GenericIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    MediaRepository mediaRepository;

    @Autowired
    PasswordEncoder encoder;

    private static User oDummyUser;

    private static String sBearerToken, sAnotherBearerToken;

    private static Exercise oExercise;

    private static Media oMedia;

    @BeforeEach
    void setUp() throws Exception {
        mediaRepository.deleteAll();
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
        Exercise oEx = Exercise.builder().name("Sport").minutes(20).noExercises(2).score(100).user(this.oDummyUser).build();
        this.oExercise = exerciseRepository.save(oEx);
        Media oMed = Media.builder().url("something here").exercise(this.oExercise).build();
        this.oMedia = mediaRepository.save(oMed);
    }

    @Test
    void shouldSuccessfullyReturnAllExercisesForUser() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(ExercisesController.PATH + "/my", sBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.SUCCESS.toString());
        assertThat(oJSONResponse.getJSONObject("data").getJSONArray("exercises").length()).isEqualTo(1);
    }

    @Test
    void shouldSuccessfullyReturnOneExerciseForUserById() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(ExercisesController.PATH + "/my/" + oExercise.getId(), sBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.SUCCESS.toString());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("exercise").get("id")).isEqualTo(oExercise.getId());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("exercise").get("name")).isEqualTo(oExercise.getName());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("exercise").getJSONArray("media").length()).isEqualTo(1);
    }

    @Test
    void shouldReturnErrorOneExerciseForUserByIdWhenIdNotFound() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(ExercisesController.PATH + "/my/" + "asdfgh", sBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.FAIL.toString());
    }

    @Test
    void shouldReturnErrorOneExerciseForUserByIdWhenIdNotMatchesTheUser() throws Exception {
        // act
        MvcResult oResponse = mockMvc
                .perform(createGetRequest(ExercisesController.PATH + "/my/" + oExercise.getId(), sAnotherBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.FAIL.toString());
    }

    @Test
    void shouldSuccessfullyCreateExerciseForUser() throws Exception {
        // arrange
        ExerciseReqDto oReq = ExerciseReqDto.builder().name("Yoga").minutes(60).noExercises(1).score(80).build();

        // act
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(ExercisesController.PATH + "/my", oReq, sBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.SUCCESS.toString());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("exercise").get("name")).isEqualTo(oReq.getName());
    }

    @Test
    void shouldSuccessfullyAddMediaToExercise() throws Exception {
        // arrange
        MediaReqDto oReq = MediaReqDto.builder().url("google").build();

        // act
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(ExercisesController.PATH + "/my/" + oExercise.getId(), oReq, sBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.SUCCESS.toString());
        assertThat(oJSONResponse.getJSONObject("data").getJSONObject("exercise").getJSONArray("media").length()).isEqualTo(2);
    }

    @Test
    void shouldReturnErrorAddMediaExerciseForUserByIdWhenIdNotMatchesTheUser() throws Exception {
        // arrange
        MediaReqDto oReq = MediaReqDto.builder().url("google").build();

        // act
        MvcResult oResponse = mockMvc
                .perform(createPostRequest(ExercisesController.PATH + "/my/" + oExercise.getId(), oReq, sAnotherBearerToken))
                .andReturn();
        JSONObject oJSONResponse = new JSONObject(oResponse.getResponse().getContentAsString());

        // assert
        assertThat(oResponse.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(oJSONResponse.get("status")).isEqualTo(StatusCode.FAIL.toString());
    }


}