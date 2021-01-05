package com.catwalk.publicapicatwalk.controller;

import com.catwalk.publicapicatwalk.controller.web.Api;
import com.catwalk.publicapicatwalk.controller.web.ErrorCode;
import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.catwalk.publicapicatwalk.controller.web.dto.ResponseDto;
import com.catwalk.publicapicatwalk.dto.BadgeReqDto;
import com.catwalk.publicapicatwalk.exception.GenericException;
import com.catwalk.publicapicatwalk.model.Badge;
import com.catwalk.publicapicatwalk.model.User;
import com.catwalk.publicapicatwalk.model.constants.BadgeType;
import com.catwalk.publicapicatwalk.repository.BadgeRepository;
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
@RequestMapping(value = BadgeController.PATH)
public class BadgeController {

    public static final String PATH = Api.BASE_PATH + "/badge";

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "/my")
    public ResponseEntity<?> myBadges(Principal principal, Authentication auth) {
        Optional<User> oUser = userRepository.findByEmail(principal.getName());
        if (!oUser.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        List<Badge> badges = badgeRepository.findAllByUser(oUser.get());
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(ListBadgeDto.builder().obj(badges).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "/my/type/{badgeType}")
    public ResponseEntity<?> myBadgesByType(@PathVariable("badgeType") String badgeType, Principal principal, Authentication auth) {
        Optional<User> oUser = userRepository.findByEmail(principal.getName());
        if (!oUser.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }

        List<Badge> badges;
        switch (badgeType) {
            case "BADGE":
                badges = badgeRepository.findAllByBadgeTypeAndUser(BadgeType.BADGE, oUser.get());
                break;
            case "AVATAR_REWARD":
                badges = badgeRepository.findAllByBadgeTypeAndUser(BadgeType.AVATAR_REWARD, oUser.get());
                break;
            default:
                throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }

        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(ListBadgeDto.builder().obj(badges).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "/my/{id}")
    public ResponseEntity<?> myBadgeById(@PathVariable("id") String id, Principal principal, Authentication auth) {
        Optional<User> oUser = userRepository.findByEmail(principal.getName());
        Optional<Badge> oBadge = badgeRepository.findById(id);
        if (!oUser.isPresent() || !oBadge.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        if (oUser.get().getId().compareTo(oBadge.get().getUser().getId()) != 0) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(SingleBadgeDto.builder().obj(oBadge.get()).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "/my")
    public ResponseEntity<?> create(@RequestBody @Valid BadgeReqDto oReq, Principal principal, Authentication auth) {
        Optional<User> oUser = userRepository.findByEmail(principal.getName());
        if (!oUser.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        Badge oBadgeToSave = convertToBadge(oReq);
        oBadgeToSave.setUser(oUser.get());
        Badge oSavedBadge = badgeRepository.save(oBadgeToSave);

        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(SingleBadgeDto.builder().obj(oSavedBadge).build())
                .build();
        return ResponseEntity.ok(oResponse);
    }

    Badge convertToBadge(BadgeReqDto oDto) {
        return modelMapper.map(oDto, Badge.class);
    }

}

@Builder
class SingleBadgeDto {
    @JsonProperty("badge")
    private Badge obj;
}

@Builder
class ListBadgeDto {
    @JsonProperty("badges")
    private List<Badge> obj;
}
