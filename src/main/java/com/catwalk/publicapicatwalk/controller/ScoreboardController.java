package com.catwalk.publicapicatwalk.controller;

import com.catwalk.publicapicatwalk.controller.web.Api;
import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.catwalk.publicapicatwalk.controller.web.dto.ResponseDto;
import com.catwalk.publicapicatwalk.exception.GenericException;
import com.catwalk.publicapicatwalk.model.Scoreboard;
import com.catwalk.publicapicatwalk.model.User;
import com.catwalk.publicapicatwalk.repository.ScoreboardRepository;
import com.catwalk.publicapicatwalk.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = ScoreboardController.PATH)
public class ScoreboardController {

    public static final String PATH = Api.BASE_PATH + "/scoreboard";

    @Autowired
    UserRepository userRepository;

    @Autowired
    ScoreboardRepository scoreboardRepository;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Scoreboard> scoreboard = scoreboardRepository.findAllOrderByTotalScoreDesc();
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(ListScoreDto.builder().obj(scoreboard).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/sortByAlimentation")
    public ResponseEntity<?> getAllSortByAlimentation() {
        List<Scoreboard> scoreboard = scoreboardRepository.findAllOrderByAlimentationScoreDesc();
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(ListScoreDto.builder().obj(scoreboard).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/sortByExercises")
    public ResponseEntity<?> getAllSortByExercises() {
        List<Scoreboard> scoreboard = scoreboardRepository.findAllOrderByExerciseScoreDesc();
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(ListScoreDto.builder().obj(scoreboard).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getByUserId(@PathVariable("id") String userId) {
        User oUser = userRepository.findById(userId).orElseThrow(GenericException::new);
        Scoreboard oScoreboard = scoreboardRepository.findByUser(oUser).orElseThrow(GenericException::new);
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(SingleScoreDto.builder().obj(oScoreboard).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

}

@Builder
class SingleScoreDto {
    @JsonProperty("score")
    private Scoreboard obj;
}

@Builder
class ListScoreDto {
    @JsonProperty("scores")
    private List<Scoreboard> obj;
}
