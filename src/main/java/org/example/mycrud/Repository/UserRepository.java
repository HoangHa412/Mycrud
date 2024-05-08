package org.example.mycrud.Repository;

import org.example.mycrud.Entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    @Query("select u from User u where u.name like %?1%")
    List<User> searhUser(String name);
}
