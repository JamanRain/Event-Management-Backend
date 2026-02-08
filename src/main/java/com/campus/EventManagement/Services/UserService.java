package com.campus.EventManagement.Services;
import com.campus.EventManagement.Entities.Role;
import com.campus.EventManagement.Entities.User;
import com.campus.EventManagement.Exceptions.BadRequestException;
import com.campus.EventManagement.Exceptions.ResourceNotFoundException;
import com.campus.EventManagement.Exceptions.UnauthorizedException;
import com.campus.EventManagement.Repositories.UserRepository;
import com.campus.EventManagement.Security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static com.campus.EventManagement.Security.SecurityUtil.getCurrentRole;

@Service
public class UserService {

    private static final Logger logger =
            Logger.getLogger(UserService.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @Transactional
    public Optional<User> createUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warning("User already exists with email: " + user.getEmail());
            throw new BadRequestException("User already exists with this email. Try another one");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
       try
       {
           emailService.sendSimpleMail(
                   user.getEmail(),
                   "Welcome to Event Management System 🎉",
                   "Hi " + user.getName() + ",\n\nYour account has been created successfully.\n\nHappy exploring events!\n\n— EMS Team"
           );
       }
       catch (Exception e)
       {
           throw new BadRequestException("Failed to create User");
       }


        return Optional.of(userRepository.save(user));
    }

    @Transactional
    public Optional<User> updateUser(Long userId, User updatedUser) {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        if (!userId.equals(currentUserId)) {
            logger.warning("Unauthorized update attempt");
            throw new UnauthorizedException("You can only update your account");
        }

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));


        if (updatedUser.getName() != null)
            existingUser.setName(updatedUser.getName());

        if (updatedUser.getEmail() != null)
            existingUser.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null
                && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(
                    passwordEncoder.encode(updatedUser.getPassword()));
        }

        return Optional.of(userRepository.save(existingUser));
    }

    public Optional<Boolean> deleteUser(Long userId) {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        if (!userId.equals(currentUserId)) {
            logger.warning("Unauthorized delete attempt");
            throw new UnauthorizedException("You can only delete your own account");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
        return Optional.of(true);
    }
    public Map<String, Page<String>> getUserNamesByRole(Pageable pageable) {

        if (!getCurrentRole().equals("ROLE_ADMIN")) {
            throw new UnauthorizedException("Only admin can access this");
        }

        Page<String> clubs = userRepository.findNamesByRole(Role.CLUB, pageable);
        Page<String> students = userRepository.findNamesByRole(Role.STUDENT, pageable);

        return Map.of(
                "clubs", clubs,
                "students", students
        );
    }
    public Map<String, Page<User>> getUsersByRole(Pageable pageable) {

        if (!getCurrentRole().equals("ROLE_ADMIN")) {
            throw new UnauthorizedException("Only admin can access this");
        }

        Page<User> clubs = userRepository.findByRole(Role.CLUB, pageable);
        Page<User> students = userRepository.findByRole(Role.STUDENT, pageable);

        return Map.of(
                "clubs", clubs,
                "students", students
        );
    }


}

