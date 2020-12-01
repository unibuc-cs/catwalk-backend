package com.catwalk.publicapicatwalk.controller;

import com.catwalk.publicapicatwalk.controller.web.Api;
import com.catwalk.publicapicatwalk.controller.web.StatusCode;
import com.catwalk.publicapicatwalk.controller.web.dto.ResponseDto;
import com.catwalk.publicapicatwalk.dto.JwtResponse;
import com.catwalk.publicapicatwalk.dto.LoginRequest;
import com.catwalk.publicapicatwalk.dto.SignupRequest;
import com.catwalk.publicapicatwalk.exception.GenericException;
import com.catwalk.publicapicatwalk.model.User;
import com.catwalk.publicapicatwalk.repository.UserRepository;
import com.catwalk.publicapicatwalk.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.catwalk.publicapicatwalk.controller.web.ErrorCode.EMAIL_NOT_UNIQUE;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = AuthController.PATH)
public class AuthController {

    public static final String PATH = Api.BASE_PATH + "/auth";

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        String jwt = jwtUtils.generateJwtToken(authentication);

        ResponseDto oResponse = ResponseDto.builder()
                .status(StatusCode.SUCCESS)
                .data(new JwtResponse(jwt))
                .build();

        return ResponseEntity.ok(oResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new GenericException(EMAIL_NOT_UNIQUE);
        }

        User user = new User(signUpRequest.getEmail(), signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));

        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setRole("USER");
        userRepository.save(user);

        ResponseDto oResponse = ResponseDto.builder().status(StatusCode.SUCCESS).build();

        return ResponseEntity.ok(oResponse);
    }

}
