package consultancy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import consultancy.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}