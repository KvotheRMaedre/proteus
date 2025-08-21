package tech.kvothe.proteus.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.kvothe.proteus.dto.UserDto;
import tech.kvothe.proteus.dto.RecoveryJwtTokenDto;
import tech.kvothe.proteus.entity.User;
import tech.kvothe.proteus.exception.EmailAlreadyLinkedException;
import tech.kvothe.proteus.repository.UserRepository;
import tech.kvothe.proteus.security.authentication.JwtTokenService;
import tech.kvothe.proteus.security.config.SecurityConfiguration;
import tech.kvothe.proteus.security.userdetails.UserDetailsImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final SecurityConfiguration securityConfiguration;

    private static final String DIRECTORY_PATH = "./uploads/";

    public UserService(AuthenticationManager authenticationManager,
                       JwtTokenService jwtTokenService,
                       UserRepository userRepository,
                       SecurityConfiguration securityConfiguration) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.securityConfiguration = securityConfiguration;
    }

    public RecoveryJwtTokenDto authenticateUser(UserDto userDto) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDto.email(), userDto.password());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new RecoveryJwtTokenDto(jwtTokenService.generateToken(userDetails));
    }

    public void createUser(UserDto userDto) throws IOException {

        validateUser(userDto);

        User newUser = new User(
                    userDto.email(),
                    securityConfiguration.passwordEncoder().encode(userDto.password())
                    );

        userRepository.save(newUser);
        createFolderForUploads(newUser);
    }

    private void createFolderForUploads(User newUser) throws IOException {
        // It's not the objective of this project to implement the actual upload system to the cloud
        // or something like that, so I'm just using the local directory to be fast.

        Path folderPath = Paths.get( DIRECTORY_PATH + newUser.getId());
        Files.createDirectories(folderPath);
    }

    public void validateUser(UserDto userDto){
        var user = userRepository.findByEmail(userDto.email());
        if (user.isPresent())
            throw new EmailAlreadyLinkedException();

    }
}