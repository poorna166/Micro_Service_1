package com.techcommerce.user_service.repository.dao;

import com.techcommerce.user_service.dto.UserDto;
import com.techcommerce.user_service.model.User;
import com.techcommerce.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserDaoTest {

    @Test
    void create_hashesPasswordAndSaves() {
        UserRepository repo = mock(UserRepository.class);
        UserDao dao = new UserDao(repo);

        UserDto dto = UserDto.builder()
                .fullName("Alice")
                .email("a@a.com")
                .password("mypwd")
                .build();

        when(repo.save(any())).thenAnswer(inv -> {
            User u = (User) inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        User saved = dao.create(dto);

        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(repo).save(cap.capture());
        User arg = cap.getValue();
        assertThat(arg.getPassword()).isNotEqualTo("mypwd");
        assertThat(arg.getId()).isEqualTo(1L);
        assertThat(saved.getEmail()).isEqualTo("a@a.com");
    }

    @Test
    void update_existing_user_appliesPatch() {
        UserRepository repo = mock(UserRepository.class);
        UserDao dao = new UserDao(repo);

        User existing = new User();
        existing.setId(5L);
        existing.setFullName("Old");

        when(repo.findById(5L)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserDto patch = UserDto.builder().fullName("New").build();
        User updated = dao.update(5L, patch);

        assertThat(updated.getFullName()).isEqualTo("New");
    }
}
