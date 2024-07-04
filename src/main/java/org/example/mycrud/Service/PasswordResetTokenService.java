package org.example.mycrud.Service;

import org.example.mycrud.Entity.PasswordResetToken;


public interface PasswordResetTokenService {

    PasswordResetToken getByToken(String token);

    PasswordResetToken save(PasswordResetToken token);

    void deleteTokenById(Integer tokenId);
}
