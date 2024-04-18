package consultancy.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import consultancy.entity.Ticket;
import consultancy.repository.ConnectedUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectedUserService {
	@Autowired
    private  ConnectedUserRepository repository;

    public void saveUser(Ticket user) {
        user.setStatus("Assigned");
        repository.save(user);
    }

    public void disconnect(Ticket user) {
        var storedUser = repository.findById(user.getNickName()).orElse(null);
        if (storedUser != null) {
            storedUser.setStatus("Hold");
            repository.save(storedUser);
        }
    }

    public List<Ticket> findConnectedUsers() {
        return repository.findAllByStatus("Assigned");
    }
}