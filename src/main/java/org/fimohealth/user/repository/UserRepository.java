package org.fimohealth.user.repository;

import org.fimohealth.user.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByEmail(String email);

    Optional<Users> findByEmailAndPassword(String email, String password);

    @Query("SELECT u FROM Users u WHERE u.age >= ?1 AND u.age < ?2")
    Collection<Users> findAllUsersInGivenAgeGroup(int minAge, int maxAge);
}
