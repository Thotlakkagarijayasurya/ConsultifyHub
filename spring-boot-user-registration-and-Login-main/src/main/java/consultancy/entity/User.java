package consultancy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import consultancy.entity.Role;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    private static final long serialVersionUID = 1L;
    @Id
    private String email;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "EMAIL")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "NAME")})
    private List<Role> roles = new ArrayList<>();
    private String domain ;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	} 

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public boolean hasRole(String Rolename) {
		Iterator<Role> itr= this.roles.iterator();
		while(itr.hasNext()) {
			Role role=itr.next();
			if(role.getName().equals(Rolename)) {
				return true;
			}
		}
		return false;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	
}