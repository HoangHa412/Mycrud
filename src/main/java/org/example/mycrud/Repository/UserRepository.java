package org.example.mycrud.Repository;

import jakarta.transaction.Transactional;
import org.example.mycrud.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select u from User u where u.username like %?1%")
    List<User> search(String keyword);

    Optional<User> findUsersByUsername(String name);

    Boolean existsUserByUsername(String username);

    User findUsersByEmail(String email);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?2 where u.email = ?1")
    void updatePassword(String email, String password);

}
