package consultancy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import consultancy.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepository  extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByChatId(String chatId);
}
