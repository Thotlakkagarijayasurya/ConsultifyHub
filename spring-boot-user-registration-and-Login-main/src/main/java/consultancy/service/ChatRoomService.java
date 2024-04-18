package consultancy.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import consultancy.entity.ChatRoom;
import consultancy.entity.Role;
import consultancy.entity.User;
import consultancy.repository.ChatRoomRepository;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
	@Autowired
    private ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatRoomId(
            String senderId,
            String recipientId,
            boolean createNewRoomIfNotExists
    ) {
        return chatRoomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    /*if(createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }*/

                    return  Optional.empty();
                });
    }

   /* private String createChatId(String senderId, String recipientId) {
        var chatId = String.format("%s_%s", senderId, recipientId);

        ChatRoom senderRecipient = new ChatRoom(chatId,senderId,recipientId);

        ChatRoom recipientSender = new ChatRoom(chatId,recipientId ,senderId);
        
        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);
        System.out.println("chatroom created");
        return chatId;
    }*/
    
    public List<ChatRoom> getbyRecipientid(String recipientid) {
    	return chatRoomRepository.findByRecipientId(recipientid);
    }

	public ChatRoom findTicketByName(String title) {
		return chatRoomRepository.findByTitle(title);
	}

	public void saveTicket( ChatRoom ticket) {
		var chatId = String.format("%s_%s", ticket.getSenderId(), ticket.getRecipientId());
		ticket.setChatId(chatId);
        ChatRoom reverseChatroom=new ChatRoom();
        reverseChatroom.setDescription(ticket.getDescription());
        reverseChatroom.setTitle(ticket.getTitle());
        reverseChatroom.setStatus(ticket.getStatus());
        reverseChatroom.setSenderId(ticket.getRecipientId());
        reverseChatroom.setRecipientId(ticket.getSenderId());
		reverseChatroom.setChatId(chatId);
		reverseChatroom.setDomain(ticket.getDomain());
		chatRoomRepository.save(ticket);
        chatRoomRepository.save(reverseChatroom);
		
	}

	public List<ChatRoom> findTicketsBySenderId(String name) {
		List<ChatRoom> tickets = chatRoomRepository.findBySenderId(name);
		return tickets;
	}

	public ChatRoom findById(Long ticketid) {
		return chatRoomRepository.findById(ticketid).get();
	}

	public List<ChatRoom> findAllTickets() {
		List<ChatRoom> alltickets=chatRoomRepository.findAll();
		List<ChatRoom> uniquetickets=new ArrayList<>();
		for(int ind=0;ind<alltickets.size();ind+=2) {
			uniquetickets.add(alltickets.get(ind));
		}
		return uniquetickets;
	}

	public void updateTicket(ChatRoom ticket) {
		chatRoomRepository.save(ticket);
		
	}

	public void removeTicket(ChatRoom ticket) {
		chatRoomRepository.delete(ticket);
		
	}
}
