package pl.patrykbober.bloomer.domain.user;

import org.springframework.stereotype.Service;
import pl.patrykbober.bloomer.domain.user.request.CreateUserRequest;
import pl.patrykbober.bloomer.domain.user.request.RegisterUserRequest;
import pl.patrykbober.bloomer.domain.user.request.SelfUpdateUserRequest;
import pl.patrykbober.bloomer.domain.user.request.UpdateUserRequest;

import java.util.List;

@Service
public class UserService {

    public Long create(CreateUserRequest request) {
        return null;
    }

    public List<UserDto> findAll() {
        return List.of();
    }

    public UserDto findById(Long id) {
        return null;
    }

    public UserDto findByEmail(String email) {
        return null;
    }

    public UserDto update(Long id, UpdateUserRequest request) {
        return null;
    }

    public UserDto update(String email, SelfUpdateUserRequest request) {
        return null;
    }

    public void deleteById(Long id) {

    }

    public void deleteByEmail(String email) {

    }

    public Long register(RegisterUserRequest request) {
        return null;
    }
}
