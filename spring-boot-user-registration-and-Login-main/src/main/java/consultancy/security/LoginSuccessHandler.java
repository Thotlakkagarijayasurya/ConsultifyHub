package consultancy.security;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
	SimpleUrlAuthenticationSuccessHandler usersuccesshandler=
			new SimpleUrlAuthenticationSuccessHandler("/users");
	SimpleUrlAuthenticationSuccessHandler adminsuccesshandler=
			new SimpleUrlAuthenticationSuccessHandler("/admin_page");
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		Collection<? extends GrantedAuthority> authorities=
				authentication.getAuthorities();
		for(final GrantedAuthority authority:authorities) {
			String authorityName=authority.getAuthority();
			if(authorityName.equals("ROLE_ADMIN")) {
				this.adminsuccesshandler.onAuthenticationSuccess(request, response, authentication);
				return;
			}
			this.usersuccesshandler.onAuthenticationSuccess(request, response, authentication);
		}
	}

}
