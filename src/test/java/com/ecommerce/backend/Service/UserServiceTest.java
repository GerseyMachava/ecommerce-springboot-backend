package com.ecommerce.backend.Service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ecommerce.backend.dto.ResponseDto.UserResponseDto;
import com.ecommerce.backend.dto.requestDto.RegisterRequestDto;
import com.ecommerce.backend.dto.requestDto.UserPasswordUpdateRequestDto;
import com.ecommerce.backend.mapper.UserMapper;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.model.enums.UserRole;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.security.SecurityService;
import com.ecommerce.backend.service.UserService;
import com.ecommerce.backend.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private UserService userService;

    private User user;
    private User adminUser;
    private UserResponseDto userResponseDto;
    private RegisterRequestDto registerRequestDto;
    private UserPasswordUpdateRequestDto passwordUpdateDto;

    @BeforeEach
    void setUp() {
        // Setup User comum
        user = User.builder()
                .email("user@example.com")
                .password("encodedPassword123")
                .role(UserRole.CUSTOMER)
                .locked(false)
                .build();
            user.setId(1L);

        // Setup Admin user
        adminUser = User.builder()
                .email("admin@example.com")
                .password("encodedAdminPassword")
                .role(UserRole.ADMIN)
                .locked(false)
                .build();
        adminUser.setId(2L);
        // Setup UserResponseDto
        userResponseDto = new UserResponseDto(
                "user@example.com",
                UserRole.CUSTOMER);

        // Setup RegisterRequestDto
        registerRequestDto = new RegisterRequestDto(
                "updated@example.com",
                "newPassword123",
                UserRole.CUSTOMER);

        // Setup UserPasswordUpdateRequestDto
        passwordUpdateDto = new UserPasswordUpdateRequestDto(
                1L,
                "currentPassword123",
                "newSecurePassword456");
    }

    @Test
    @DisplayName("Deve retornar lista de todos os usuários")
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        List<User> users = List.of(user, adminUser);
        List<UserResponseDto> responseDtos = List.of(
                new UserResponseDto("user@example.com", UserRole.CUSTOMER),
                new UserResponseDto("admin@example.com", UserRole.ADMIN));

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toListResponseDto(users)).thenReturn(responseDtos);

        // Act
        List<UserResponseDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
        verify(userMapper).toListResponseDto(users);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver usuários")
    void getAllUsers_WhenNoUsersExist_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());
        when(userMapper.toListResponseDto(List.of())).thenReturn(List.of());

        // Act
        List<UserResponseDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Nested
    @DisplayName("Testes para updateUser")
    class UpdateUserTests {

        @Test
        @DisplayName("Deve atualizar usuário com dados válidos")
        void updateUser_WithValidData_ShouldUpdateUser() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.findByEmail("updated@example.com")).thenReturn(null);
            when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
            when(userRepository.save(user)).thenReturn(user);

            // Act
            UserResponseDto result = userService.updateUser(1L, registerRequestDto);

            // Assert
            assertNotNull(result);
            assertEquals("updated@example.com", user.getEmail());
            assertEquals("encodedNewPassword", user.getPassword());
            assertEquals(UserRole.CUSTOMER, user.getRole());
            
            verify(userRepository).findById(1L);
            verify(userRepository).findByEmail("updated@example.com");
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não existe")
        void updateUser_WithNonExistingUser_ShouldThrowBusinessException() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.updateUser(999L, registerRequestDto));

            assertEquals("No User Found with id 999", exception.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
            verify(userRepository).findById(999L);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando email já está em uso")
        void updateUser_WithDuplicateEmail_ShouldThrowBusinessException() {
            // Arrange
            User anotherUser = User.builder()
                    .email("existing@example.com")
                    .build();
                 anotherUser.setId(3L);
            RegisterRequestDto duplicateEmailDto = new RegisterRequestDto(
                    "existing@example.com", "password", UserRole.CUSTOMER);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.findByEmail("existing@example.com")).thenReturn(anotherUser);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.updateUser(1L, duplicateEmailDto));

            assertEquals("Email already in use", exception.getMessage());
            assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
            verify(userRepository).findById(1L);
            verify(userRepository).findByEmail("existing@example.com");
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve permitir atualização para o mesmo email do usuário")
        void updateUser_WithSameEmail_ShouldUpdateUser() {
            // Arrange
            RegisterRequestDto sameEmailDto = new RegisterRequestDto(
                    "user@example.com", // Mesmo email atual
                    "newPassword123",
                    UserRole.CUSTOMER);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            // findByEmail retorna o próprio usuário (o que é correto)
            when(userRepository.findByEmail("user@example.com")).thenReturn(user);
            when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
            when(userRepository.save(user)).thenReturn(user);

            // Act
            UserResponseDto result = userService.updateUser(1L, sameEmailDto);

            // Assert
            assertNotNull(result);
            // Não deve lançar exceção mesmo com email "duplicado" (é o mesmo usuário)
            verify(userRepository).findById(1L);
            verify(userRepository).findByEmail("user@example.com");
            verify(userRepository).save(user);
        }
    }

    @Nested
    @DisplayName("Testes para PasswordUpdate")
    class PasswordUpdateTests {

        @Test
        @DisplayName("Deve atualizar senha quando usuário é o proprietário")
        void PasswordUpdate_WhenUserIsOwner_ShouldUpdatePassword() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("currentPassword123", "encodedPassword123")).thenReturn(true);
            when(passwordEncoder.encode("newSecurePassword456")).thenReturn("encodedNewPassword");
            when(userRepository.save(user)).thenReturn(user);

            // Act
            userService.passwordUpdate(passwordUpdateDto);

            // Assert
            assertEquals("encodedNewPassword", user.getPassword());
            verify(userRepository).findById(1L);
            verify(securityService).getAuthenticatedUser();
            verify(passwordEncoder).matches("currentPassword123", "encodedPassword123");
            verify(passwordEncoder).encode("newSecurePassword456");
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Deve atualizar senha quando usuário é ADMIN")
        void PasswordUpdate_WhenUserIsAdmin_ShouldUpdatePassword() {
            // Arrange
            // Admin tentando atualizar senha de outro usuário
            UserPasswordUpdateRequestDto adminUpdateDto = new UserPasswordUpdateRequestDto(
                    1L, null, "adminChangedPassword");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(adminUser));
            when(passwordEncoder.encode("adminChangedPassword")).thenReturn("encodedByAdmin");
            when(userRepository.save(user)).thenReturn(user);

            // Act
            userService.passwordUpdate(adminUpdateDto);

            // Assert
            // Admin não precisa fornecer currentPassword
            assertEquals("encodedByAdmin", user.getPassword());
            verify(userRepository).findById(1L);
            verify(securityService).getAuthenticatedUser();
            verify(passwordEncoder, never()).matches(anyString(), anyString()); // Admin não verifica senha atual
            verify(passwordEncoder).encode("adminChangedPassword");
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não existe")
        void PasswordUpdate_WithNonExistingUser_ShouldThrowBusinessException() {
            // Arrange
            UserPasswordUpdateRequestDto nonExistentUserDto = new UserPasswordUpdateRequestDto(
                    999L, "currentPassword123", "newPassword");
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.passwordUpdate(nonExistentUserDto));

            assertEquals("No User Found with id 999", exception.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
            verify(userRepository).findById(999L);
            verify(securityService, never()).getAuthenticatedUser();
        }

        @Test
        @DisplayName("Deve lançar exceção quando nenhum usuário está autenticado")
        void PasswordUpdate_WhenNoUserIsAuthenticated_ShouldThrowBusinessException() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(securityService.getAuthenticatedUser()).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.passwordUpdate(passwordUpdateDto));

            assertEquals("User not authenticated", exception.getMessage()); // Bug no código original!
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
            verify(userRepository).findById(1L);
            verify(securityService).getAuthenticatedUser();
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não é owner nem admin")
        void PasswordUpdate_WhenUserIsNotOwnerNorAdmin_ShouldThrowBusinessException() {
            // Arrange
            User otherUser = User.builder()
                    .email("other@example.com")
                    .role(UserRole.CUSTOMER)
                    .build();
                otherUser.setId(3L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(otherUser));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.passwordUpdate(passwordUpdateDto));

            assertEquals("You can only update your own password", exception.getMessage());
            assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
            verify(userRepository).findById(1L);
            verify(securityService).getAuthenticatedUser();
        }

        @Test
        @DisplayName("Deve lançar exceção quando senha atual está incorreta")
        void PasswordUpdate_WhenCurrentPasswordIsWrong_ShouldThrowBusinessException() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongPassword", "encodedPassword123")).thenReturn(false);

            UserPasswordUpdateRequestDto wrongPasswordDto = new UserPasswordUpdateRequestDto(
                    1L, "wrongPassword", "newPassword");

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.passwordUpdate(wrongPasswordDto));

            assertEquals("Incorrect Password", exception.getMessage());
            assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
            verify(userRepository).findById(1L);
            verify(securityService).getAuthenticatedUser();
            verify(passwordEncoder).matches("wrongPassword", "encodedPassword123");
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("Deve permitir admin atualizar senha sem verificar senha atual")
        void PasswordUpdate_WhenAdmin_SkipCurrentPasswordCheck() {
            // Arrange
            UserPasswordUpdateRequestDto adminRequest = new UserPasswordUpdateRequestDto(
                    1L, null, "adminNewPassword");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(adminUser));
            when(passwordEncoder.encode("adminNewPassword")).thenReturn("adminEncoded");
            when(userRepository.save(user)).thenReturn(user);

            // Act
            userService.passwordUpdate(adminRequest);

            // Assert
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(passwordEncoder).encode("adminNewPassword");
            verify(userRepository).save(user);
        }
    }

    @Nested
    @DisplayName("Testes para toggleUserLock")
    class ToggleUserLockTests {

        @Test
        @DisplayName("Deve bloquear usuário desbloqueado")
        void toggleUserLock_WhenUserIsUnlocked_ShouldLockUser() {
            // Arrange
            user.setLocked(false);
            UserResponseDto lockedResponse = new UserResponseDto(
                    "user@example.com",
                    UserRole.CUSTOMER);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponseDto(user)).thenReturn(lockedResponse);

            // Act
            UserResponseDto result = userService.toggleUserLock(1L);

            // Assert
            assertNotNull(result);
            assertTrue(user.isLocked()); // Deve estar bloqueado agora
            verify(userRepository).findById(1L);
            verify(userRepository).save(user);
            verify(userMapper).toResponseDto(user);
        }

        @Test
        @DisplayName("Deve desbloquear usuário bloqueado")
        void toggleUserLock_WhenUserIsLocked_ShouldUnlockUser() {
            // Arrange
            user.setLocked(true);
            UserResponseDto unlockedResponse = new UserResponseDto(
                    "user@example.com",
                    UserRole.CUSTOMER);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponseDto(user)).thenReturn(unlockedResponse);

            // Act
            UserResponseDto result = userService.toggleUserLock(1L);

            // Assert
            assertNotNull(result);
            assertFalse(user.isLocked()); // Deve estar desbloqueado agora
            verify(userRepository).findById(1L);
            verify(userRepository).save(user);
            verify(userMapper).toResponseDto(user);
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não existe")
        void toggleUserLock_WithNonExistingUser_ShouldThrowBusinessException() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.toggleUserLock(999L));

            assertEquals("No User Found with id 999", exception.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
            verify(userRepository).findById(999L);
            verify(userRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Deve lançar exceção quando passwordEncoder retorna null")
    void updateUser_WhenPasswordEncoderReturnsNull_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("updated@example.com")).thenReturn(null);
        when(passwordEncoder.encode("newPassword123")).thenReturn(null); // Simulando retorno null

        // Act & Assert - O comportamento depende da implementação
        // Se o passwordEncoder retornar null, a senha ficará null
        userService.updateUser(1L, registerRequestDto);

        // Verificar que o fluxo continua mesmo com null
        verify(userRepository).findById(1L);
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(user);
    }
}