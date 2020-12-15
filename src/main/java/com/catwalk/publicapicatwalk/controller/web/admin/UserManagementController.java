package com.catwalk.publicapicatwalk.controller.web.admin;

import com.catwalk.publicapicatwalk.controller.web.Api;
import com.catwalk.publicapicatwalk.controller.web.ErrorCode;
import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.catwalk.publicapicatwalk.controller.web.dto.ResponseDto;
import com.catwalk.publicapicatwalk.dto.UserResponseDto;
import com.catwalk.publicapicatwalk.dto.admin.AdminUserResponseDto;
import com.catwalk.publicapicatwalk.exception.GenericException;
import com.catwalk.publicapicatwalk.model.User;
import com.catwalk.publicapicatwalk.repository.UserRepository;
import com.catwalk.publicapicatwalk.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = UserManagementController.PATH)
public class UserManagementController {

    public static final String PATH = Api.BASE_ADMIN_PATH + "/user";

    private UserDetailsServiceImpl userDetailsService;
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    public UserManagementController(UserDetailsServiceImpl userDetailsService, UserRepository userRepository, ModelMapper modelMapper) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> get(Principal principal, Authentication auth) {
        List<User> aUsers = userRepository.findAll();
        List<AdminUserResponseDto> aUsersDto = aUsers.stream().map(this::convertToDto).collect(Collectors.toList());
        ResponseDto oResponse = ResponseDto.builder().status(StatusCode.SUCCESS).data(createUserResponse(aUsersDto)).build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{email}")
    public ResponseEntity<?> getByEmail(@PathVariable("email") String email, Principal principal, Authentication auth) {
        Optional<User> oUser = userRepository.findByEmail(email);
        if (!oUser.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        ResponseDto oResponse = ResponseDto.builder().status(StatusCode.SUCCESS).data(createUserResponse(oUser.get())).build();
        return ResponseEntity.ok(oResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ban/{email}")
    public ResponseEntity<?> banByEmail(@PathVariable("email") String email, Principal principal, Authentication auth) {
        Optional<User> oUser = userRepository.findByEmail(email);
        if (!oUser.isPresent()) {
            throw new GenericException(ErrorCode.GENERAL_UNEXPECTED_ERROR);
        }
        oUser.get().setIsEnabled(false);
        User savedUser = userRepository.save(oUser.get());
        ResponseDto oResponse = ResponseDto.builder().status(StatusCode.SUCCESS).data(createUserResponse(savedUser)).build();
        return ResponseEntity.ok(oResponse);
    }

    private User convertToUser(AdminUserResponseDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        return user;
    }

    private AdminUserResponseDto convertToDto(User user) {
        AdminUserResponseDto userDto = modelMapper.map(user, AdminUserResponseDto.class);
        userDto.setIsEnabled(user.getIsEnabled());
        return userDto;
    }

    private SingleUser createUserResponse(User oUser) {
        return SingleUser.builder().user(convertToDto(oUser)).build();
    }

    private ListUsers createUserResponse(List<AdminUserResponseDto> aUsers) {
        return ListUsers.builder().users(aUsers).build();
    }

}

@Builder
class SingleUser {
    @JsonProperty("user")
    private AdminUserResponseDto user;
}

@Builder
class ListUsers {
    @JsonProperty("users")
    private List<AdminUserResponseDto> users;
}
