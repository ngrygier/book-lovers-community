package com.booklover.book_lover_community.service;

import com.booklover.book_lover_community.Dto.EditProfileDto;
import com.booklover.book_lover_community.Dto.RegisterRequestDto;
import com.booklover.book_lover_community.model.Library;
import com.booklover.book_lover_community.model.Role;
import com.booklover.book_lover_community.repository.LibraryRepository;
import com.booklover.book_lover_community.repository.UserRepository;
import com.booklover.book_lover_community.user.ShelfStatus;
import com.booklover.book_lover_community.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LibraryRepository libraryRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private RegisterRequestDto registerDto;
    private EditProfileDto editDto;

    @BeforeEach
    void setUp() {
        // Użytkownik testowy
        user = User.builder()
                .id(1)
                .username("testuser")
                .email("test@test.com")
                .firstname("Jan")
                .lastname("Kowalski")
                .role(Role.USER)
                .enabled(true)
                .accountLocked(false)
                .build();

        // DTO do rejestracji
        registerDto = new RegisterRequestDto();
        registerDto.setUsername("newuser");
        registerDto.setEmail("new@test.com");
        registerDto.setPassword("password");
        registerDto.setFirstname("Anna");
        registerDto.setLastname("Nowak");

        // DTO do edycji profilu
        editDto = new EditProfileDto();
        editDto.setUsername("updatedUser");
        editDto.setProfileImage(new MockMultipartFile(
                "file", "image.png", "image/png", "data".getBytes()
        ));

        // SecurityContext dla aktualnego użytkownika
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", "password")
        );

        // Mocki wspólne
        lenient().when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        lenient().when(userRepository.findById(1)).thenReturn(Optional.of(user));
    }

    // -------------------- REGISTER --------------------

    @Test
    void registerUser_success() {
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encoded");

        // ważne: ustawiamy ID dla zapisanego usera
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1); // symulacja bazy danych
            return u;
        });

        when(libraryRepository.findByUserIdAndName(anyLong(), anyString())).thenReturn(null);

        User result = userService.registerUser(registerDto);

        assertEquals("encoded", result.getPassword());
    }

    @Test
    void registerUser_usernameExists() {
        when(userRepository.existsByUsername("newuser")).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registerDto));
    }

    @Test
    void registerUser_emailExists() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registerDto));
    }

    // -------------------- DEFAULT SHELVES --------------------

    @Test
    void createDefaultShelves_createsAll() {
        when(libraryRepository.findByUserIdAndName(anyLong(), anyString())).thenReturn(null);

        userService.createDefaultShelves(user);

        verify(libraryRepository, atLeast(ShelfStatus.values().length)).save(any(Library.class));
    }

    @Test
    void getDefaultLibraries_returnsAll() {
        when(libraryRepository.findByUserIdAndName(anyLong(), anyString())).thenReturn(null);

        List<Library> libs = userService.getDefaultLibraries(user);

        assertEquals(ShelfStatus.values().length, libs.size());
    }

    // -------------------- CUSTOM LIBRARY --------------------

    @Test
    void createCustomLibrary_success() {
        when(libraryRepository.findByUserIdAndName(anyLong(), eq("Custom"))).thenReturn(null);
        when(libraryRepository.save(any(Library.class))).thenAnswer(inv -> inv.getArgument(0));

        Library lib = userService.createCustomLibrary("Custom", user);

        assertEquals("Custom", lib.getName());
    }

    @Test
    void createCustomLibrary_exists() {
        when(libraryRepository.findByUserIdAndName(anyLong(), anyString())).thenReturn(new Library());
        assertThrows(RuntimeException.class,
                () -> userService.createCustomLibrary("Custom", user));
    }

    // -------------------- CURRENT USER --------------------

    @Test
    void getCurrentUser_returnsUser() {
        User result = userService.getCurrentUser();
        assertEquals("testuser", result.getUsername());
    }

    // -------------------- UPDATE PROFILE --------------------

    @Test
    void updateProfile_updatesUsernameAndImage() throws IOException {
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.updateProfile(editDto);

        assertEquals("updatedUser", user.getUsername());
    }

    // -------------------- LOAD BY USERNAME --------------------

    @Test
    void loadUserByUsername_success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        assertEquals("testuser", userService.loadUserByUsername("testuser").getUsername());
    }

    @Test
    void loadUserByUsername_notFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> userService.loadUserByUsername("unknown"));
    }

    // -------------------- GET USER BY ID --------------------

    @Test
    void getUserById_success() {
        assertEquals("testuser", userService.getUserById(1L).getUsername());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUserById(2L));
    }
}