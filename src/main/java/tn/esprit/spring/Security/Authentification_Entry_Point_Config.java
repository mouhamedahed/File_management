package tn.esprit.spring.Security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
@Component
public class Authentification_Entry_Point_Config extends BasicAuthenticationEntryPoint{
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
			response.addHeader("WWW-Authentificate", "Basic Test Authentification" + getRealmName());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			PrintWriter writer = response.getWriter();
			writer.println("HTTP STATUS 999 (Error Managed) " + authException.getMessage());
	}
	
	@Override
	public void afterPropertiesSet() {
		 setRealmName("Ayoub Ben zahra Project");
		 super.afterPropertiesSet();
	}
	
}
