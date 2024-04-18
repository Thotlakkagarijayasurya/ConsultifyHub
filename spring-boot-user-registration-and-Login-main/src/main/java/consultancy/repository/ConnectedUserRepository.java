package consultancy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import consultancy.entity.Ticket;

import java.util.List;

public interface ConnectedUserRepository  extends JpaRepository<Ticket, String> {
    List<Ticket> findAllByStatus(String status);
}
