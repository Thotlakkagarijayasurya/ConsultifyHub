package consultancy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import consultancy.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);

	User findByDomain(String domain);
}