package info.sasinski.security.controller;

import info.sasinski.security.JwtConfig.JwtProvider;
import info.sasinski.transfer.request.Login;
import info.sasinski.transfer.request.SignUp;
import info.sasinski.transfer.response.JwtResponse;
import info.sasinski.transfer.response.ResponseMessage;
import info.sasinski.security.model.Role;
import info.sasinski.security.model.RoleName;
import info.sasinski.security.model.User;
import info.sasinski.security.repository.RoleRepository;
import info.sasinski.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthRestAPI {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody Login loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUp signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Email is already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                    roles.add(adminRole);

                    break;
                default:
                    Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                    roles.add(userRole);
            }
        });

        user.setRoles(roles);
        userRepository.save(user);

        return new ResponseEntity<>(new ResponseMessage("User registered successfully!"), HttpStatus.OK);
    }

}
