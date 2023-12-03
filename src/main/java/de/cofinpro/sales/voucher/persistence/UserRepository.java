package de.cofinpro.sales.voucher.persistence;

import de.cofinpro.sales.voucher.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findUsersByNameIgnoreCase(String name);

    List<User> findAll();
}
