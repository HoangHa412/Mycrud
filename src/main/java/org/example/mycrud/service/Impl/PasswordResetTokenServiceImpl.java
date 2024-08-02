package org.example.mycrud.service.Impl;

import org.example.mycrud.entity.PasswordResetToken;
import org.example.mycrud.repository.PasswordResetTokenRepository;
import org.example.mycrud.service.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
