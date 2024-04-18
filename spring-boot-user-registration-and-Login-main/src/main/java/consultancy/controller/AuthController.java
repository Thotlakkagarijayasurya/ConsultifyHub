package consultancy.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import consultancy.dto.UserDto;
import consultancy.entity.ChatRoom;
import consultancy.entity.User;
import consultancy.service.ChatRoomService;
import consultancy.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {
	@Autowired
    private UserService userService;
    // handler method to handle home page request
    @Autowired
    private ChatRoomService chatroomService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping("/")
    public String home() {
        return "index";
    }
	// handler method to handle user registration form request
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // create model object to store form data
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle user registration form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
            model.addAttribute("user", userDto);
            return "/register";
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/register";
        }

        userService.saveUser(userDto);
        if(userDto.getDomain()!=null)return "redirect:/admin_page?success";
        return "redirect:/register?success";
    }
    
    @GetMapping("/newticket")
    public String showTicketForm(Model model) {
        // create model object to store form data
        ChatRoom ticket = new ChatRoom();
        model.addAttribute("ticket", ticket);
        return "RaiseTicket";
    }

    // handler method to handle user registration form submit request
    @PostMapping("/newticket/save")
    public String createTicket(@Valid @ModelAttribute("ticket") ChatRoom ticket,
                               BindingResult result,
                               Model model,
                               Principal principal) {
        Optional<String> existingTicket = chatroomService.getChatRoomId(ticket.getSenderId(),ticket.getRecipientId(),false);
        if (!existingTicket.isEmpty() ) {
            model.addAttribute("ticket", ticket);
            return "redirect:/newticket?failure";
        }

        if (result.hasErrors()) {
            model.addAttribute("ticket", ticket);
            return "/newticket";
        }
        ticket.setSenderId(principal.getName());
        ticket.setRecipientId(userService.findUserByDomain(ticket.getDomain()).getEmail());
        chatroomService.saveTicket(ticket);
        return "redirect:/newticket?success";
    }

    // handler method to handle list of users
    @GetMapping("/users")
    public String users_page() {
        return "users";
    }
    @GetMapping("/admin_page")
    public String getadminpage(Model model,Principal principal) {
    	UserDto newConsultant = new UserDto();
        model.addAttribute("user", newConsultant);
    	List<UserDto> users=userService.findAllUsers();
    	List<ChatRoom> tickets=chatroomService.findAllTickets();
    	List<UserDto> clients=new ArrayList<>();
    	List<UserDto> consultants=new ArrayList<>();
    	for(UserDto user:users) {
    		if(user.getEmail().equals(principal.getName()))continue;
    		if(user.getDomain()==null ) {
    			clients.add(user);
    		}
    		else {
    			consultants.add(user);
    		}
    	}
    	model.addAttribute("consultants", consultants);
    	model.addAttribute("users", clients);
    	model.addAttribute("alltickets", tickets);
        return "adminpage";
    }
    @GetMapping("/mytickets")
    public String gettickets(Model model, Principal principal) {
        List<ChatRoom> tickets = chatroomService.findTicketsBySenderId(principal.getName());
        model.addAttribute("tickets", tickets);
        return "tickets";
    }
    // handler method to handle login request
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/update/{ticketid}")
    public String updateticket(@PathVariable Long ticketid,Principal principal, Model model) {
    	ChatRoom ticket=chatroomService.findById(ticketid);
    	model.addAttribute("ticket", ticket);
    	return "updateticket";
    }
    
    @GetMapping("/update/save")
    public String saveupdatedticket(@Valid @ModelAttribute("ticket") ChatRoom ticket, Principal principal) {
    	ChatRoom updateticket=chatroomService.findById(ticket.getId());
    	updateticket.setStatus(ticket.getStatus());
    	
    	chatroomService.updateTicket(updateticket);
    	Long id=ticket.getId()-1;
    	if(principal.getName().equals("admin@123")) {
    		id=ticket.getId()+1;
    	}
    	ChatRoom updateticket2=chatroomService.findById(id);
    	updateticket2.setStatus(ticket.getStatus());
    	chatroomService.updateTicket(updateticket2);
    	if(principal.getName().equals("admin@123")) return "redirect:/admin_page";
    	return "redirect:/mytickets";
    }
    @PostMapping("/addfeedback")
    public String submitFeedback( @RequestParam("ticketId") Long ticketId, @RequestParam("feedback") String feedback, @RequestParam("rating") int rating) {
        
        // Here you can process the rating and feedback text as per your requirement
        // For example, you can save them to a database or perform any other operation
        ChatRoom ticket=chatroomService.findById(ticketId);
        ticket.setRating(rating);
        ticket.setFeedback(feedback);
        chatroomService.updateTicket(ticket);
        ChatRoom ticket2=chatroomService.findById(ticketId+1);
        ticket2.setRating(rating);
        ticket2.setFeedback(feedback);
        chatroomService.updateTicket(ticket2);
        return "redirect:/mytickets#";
    }
    @GetMapping("/delete/{userid}")
    public String deleteuser(@PathVariable String userid) {
    	User user=userService.findUserByEmail(userid);
    	userService.removeUser(user);
        return "redirect:/admin_page";
    }
    @GetMapping("/deleteticket/{ticketid}")
    public String deleteticket(@PathVariable Long ticketid,Principal principal) {
    	ChatRoom ticket=chatroomService.findById(ticketid);
    	chatroomService.removeTicket(ticket);
    	Long id=ticketid+1;
    	ChatRoom ticket2=chatroomService.findById(id);
    	chatroomService.removeTicket(ticket2);
        return "redirect:/admin_page";
    }
    @GetMapping("/profile")
    public String showUpdateProfileForm(Model model,Principal principal) {
    	String email=principal.getName();
        User user = userService.findUserByEmail(email);
        model.addAttribute("user", user);
        return "profile"; 
    }
    @PostMapping("/profile/update")
	public String UpdateProfile(@Valid @ModelAttribute("user") User user, Principal principal) {
		User existinguser = userService.findUserByEmail(principal.getName());
		existinguser.setEmail(user.getEmail());
		existinguser.setName(user.getName());
		existinguser.setPassword(existinguser.getPassword());
		if(user.getPassword()!="") {
		existinguser.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		
		userService.UpdateUser(existinguser);
		return "redirect:/users";
	}
    @GetMapping("/chat/{ticketid}")
    public String chatpage(@PathVariable Long ticketid,Model model) {
    	ChatRoom chatroom=chatroomService.findById(ticketid);
    	model.addAttribute("ticket", chatroom);
        return "chat";
    }
    @GetMapping("/chat")
    public String consultantchatpage() {
        return "chat";
    }
    @GetMapping("/getuserrole")
    public ResponseEntity<String> getRole(Principal principal){
    	return ResponseEntity.ok(userService.findUserByEmail(principal.getName()).getRoles().get(0).getName());
    }
    @GetMapping("/getconsultant")
    public ResponseEntity<List<UserDto>> getConsultantbyDomain() {
        return ResponseEntity.ok(userService.findAllUsers());
    }
    
    @GetMapping("/getuser")
    public ResponseEntity<List<UserDto>> getConnectedUser(Principal principal) {
    	String email=principal.getName();
    	List<UserDto> users = new ArrayList<UserDto>();
    	List<ChatRoom> chatrooms = chatroomService.getbyRecipientid(email);
    	for(ChatRoom chatroom : chatrooms) {
    		users.add(userService.convertEntityToDto(userService.findUserByEmail(chatroom.getSenderId())));
    	}
        return ResponseEntity.ok(users);
    }
    
    /*@MessageMapping("/user.addUser")
    @SendTo("/user/public")
    public Ticket addTicket(
            @Payload Ticket ticket
    ) {
        Service.saveUser(ticket);
        return ticket;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/public")
    public Ticket disconnectUser(
            @Payload Ticket user
    ) {
        Service.disconnect(user);
        return user;
    }*/

}