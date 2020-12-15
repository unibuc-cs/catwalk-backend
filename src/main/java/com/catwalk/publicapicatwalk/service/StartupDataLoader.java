package com.catwalk.publicapicatwalk.service;

import com.catwalk.publicapicatwalk.model.User;
import com.catwalk.publicapicatwalk.model.constants.Sex;
import com.catwalk.publicapicatwalk.repository.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class StartupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadyLoaded = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @SneakyThrows
    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadyLoaded) return;

        User oAdminUser = User.builder()
                .email("admin@admin.ro")
                .firstName("Admin")
                .lastName("Super")
                .greutate(0D)
                .inaltime(0D)
                .role("ROLE_ADMIN")
                .varsta(0)
                .sex(Sex.Altul)
                .password(encoder.encode("admin"))
                .isEnabled(true)
                .build();
        User oBasicUser = User.builder()
                .email("user@user.ro")
                .firstName("Admin")
                .lastName("Super")
                .greutate(0D)
                .inaltime(0D)
                .role("ROLE_USER")
                .varsta(0)
                .sex(Sex.Altul)
                .password(encoder.encode("user"))
                .isEnabled(true)
                .build();
        userRepository.save(oAdminUser);
        userRepository.save(oBasicUser);

        alreadyLoaded = true;
    }

}
