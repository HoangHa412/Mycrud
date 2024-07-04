package org.example.mycrud.Service;

import lombok.AllArgsConstructor;
import org.example.mycrud.Entity.PasswordResetToken;
import org.example.mycrud.Entity.User;
import org.example.mycrud.Repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;


    @Override
    public PasswordResetToken getByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        return passwordResetTokenRepository.save(token);
    }

    @Override
    public void deleteTokenById(Integer tokenId) {
        passwordResetTokenRepository.deleteById(tokenId);
    }


}
