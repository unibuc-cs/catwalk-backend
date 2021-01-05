package com.catwalk.publicapicatwalk.controller.web.admin;

import com.catwalk.publicapicatwalk.controller.web.Api;
import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.catwalk.publicapicatwalk.controller.web.dto.ResponseDto;
import com.catwalk.publicapicatwalk.dto.ExerciseAdminReqDto;
import com.catwalk.publicapicatwalk.exception.GenericException;
import com.catwalk.publicapicatwalk.model.*;
import com.catwalk.publicapicatwalk.repository.*;
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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = ExerciseManagementController.PATH)
public class ExerciseManagementController {

    public static final String PATH = Api.BASE_ADMIN_PATH + "/exercise";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    ScoreboardRepository scoreboardRepository;

    @Autowired
    MediaRepository mediaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> get(Principal principal, Authentication auth) {
        List<Exercise> exerciseList = exerciseRepository.findAll();
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(ListExAdminDto.builder().obj(exerciseList).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<?> getByUserId(@PathVariable("userId") String id, Principal principal, Authentication auth) {
        User oUser = userRepository.findById(id).orElseThrow(GenericException::new);
        List<Exercise> exerciseList = exerciseRepository.findAllByUser(oUser);
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(ListExAdminDto.builder().obj(exerciseList).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<?> getByExId(@PathVariable("id") String id, Principal principal, Authentication auth) {
        Exercise oEx = exerciseRepository.findById(id).orElseThrow(GenericException::new);
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(SingleExAdminDto.builder().obj(oEx).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/id/{id}")
    public ResponseEntity<?> updateByExId(@PathVariable("id") String id, Principal principal, Authentication auth, @Valid @RequestBody ExerciseAdminReqDto dto) {
        Exercise oEx = exerciseRepository.findById(id).orElseThrow(GenericException::new);
        Scoreboard oScoreboard = scoreboardRepository.findByUser(oEx.getUser()).orElseThrow(GenericException::new);

        if (dto.getScore() != null) {
            oScoreboard.decreaseEx(oEx.getScore());
            oScoreboard.increaseEx(dto.getScore());
            oEx.setScore(dto.getScore());
        }
        if (dto.getName() != null) oEx.setName(dto.getName());
        if (dto.getMinutes() != null) oEx.setMinutes(dto.getMinutes());
        if (dto.getNoExercises() != null) oEx.setNoExercises(dto.getNoExercises());

        scoreboardRepository.save(oScoreboard);
        exerciseRepository.save(oEx);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/id/{id}")
    public ResponseEntity<?> deleteByExId(@PathVariable("id") String id, Principal principal, Authentication auth) {
        Exercise oEx = exerciseRepository.findById(id).orElseThrow(GenericException::new);
        List<Media> aMedia = mediaRepository.findByExercise(oEx);
        Scoreboard oScoreboard = scoreboardRepository.findByUser(oEx.getUser()).orElseThrow(GenericException::new);
        oScoreboard.decreaseEx(oEx.getScore());

        scoreboardRepository.save(oScoreboard);
        mediaRepository.deleteAll(aMedia);
        exerciseRepository.delete(oEx);

        return ResponseEntity.ok().build();
    }

}

@Builder
class SingleExAdminDto {
    @JsonProperty("exercise")
    private Exercise obj;
}

@Builder
class ListExAdminDto {
    @JsonProperty("exercises")
    private List<Exercise> obj;
}
