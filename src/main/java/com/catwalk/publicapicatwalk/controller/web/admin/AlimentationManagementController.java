package com.catwalk.publicapicatwalk.controller.web.admin;

import com.catwalk.publicapicatwalk.controller.web.Api;
import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.catwalk.publicapicatwalk.controller.web.dto.ResponseDto;
import com.catwalk.publicapicatwalk.dto.AlimentationAdminReqDto;
import com.catwalk.publicapicatwalk.dto.AlimentationReqDto;
import com.catwalk.publicapicatwalk.exception.GenericException;
import com.catwalk.publicapicatwalk.model.Alimentation;
import com.catwalk.publicapicatwalk.model.Media;
import com.catwalk.publicapicatwalk.model.Scoreboard;
import com.catwalk.publicapicatwalk.model.User;
import com.catwalk.publicapicatwalk.repository.AlimentationRepository;
import com.catwalk.publicapicatwalk.repository.MediaRepository;
import com.catwalk.publicapicatwalk.repository.ScoreboardRepository;
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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = AlimentationManagementController.PATH)
public class AlimentationManagementController {

    public static final String PATH = Api.BASE_ADMIN_PATH + "/alimentation";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlimentationRepository alimentationRepository;

    @Autowired
    ScoreboardRepository scoreboardRepository;

    @Autowired
    MediaRepository mediaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> get(Principal principal, Authentication auth) {
        List<Alimentation> alimentationList = alimentationRepository.findAll();
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(ListAlimAdminDto.builder().obj(alimentationList).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<?> getByUserId(@PathVariable("userId") String id, Principal principal, Authentication auth) {
        User oUser = userRepository.findById(id).orElseThrow(GenericException::new);
        List<Alimentation> alimentationList = alimentationRepository.findAllByUser(oUser);
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(ListAlimAdminDto.builder().obj(alimentationList).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<?> getByAlimId(@PathVariable("id") String id, Principal principal, Authentication auth) {
        Alimentation oAlim = alimentationRepository.findById(id).orElseThrow(GenericException::new);
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(SingleAlimAdminDto.builder().obj(oAlim).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/id/{id}")
    public ResponseEntity<?> updateByAlimId(@PathVariable("id") String id, Principal principal, Authentication auth, @Valid @RequestBody AlimentationAdminReqDto dto) {
        Alimentation oAlim = alimentationRepository.findById(id).orElseThrow(GenericException::new);
        Scoreboard oScoreboard = scoreboardRepository.findByUser(oAlim.getUser()).orElseThrow(GenericException::new);

        if (dto.getScore() != null) {
            oScoreboard.decreaseAlim(oAlim.getScore());
            oScoreboard.increaseAlim(dto.getScore());
            oAlim.setScore(dto.getScore());
        }
        if (dto.getName() != null) oAlim.setName(dto.getName());
        if (dto.getNoCalories() != null) oAlim.setNoCalories(dto.getNoCalories());

        scoreboardRepository.save(oScoreboard);
        alimentationRepository.save(oAlim);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/id/{id}")
    public ResponseEntity<?> deleteByAlimId(@PathVariable("id") String id, Principal principal, Authentication auth) {
        Alimentation oAlim = alimentationRepository.findById(id).orElseThrow(GenericException::new);
        List<Media> aMedia = mediaRepository.findByAlimentation(oAlim);
        Scoreboard oScoreboard = scoreboardRepository.findByUser(oAlim.getUser()).orElseThrow(GenericException::new);
        oScoreboard.decreaseAlim(oAlim.getScore());

        scoreboardRepository.save(oScoreboard);
        mediaRepository.deleteAll(aMedia);
        alimentationRepository.delete(oAlim);

        return ResponseEntity.ok().build();
    }

}

@Builder
class SingleAlimAdminDto {
    @JsonProperty("alimentation")
    private Alimentation obj;
}

@Builder
class ListAlimAdminDto {
    @JsonProperty("alimentations")
    private List<Alimentation> obj;
}
