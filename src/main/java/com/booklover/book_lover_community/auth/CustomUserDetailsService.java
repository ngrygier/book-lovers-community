package com.booklover.book_lover_community.auth;
import com.booklover.book_lover_community.user.User;
import com.booklover.book_lover_community.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // <- musisz zwracać zahashowane
                .disabled(!user.isEnabled())
                .accountLocked(user.isAccountNonLocked() == false)
                .authorities(user.getAuthorities())
                .build();
    }
}

