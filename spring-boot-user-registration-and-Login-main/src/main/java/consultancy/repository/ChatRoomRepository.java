package consultancy.repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import consultancy.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);

	List<ChatRoom> findByRecipientId(String recipientid);

	ChatRoom findByTitle(String title);

	List<ChatRoom> findBySenderId(String senderId);
}
