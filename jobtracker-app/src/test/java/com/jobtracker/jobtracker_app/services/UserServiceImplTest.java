package com.jobtracker.jobtracker_app.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.requests.UserCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.UserUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.UserResponse;
import com.jobtracker.jobtracker_app.entities.Role;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.UserMapper;
import com.jobtracker.jobtracker_app.repositories.RoleRepository;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.services.cache.PermissionCacheService;
import com.jobtracker.jobtracker_app.services.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionCacheService permissionCacheService;

    @InjectMocks
    private UserServiceImpl userService;

    private UserCreationRequest creationRequest;
    private UserUpdateRequest updateRequest;
    private User user;
    private Role role;
    private UserResponse userResponse;

    @BeforeEach
    void setup() {
        role = Role.builder().id("role1").name("admin").build();
        user = User.builder()
                .id("user1")
                .email("user1@gmail.com")
                .phone("0988777666")
                .firstName("Nam")
                .lastName("Phan")
                .role(role)
                .build();

        creationRequest = UserCreationRequest.builder()
                .email("user1@gmail.com")
                .password("123456")
                .firstName("Nam")
                .lastName("Phan")
                .phone("0988777666")
                .roleId("role1")
                .build();

        updateRequest = UserUpdateRequest.builder()
                .firstName("Nam1")
                .lastName("Phan")
                .phone("0988777666")
                .roleId("role1")
                .build();

        userResponse = UserResponse.builder()
                .id("user1")
                .roleName(role.getName())
                .email("user1@gmail.com")
                .phone("0988777666")
                .firstName("Nam")
                .lastName("Phan")
                .build();
    }

//    @Test
//    void create_shouldThrowException_whenEmailExists() {
////        when(userRepository.existsByEmail(anyString())).thenReturn(true);
//
//        AppException appException = assertThrows(AppException.class, () -> userService.create(creationRequest));
//
//        assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT);
////        verify(userRepository).existsByEmail("user1@gmail.com");
//        verifyNoMoreInteractions(userRepository, roleRepository, userMapper);
//    }

    @Test
    void create_shouldThrowException_whenRoleNotExist() {
//        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findById(anyString())).thenReturn(Optional.empty());

        AppException appException = assertThrows(AppException.class, () -> userService.create(creationRequest));

        assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.ROLE_NOT_EXISTED);
        verify(roleRepository).findById("role1");
        verifyNoMoreInteractions(userRepository, roleRepository);
    }

    @Test
    void create_shouldReturnUserResponse_whenValidRequest() {
//        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findById("role1")).thenReturn(Optional.of(role));
        when(userMapper.toUser(any())).thenReturn(user);
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);
        when(userRepository.save(any())).thenReturn(user);

        UserResponse response = userService.create(creationRequest);

        assertNotNull(response);
        assertThat(response.getEmail()).isEqualTo("user1@gmail.com");
        verify(userMapper).toUser(creationRequest);
        verify(userMapper).toUserResponse(user);
//        verify(userRepository).existsByEmail("user1@gmail.com");
        verify(roleRepository).findById("role1");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getById_shouldThrowException_whenUserNotExist() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        AppException appException = assertThrows(AppException.class, () -> userService.getById("not-exist"));

        assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_EXISTED);
        verify(userRepository).findById("not-exist");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getById_shouldReturnUserResponse_whenValidRequest() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        UserResponse response = userService.getById("user1");

        assertNotNull(response);
        verify(userRepository).findById("user1");
    }

    @Test
    void getAll_shouldReturnPageUserResponse_whenValidRequest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> users = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(users);
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        Page<UserResponse> responses = userService.getAll(pageable);

        assertEquals(1, responses.getTotalElements());
        assertEquals(userResponse, responses.getContent().get(0));
        verify(userRepository).findAll(pageable);
    }

    @Test
    void update_shouldThrowException_whenUserNotExist() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        AppException appException =
                assertThrows(AppException.class, () -> userService.update("not-exist", updateRequest));

        assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_EXISTED);
        verify(userRepository).findById("not-exist");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void update_shouldThrowException_whenRoleNotExist() {
        updateRequest.setRoleId("not-exist");
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findById(anyString())).thenReturn(Optional.empty());

        AppException appException = assertThrows(AppException.class, () -> userService.update("user1", updateRequest));

        assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.ROLE_NOT_EXISTED);
        verify(roleRepository).findById("not-exist");
        verifyNoMoreInteractions(userRepository, roleRepository);
    }

    @Test
    void update_shouldEvictPermissionCache_whenRoleChanged() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findById(anyString())).thenReturn(Optional.of(role));
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);
        when(userRepository.save(any())).thenReturn(user);

        userService.update("user1", updateRequest);

        verify(roleRepository).findById("role1");
        verify(permissionCacheService).evict("user1");
    }

    @Test
    void update_shouldReturnUpdateNotChangeRole_whenRoleIsNull() {
        updateRequest.setRoleId(null);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);
        when(userRepository.save(any())).thenReturn(user);

        UserResponse response = userService.update("user1", updateRequest);

        assertNotNull(response);
        assertThat(response.getRoleName()).isEqualTo("admin");
        verify(userMapper).toUserResponse(user);
        verify(userMapper).updateUser(user, updateRequest);
        verify(userRepository).save(user);
    }

    @Test
    void update_shouldReturnUpdateUser_whenValidRequest() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findById(anyString())).thenReturn(Optional.of(role));
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);
        when(userRepository.save(any())).thenReturn(user);

        UserResponse response = userService.update("user1", updateRequest);

        assertNotNull(response);
        verify(userRepository).save(user);
        verify(userMapper).updateUser(user, updateRequest);
    }

    @Test
    void delete_shouldThrowException_whenUserNotExist() {
        when(userRepository.findById("not-exist")).thenReturn(Optional.empty());

        AppException appException = assertThrows(AppException.class, () -> userService.delete("not-exist"));

        assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_EXISTED);
        verify(userRepository).findById("not-exist");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void delete_shouldSoftDelete_whenValidRequest() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        userService.delete("user1");

        assertTrue(user.isDeleted());
        assertNotNull(user.getDeletedAt());
        verify(userRepository).save(user);
    }
}
