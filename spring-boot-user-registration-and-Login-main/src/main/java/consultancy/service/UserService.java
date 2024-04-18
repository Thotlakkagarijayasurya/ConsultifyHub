package consultancy.service;

import java.util.List;

import consultancy.dto.UserDto;
import consultancy.entity.User;
public interface UserService {
    void saveUser(UserDto userDto);
    void UpdateUser(User User);
    User findUserByEmail(String email);
    List<UserDto> findAllUsers();
	UserDto convertEntityToDto(User user);
	User findUserByDomain(String domain);
	void removeUser(User user);
}