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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private UserResponseDto convertToDto(User user) {
        UserResponseDto userDto = modelMapper.map(user, UserResponseDto.class);
        return userDto;
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
