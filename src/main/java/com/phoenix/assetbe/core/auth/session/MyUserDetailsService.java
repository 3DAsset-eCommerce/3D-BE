package com.phoenix.assetbe.core.auth.session;

import com.phoenix.assetbe.model.user.Status;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userPS = userRepository.findByUserWithEmailAndStatus(email, Status.ACTIVE).orElseThrow(
                () -> new InternalAuthenticationServiceException("인증 실패"));
        return new MyUserDetails(userPS);
    }
}
