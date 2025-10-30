package com.users.demo;

import com.users.demo.config.ValidationProperties;
import com.users.demo.model.dto.UserRequest;
import com.users.demo.model.dto.UserResponse;
import com.users.demo.model.entity.Phone;
import com.users.demo.model.entity.User;
import com.users.demo.repository.UserRepository;
import com.users.demo.security.JwtUtil;
import com.users.demo.service.UserService;
import com.users.demo.util.Messages;
import com.users.demo.validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private ValidationProperties validationProperties;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    private UserValidator userValidator;

    @InjectMocks
    private UserService userService;

    private UserRequest userRequest;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurar regex simuladas
        when(validationProperties.getEmailRegex()).thenReturn("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$");
        when(validationProperties.getPasswordRegex()).thenReturn("^(?=.*[a-zA-Z])(?=.*\\d).{6,}$");

        // Crear validator real usando las regex
        userValidator = new UserValidator(validationProperties);

        // Inyectar manualmente el validador real en el servicio
        userService = new UserService(userRepository, jwtUtil, userValidator, passwordEncoder);

        userRequest = buildUserRequest("juan@rodriguez.org", "hunter2");
    }

    private UserRequest buildUserRequest(String email, String password) {
        Phone phone = new Phone();
        phone.setNumber("1234567");
        phone.setCityCode("1");
        phone.setCountryCode("57");

        UserRequest request = new UserRequest();
        request.setName("Juan Rodriguez");
        request.setEmail(email);
        request.setPassword(password);
        request.setPhones(List.of(phone));
        return request;
    }

    @Test
    void givenNullRequest_whenCreateUser_thenThrowsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(null)
        );
        assertEquals("La solicitud no puede ser nula", ex.getMessage());
    }

    @Test
    void givenValidRequest_whenCreateUser_thenUserIsCreatedSuccessfully() {
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
        when(jwtUtil.generateToken(userRequest.getEmail())).thenReturn("fake-jwt-token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encrypted-hunter2");

        UserResponse response = userService.createUser(userRequest);

        assertNotNull(response);
        assertEquals("Juan Rodriguez", response.getName());
        assertEquals("juan@rodriguez.org", response.getEmail());
        assertEquals("fake-jwt-token", response.getToken());
        assertTrue(response.getIsActive());
        verify(userRepository, times(1)).save(argThat(user ->
                        "encrypted-hunter2".equals(user.getPassword())));
                verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenExistingEmail_whenCreateUser_thenThrowsException() {
        when(userRepository.findByEmail(userRequest.getEmail()))
                .thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(userRequest)
        );

        assertEquals(Messages.EMAIL_ALREADY_REGISTERED, exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void givenInvalidEmail_whenCreateUser_thenThrowsException() {
        userRequest.setEmail("invalid-email");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(userRequest)
        );

        assertEquals("Formato de correo electrónico inválido", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void givenInvalidPassword_whenCreateUser_thenThrowsException() {
        userRequest.setPassword("123");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(userRequest)
        );

        assertEquals("Formato de contraseña inválido. Debe contener al menos una letra, un número y mínimo 6 caracteres", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void givenTokenGenerationFails_whenCreateUser_thenThrowsException() {
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
        when(jwtUtil.generateToken(anyString())).thenReturn(null);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.createUser(userRequest)
        );

        assertEquals(Messages.TOKEN_GENERATION_FAILED, exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void givenExistingUsers_whenFindAll_thenReturnListOfUsers() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Maria");
        user.setEmail("maria@test.com");
        user.setToken("jwt123");
        user.setCreated(LocalDateTime.now());
        user.setIsActive(true);

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals("Maria", result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

}
