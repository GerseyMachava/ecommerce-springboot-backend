package com.ecommerce.backend.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ecommerce.backend.model.User;
import com.ecommerce.backend.model.enums.UserRole;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.AuthorizationService;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    private User user;

    @BeforeEach
    void setUp() {
        // Setup User - SEM ID no builder, SET depois!
        user = User.builder()
                .email("test@email.com")
                .password("encodedPassword123")
                .role(UserRole.CUSTOMER)
                .enabled(true)
                .locked(false)
                .build();
        user.setId(1L); // ✅ SET ID DEPOIS!
    }

    @Nested
    @DisplayName("loadUserByUsername - Carregar usuário para autenticação")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("✅ Deve retornar UserDetails quando email existe")
        void loadUserByUsername_WithExistingEmail_ShouldReturnUserDetails() {
            // Arrange
            String email = "test@email.com";
            when(userRepository.findByEmail(email)).thenReturn(user);

            // Act
            UserDetails result = authorizationService.loadUserByUsername(email);

            // Assert
            assertNotNull(result);
            assertEquals(email, result.getUsername());
            assertEquals("encodedPassword123", result.getPassword());
            assertTrue(result.isEnabled());
            assertTrue(result.isAccountNonLocked());
            assertTrue(result.isAccountNonExpired());
            assertTrue(result.isCredentialsNonExpired());
            assertEquals(1, result.getAuthorities().size());
            assertEquals("ROLE_USER", result.getAuthorities().iterator().next().getAuthority());
            
            verify(userRepository).findByEmail(email);
        }

        @Test
        @DisplayName("✅ Deve retornar UserDetails com role ADMIN")
        void loadUserByUsername_WithAdminRole_ShouldReturnUserDetailsWithAdminAuthority() {
            // Arrange
            User admin = User.builder()
                    .email("admin@email.com")
                    .password("adminPassword")
                    .role(UserRole.ADMIN)
                    .enabled(true)
                    .locked(false)
                    .build();
            admin.setId(2L);
            
            String email = "admin@email.com";
            when(userRepository.findByEmail(email)).thenReturn(admin);

            // Act
            UserDetails result = authorizationService.loadUserByUsername(email);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getAuthorities().size());
            assertEquals("ROLE_ADMIN", result.getAuthorities().iterator().next().getAuthority());
        }

        @Test
        @DisplayName("✅ Deve retornar UserDetails com usuário desabilitado")
        void loadUserByUsername_WithDisabledUser_ShouldReturnDisabledUserDetails() {
            // Arrange
            User disabledUser = User.builder()
                    .email("disabled@email.com")
                    .password("password")
                    .role(UserRole.CUSTOMER)
                    .enabled(false) // Usuário desabilitado
                    .locked(false)
                    .build();
            disabledUser.setId(3L);
            
            String email = "disabled@email.com";
            when(userRepository.findByEmail(email)).thenReturn(disabledUser);

            // Act
            UserDetails result = authorizationService.loadUserByUsername(email);

            // Assert
            assertNotNull(result);
            assertFalse(result.isEnabled()); // ✅ Spring Security respeita isso!
            assertTrue(result.isAccountNonLocked());
        }

        @Test
        @DisplayName("✅ Deve retornar UserDetails com usuário bloqueado")
        void loadUserByUsername_WithLockedUser_ShouldReturnLockedUserDetails() {
            // Arrange
            User lockedUser = User.builder()
                    .email("locked@email.com")
                    .password("password")
                    .role(UserRole.CUSTOMER)
                    .enabled(true)
                    .locked(true) // Usuário bloqueado
                    .build();
            lockedUser.setId(4L);
            
            String email = "locked@email.com";
            when(userRepository.findByEmail(email)).thenReturn(lockedUser);

            // Act
            UserDetails result = authorizationService.loadUserByUsername(email);

            // Assert
            assertNotNull(result);
            assertFalse(result.isAccountNonLocked()); // ✅ Spring Security respeita isso!
            assertTrue(result.isEnabled());
        }

        @Test
        @DisplayName("❌ Deve lançar UsernameNotFoundException quando email não existe")
        void loadUserByUsername_WithNonExistingEmail_ShouldThrowException() {
            // Arrange
            String email = "nonexistent@email.com";
            when(userRepository.findByEmail(email)).thenReturn(null);

            // Act & Assert
            UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                    () -> authorizationService.loadUserByUsername(email));

            assertEquals("User not found with email: " + email, exception.getMessage());
            
            verify(userRepository).findByEmail(email);
        }

        @Test
        @DisplayName("❌ Deve lançar UsernameNotFoundException quando email é null")
        void loadUserByUsername_WithNullEmail_ShouldThrowException() {
            // Arrange
            when(userRepository.findByEmail(null)).thenReturn(null);

            // Act & Assert
            UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                    () -> authorizationService.loadUserByUsername(null));

            assertEquals("User not found with email: null", exception.getMessage());
            
            verify(userRepository).findByEmail(null);
        }

        @Test
        @DisplayName("❌ Deve lançar UsernameNotFoundException quando email é vazio")
        void loadUserByUsername_WithEmptyEmail_ShouldThrowException() {
            // Arrange
            String email = "";
            when(userRepository.findByEmail(email)).thenReturn(null);

            // Act & Assert
            UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                    () -> authorizationService.loadUserByUsername(email));

            assertEquals("User not found with email: ", exception.getMessage());
            
            verify(userRepository).findByEmail(email);
        }
    }
}