package com.catwalk.publicapicatwalk.controller;

import com.catwalk.publicapicatwalk.controller.web.Api;
import com.catwalk.publicapicatwalk.controller.web.ErrorCode;
import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.catwalk.publicapicatwalk.controller.web.dto.ResponseDto;
import com.catwalk.publicapicatwalk.dto.UserResponseDto;
import com.catwalk.publicapicatwalk.exception.GenericException;
import com.catwalk.publicapicatwalk.model.User;
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
@RequestMapping(value = ProfileController.PATH)
public class ProfileController {

    public static final String PATH = Api.BASE_PATH + "/profile";

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> profile(Principal principal, Authentication auth) {
        Optional<User> oUser = userRepository.findByEmail(principal.getName());
        if (!oUser.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        ResponseDto oResponse = ResponseDto.builder().status(StatusCode.SUCCESS).data(createUserResponse(oUser.get())).build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<?> update(@RequestBody UserResponseDto oRequest, Principal principal, Authentication auth) {
        Optional<User> oExistingUser = userRepository.findByEmail(principal.getName());
        if (!oExistingUser.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }

        if (oRequest.getFirstName() != null) oExistingUser.get().setFirstName(oRequest.getFirstName());
        if (oRequest.getLastName() != null) oExistingUser.get().setLastName(oRequest.getLastName());
        if (oRequest.getGreutate() != null) oExistingUser.get().setGreutate(oRequest.getGreutate());
        if (oRequest.getInaltime() != null) oExistingUser.get().setInaltime(oRequest.getInaltime());
        if (oRequest.getSex() != null) oExistingUser.get().setSex(oRequest.getSex());
        if (oRequest.getVarsta() != null) oExistingUser.get().setVarsta(oRequest.getVarsta());

        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(createUserResponse(userRepository.save(oExistingUser.get())))
                .build();
        return ResponseEntity.ok(oResponse);
    }

    private UserResponseDto convertToDto(User user) {
        UserResponseDto userDto = modelMapper.map(user, UserResponseDto.class);
        return userDto;
    }

    private User convertToUser(UserResponseDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        return user;
    }

    private SingleUser createUserResponse(User oUser) {
        return SingleUser.builder().user(convertToDto(oUser)).build();
    }

}

@Builder
class SingleUser {
    @JsonProperty("user")
    private UserResponseDto user;
}

@Builder
class ListUsers {
    @JsonProperty("users")
    private List<UserResponseDto> users;
}
