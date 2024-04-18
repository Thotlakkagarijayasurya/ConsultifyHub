package consultancy.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import consultancy.dto.UserDto;
import consultancy.entity.Role;
import consultancy.entity.User;
import consultancy.repository.RoleRepository;
import consultancy.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.*;
@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        String user_role="ROLE_USER";
        if(userDto.getDomain()!=null) {
        	user.setDomain(userDto.getDomain());
        	user_role="ROLE_CONSULTANT";
        }
        //encrypt the password using spring security
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Role role = roleRepository.findByName(user_role);
        if (role == null) {
            role = checkRoleExist(user_role);
        }
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    private Role checkRoleExist(String str) {
        Role role = new Role();
        role.setName(str);
        return roleRepository.save(role);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userlist = new ArrayList<UserDto>();
        for(User user:users) {
        	userlist.add(convertEntityToDto(user));
        }
        return userlist;
    }
    @Override
    public UserDto convertEntityToDto(User user) {
        UserDto userDto = new UserDto();
        String[] name = user.getName().split(" ");
        userDto.setFirstName(name[0]);
        userDto.setLastName(name[1]);
        userDto.setEmail(user.getEmail());
        userDto.setDomain(user.getDomain());
        return userDto;
    }

	@Override
	public void UpdateUser(User user) {
		 
	        userRepository.save(user);
	}

	@Override
	public User findUserByDomain(String domain) {
		return userRepository.findByDomain(domain);
	}

	@Override
	public void removeUser(User user) {
		for (Role role : user.getRoles()) {
            role.getUsers().remove(user);
        }

        // Save EntityB entities to reflect the removal of associations
        roleRepository.saveAll(user.getRoles());
		userRepository.delete(user);
		
	}

}
