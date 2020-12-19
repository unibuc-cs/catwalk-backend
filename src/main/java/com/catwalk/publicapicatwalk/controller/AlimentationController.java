package com.catwalk.publicapicatwalk.controller;

import com.catwalk.publicapicatwalk.controller.web.Api;
import com.catwalk.publicapicatwalk.controller.web.ErrorCode;
import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.catwalk.publicapicatwalk.controller.web.dto.ResponseDto;
import com.catwalk.publicapicatwalk.dto.AlimentationReqDto;
import com.catwalk.publicapicatwalk.dto.MediaReqDto;
import com.catwalk.publicapicatwalk.exception.GenericException;
import com.catwalk.publicapicatwalk.model.Alimentation;
import com.catwalk.publicapicatwalk.model.Media;
import com.catwalk.publicapicatwalk.model.User;
import com.catwalk.publicapicatwalk.repository.AlimentationRepository;
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
@RequestMapping(value = AlimentationController.PATH)
public class AlimentationController {

    public static final String PATH = Api.BASE_PATH + "/alimentation";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlimentationRepository alimentationRepository;

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
        List<Alimentation> alimentationList = alimentationRepository.findAllByUser(oUser.get());
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(ListAlimDto.builder().obj(alimentationList).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "/my/{id}")
    public ResponseEntity<?> myExerciseById(@PathVariable("id") String id, Principal principal, Authentication auth) {
        Optional<User> oUser = userRepository.findByEmail(principal.getName());
        Optional<Alimentation> oAlimentation = alimentationRepository.findById(id);
        if (!oUser.isPresent() || !oAlimentation.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        if (oUser.get().getId().compareTo(oAlimentation.get().getUser().getId()) != 0) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(SingleAlimDto.builder().obj(oAlimentation.get()).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "/my")
    public ResponseEntity<?> create(@RequestBody @Valid AlimentationReqDto oReq, Principal principal, Authentication auth) {
        Optional<User> oUser = userRepository.findByEmail(principal.getName());
        if (!oUser.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        Alimentation oAlToSave = convertToAlimentation(oReq);
        oAlToSave.setUser(oUser.get());
        Alimentation oSavedAl = alimentationRepository.save(oAlToSave);
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(SingleAlimDto.builder().obj(oSavedAl).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "/my/{id}")
    public ResponseEntity<?> addMedia(@PathVariable("id") String id, @RequestBody @Valid MediaReqDto oReq, Principal principal, Authentication auth) {
        Optional<User> oUser = userRepository.findByEmail(principal.getName());
        Optional<Alimentation> oAlimentation = alimentationRepository.findById(id);
        if (!oUser.isPresent() || !oAlimentation.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        if (oUser.get().getId().compareTo(oAlimentation.get().getUser().getId()) != 0) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        Media oMediaToSave = convertToMedia(oReq);
        oMediaToSave.setAlimentation(oAlimentation.get());
        mediaRepository.save(oMediaToSave);
        oAlimentation = alimentationRepository.findById(id);
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(SingleAlimDto.builder().obj(oAlimentation.get()).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    Alimentation convertToAlimentation(AlimentationReqDto oDto) {
        return modelMapper.map(oDto, Alimentation.class);
    }

    Media convertToMedia(MediaReqDto oDto) {
        return modelMapper.map(oDto, Media.class);
    }

}

@Builder
class SingleAlimDto {
    @JsonProperty("alimentation")
    private Alimentation obj;
}

@Builder
class ListAlimDto {
    @JsonProperty("alimentations")
    private List<Alimentation> obj;
}
