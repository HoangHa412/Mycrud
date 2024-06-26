package org.example.mycrud.Repository;

import org.example.mycrud.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.username like %?1%")
    List<User> search(String keyword);

    Optional<User> findUsersByUsername(String name);

    Boolean existsUserByUsername(String username);

}
