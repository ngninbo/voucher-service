package de.cofinpro.sales.voucher.persistence;

import de.cofinpro.sales.voucher.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    List<User> findAll();

    @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.email = ?2")
    @Modifying
    void updateFailedAttempts(int failAttempts, String email);
}
