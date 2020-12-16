package com.catwalk.publicapicatwalk.controller;

import com.catwalk.publicapicatwalk.controller.web.Api;
import com.catwalk.publicapicatwalk.controller.web.ErrorCode;
import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.catwalk.publicapicatwalk.controller.web.dto.ResponseDto;
import com.catwalk.publicapicatwalk.dto.ExerciseReqDto;
import com.catwalk.publicapicatwalk.dto.MediaReqDto;
import com.catwalk.publicapicatwalk.exception.GenericException;
import com.catwalk.publicapicatwalk.model.Exercise;
import com.catwalk.publicapicatwalk.model.Media;
import com.catwalk.publicapicatwalk.model.User;
import com.catwalk.publicapicatwalk.repository.ExerciseRepository;
import com.catwalk.publicapicatwalk.repository.MediaRepository;
import com.catwalk.publicapicatwalk.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = ExercisesController.PATH)
public class ExercisesController {

    public static final String PATH = Api.BASE_PATH + "/exercise";

    @Autowired
    UserRepository userRepository;

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    MediaRepository mediaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "/my")
    public ResponseEntity<?> myExercises(Principal principal, Authentication auth) {
        Optional<User> oUser = userRepository.findByEmail(principal.getName());
        if (!oUser.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        List<Exercise> exerciseList = exerciseRepository.findAllByUser(oUser.get());
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(ListDto.builder().obj(exerciseList).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "/my/{id}")
    public ResponseEntity<?> myExerciseById(@PathVariable("id") String id, Principal principal, Authentication auth) {
        Optional<User> oUser = userRepository.findByEmail(principal.getName());
        Optional<Exercise> oExercise = exerciseRepository.findById(id);
        if (!oUser.isPresent() || !oExercise.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        if (oUser.get().getId().compareTo(oExercise.get().getUser().getId()) != 0) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(SingleDto.builder().obj(oExercise.get()).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "/my")
    public ResponseEntity<?> create(@RequestBody @Valid ExerciseReqDto oReq, Principal principal, Authentication auth) {
        Optional<User> oUser = userRepository.findByEmail(principal.getName());
        if (!oUser.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        Exercise oExToSave = convertToExercise(oReq);
        oExToSave.setUser(oUser.get());
        Exercise oSavedEx = exerciseRepository.save(oExToSave);
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(SingleDto.builder().obj(oSavedEx).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "/my/{id}")
    public ResponseEntity<?> addMedia(@PathVariable("id") String id, @RequestBody @Valid MediaReqDto oReq, Principal principal, Authentication auth) {
        Optional<User> oUser = userRepository.findByEmail(principal.getName());
        Optional<Exercise> oExercise = exerciseRepository.findById(id);
        if (!oUser.isPresent() || !oExercise.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        if (oUser.get().getId().compareTo(oExercise.get().getUser().getId()) != 0) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        Media oMediaToSave = convertToMedia(oReq);
        oMediaToSave.setExercise(oExercise.get());
        mediaRepository.save(oMediaToSave);
        oExercise = exerciseRepository.findById(id);
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(SingleDto.builder().obj(oExercise.get()).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    Exercise convertToExercise(ExerciseReqDto oDto) {
        return modelMapper.map(oDto, Exercise.class);
    }

    Media convertToMedia(MediaReqDto oDto) {
        return modelMapper.map(oDto, Media.class);
    }

}

@Builder
class SingleDto {
    @JsonProperty("exercise")
    private Exercise obj;
}

@Builder
class ListDto {
    @JsonProperty("exercises")
    private List<Exercise> obj;
}
