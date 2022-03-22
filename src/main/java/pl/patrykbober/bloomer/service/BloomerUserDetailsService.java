package pl.patrykbober.bloomer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.patrykbober.bloomer.model.jpa.Role;
import pl.patrykbober.bloomer.repository.BloomerUserRepository;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BloomerUserDetailsService implements UserDetailsService {

    private final BloomerUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(user -> new User(
                        user.getEmail(),
                        user.getPassword(),
                        getAuthorities(user.getRoles())
                ))
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s has not been found", username)));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        var rolePrefix = "ROLE_";
        return roles.stream()
                .map(Role::getName)
                .map(roleName -> rolePrefix + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
