package org.example.mycrud.service;

import org.example.mycrud.entity.PasswordResetToken;


public interface PasswordResetTokenService {

    PasswordResetToken getByToken(String token);

    PasswordResetToken save(PasswordResetToken token);

    void deleteTokenById(Integer tokenId);
}
