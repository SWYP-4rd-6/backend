package com.swygbro.trip.backend.domain.user.application;

import com.swygbro.trip.backend.domain.user.domain.SignUpType;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.domain.user.domain.UserRepository;
import com.swygbro.trip.backend.domain.user.dto.CreateUserRequest;
import com.swygbro.trip.backend.domain.user.dto.UpdateUserRequest;
import com.swygbro.trip.backend.domain.user.dto.UserProfileDto;
import com.swygbro.trip.backend.domain.user.excepiton.PasswordNotMatchException;
import com.swygbro.trip.backend.domain.user.excepiton.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserValidationService userValidationService;

    @Transactional
    public Long createUser(CreateUserRequest dto) {
        userValidationService.checkUniqueUser(dto);

        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            throw new PasswordNotMatchException();
        }

        User createdUser = userRepository.save(
                new User(dto, SignUpType.Local, passwordEncoder.encode(dto.getPassword()))
        );

        return createdUser.getId();
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserProfileDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .name(user.getName())
                .profile(user.getProfile())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    @Transactional
    public void updateUser(Long id, UpdateUserRequest dto, MultipartFile imageFile) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (imageFile != null) {
            // TODO
            // 이미지 파일 업로드 처리
            // user.setProfileImageUrl("이미지 URL");
        }

        user.update(dto);
    }

}
