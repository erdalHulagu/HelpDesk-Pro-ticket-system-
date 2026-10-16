
package com.erdal.helpdeskpro.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mindrot.jbcrypt.BCrypt;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
import com.erdal.helpdeskpro.repository.UserRepository;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository);
    }

    // 1. Kullanıcı kaydedilirken şifre hash'lenmeli ve rol EMPLOYEE olmalı.
    @Test
    void registerUser_shouldHashPasswordAndSetDefaultValues() {

        User inputUser = new User();
        inputUser.setUsername("testuser");
        inputUser.setEmail("test@example.com");
        inputUser.setPassword("PlainPassword123");

        userService.registerUser(inputUser);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertTrue(BCrypt.checkpw(
            "PlainPassword123",
            savedUser.getPassword()
        ));
        assertEquals(Role.EMPLOYEE, savedUser.getRole());
        assertTrue(savedUser.isActive());

        // Orijinal kullanıcının şifresi değiştirilmemeli.
        assertEquals("PlainPassword123", inputUser.getPassword());
    }

    // 2. Doğru e-posta ve şifreyle giriş başarılı olmalı.
    @Test
    void login_shouldReturnUserWhenCredentialsAreCorrect() {

        String email = "test@example.com";
        String password = "CorrectPassword123";

        User user = new User();
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setActive(true);

        when(userRepository.findByEmail(email)).thenReturn(user);

        User result = userService.login(email, password);

        assertSame(user, result);
        verify(userRepository).findByEmail(email);
    }

    // 3. Kullanıcı bulunamazsa ResourceNotFoundExeption fırlatılmalı.
    @Test
    void login_shouldThrowExceptionWhenUserNotFound() {

        String email = "missing@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThrows(
            ResourceNotFoundExeption.class,
            () -> userService.login(email, "AnyPassword123")
        );

        verify(userRepository).findByEmail(email);
    }

    // 4. Şifre yanlışsa BadRequestExeption fırlatılmalı.
    @Test
    void login_shouldThrowExceptionWhenPasswordIsWrong() {

        String email = "test@example.com";

        User user = new User();
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(
            "CorrectPassword123",
            BCrypt.gensalt()
        ));
        user.setActive(true);

        when(userRepository.findByEmail(email)).thenReturn(user);

        assertThrows(
            BadRequestExeption.class,
            () -> userService.login(email, "WrongPassword123")
        );
    }

    // 5. Kullanıcı pasifse doğru şifreyle bile giriş yapamamalı.
    @Test
    void login_shouldThrowExceptionWhenUserIsInactive() {

        String email = "inactive@example.com";
        String password = "CorrectPassword123";

        User user = new User();
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setActive(false);

        when(userRepository.findByEmail(email)).thenReturn(user);

        assertThrows(
            BadRequestExeption.class,
            () -> userService.login(email, password)
        );
    }

    // 6. ID ile kullanıcı bulunduğunda aynı kullanıcı dönmeli.
    @Test
    void getUser_shouldReturnUserWhenFound() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        when(userRepository.findById(userId)).thenReturn(user);

        User result = userService.getUser(userId);

        assertSame(user, result);
        assertEquals("testuser", result.getUsername());
    }

    // 7. ID ile kullanıcı bulunamazsa exception fırlatılmalı.
    @Test
    void getUser_shouldThrowExceptionWhenUserNotFound() {

        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(null);

        assertThrows(
            ResourceNotFoundExeption.class,
            () -> userService.getUser(userId)
        );
    }

    // 8. ADMIN tüm kullanıcıları listeleyebilmeli.
    @Test
    void getAllUsers_shouldReturnUsersForAdmin() {

        User admin = new User();
        admin.setRole(Role.ADMIN);

        List<User> expectedUsers = List.of(
            new User(),
            new User()
        );

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> result = userService.getAllUsers(admin);

        assertEquals(2, result.size());
        assertEquals(expectedUsers, result);

        verify(userRepository).findAll();
    }

    // 9. ADMIN olmayan kullanıcı tüm kullanıcıları listeleyememeli.
    @Test
    void getAllUsers_shouldThrowExceptionForEmployee() {

        User employee = new User();
        employee.setRole(Role.EMPLOYEE);

        assertThrows(
            BadRequestExeption.class,
            () -> userService.getAllUsers(employee)
        );

        verifyNoInteractions(userRepository);
    }

    // 10. Kullanıcı güncellenirken username ve password değiştirilip kaydedilmeli.
    @Test
    void updateUser_shouldUpdateUsernameAndPassword() {

        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldname");
        existingUser.setPassword("oldpassword");

        User updatedData = new User();
        updatedData.setId(userId);
        updatedData.setUsername("newname");
        updatedData.setPassword("newpassword");

        when(userRepository.findById(userId)).thenReturn(existingUser);

        User result = userService.updateUser(updatedData);

        assertSame(existingUser, result);
        assertEquals("newname", result.getUsername());
        assertEquals("newpassword", result.getPassword());

        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
    }

    // 11. ADMIN kullanıcıyı devre dışı bırakma işlemini başlatabilmeli.
    // Mevcut servis implementasyonu burada deleteById çağırıyor.
    @Test
    void deactivateUser_shouldDeleteUserByIdForAdmin() {

        Long userId = 2L;

        User admin = new User();
        admin.setRole(Role.ADMIN);

        userService.deactivateUser(admin, userId);

        verify(userRepository).deleteById(userId);
    }

    // 12. ADMIN olmayan kullanıcı bu işlemi yapamamalı.
    @Test
    void deactivateUser_shouldThrowExceptionForEmployee() {

        Long userId = 2L;

        User employee = new User();
        employee.setRole(Role.EMPLOYEE);

        assertThrows(
            BadRequestExeption.class,
            () -> userService.deactivateUser(employee, userId)
        );

        verify(userRepository, never()).deleteById(userId);
    }
}
