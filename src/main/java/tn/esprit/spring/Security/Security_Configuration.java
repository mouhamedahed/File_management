package tn.esprit.spring.Security;

import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import tn.esprit.spring.ExterneService.Custom_Oauth2_User_Service;
import tn.esprit.spring.ExterneService.OAuth2_Login_Success_Handler;
import tn.esprit.spring.Service.User_Details_Service_Impl;

@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@Configuration
@EnableWebSecurity
@EnableWebMvc
public class Security_Configuration extends WebSecurityConfigurerAdapter {
	/*
	  securedEnabled = true: activate the annotation @Secured
	  jsr250Enabled = true:   activate all annotations Java standardJSR250  @RolesAllowed
	  prePostEnabled: activate all annotations Spring@PreAuthorizeet@PostAuthorize. 
	 */
	@Autowired
	private Authentification_Entry_Point_Config entryPoint;

	@Autowired
	private User_Details_Service_Impl userDetailsService;
	@Autowired
	Custom_Oauth2_User_Service customService;
	@Autowired
	OAuth2_Login_Success_Handler oauth2LoginSuccessHandler;
	 
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());// Authentication
																									// Bean
																									// Configuration

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable() .authorizeRequests().antMatchers("/question/getQuestionbyid/{question_id},/Satisfaction/AffecterQuetions_Satisfaction_Satisfaction_User/{iduser}","/Satisfaction/StatistiqueAnswer_QuetionSatisfactionbUSer/{idusersat}/{namesat}","/Satisfaction/getAllQuestionsbySatisfaction/{idsat}","/Satisfaction/Affecter_Answer_Question_Satisfaction/{idsat}","/Satisfaction/modify-satisfaction/{iduser}","/Satisfaction/remove-satisfaction/{satisfaction-id}","/Satisfaction/add-satisfaction","/Satisfaction/findSatisfactionById/{satisfaction-id}","/Satisfaction/retrieve-all-satisfactions","/question/getAllAnswerByQuestion/{question_id}","/question/Updatequestion/{question_id}","/question/deleteQuestionById/{question_id}","/question/addQuestion","/question/getAllQuestion" ,"/v1/payments/payment/123/execute","/messages/findMessageById/{msg_id}","/messages/retrieve-all-Messages","/messages/remove-Message/{Message-id}","/messages/sendMessage/{idReciever}","/messages/retrieve-conversation/{reciever_id}","/payment/execute","/PAYPAL_SUCCESS_URL","/pay","/confirm-account","/generate","/Sign-In" , "/Sign-Up" ,  "/oauth2/**" , "/all" , "/forgot-password" , "/confirm-reset" , "/reset-password" , "/deleteCron", "/login").permitAll();

				 

				
				 /*
								 * The first instruction allows access to the
								 * roads
								 */

			http.authorizeRequests().anyRequest().authenticated().and().formLogin().loginPage("/login").permitAll()
				/*
				 * The second instruction asks all requests to be authenticated,
				 * and indicates the path to the authentication page (here /
				 * login) accessible by the first instruction)
				 */

				.and().httpBasic().authenticationEntryPoint(entryPoint)
				/*
				 * Once a user agent is authenticated using BASIC
				 * authentication, logout requires that the browser be closed or
				 * an unauthorized (401) header be sent. The simplest way of
				 * achieving the latter is to call the
				 * commence(HttpServletRequest, HttpServletResponse,
				 * AuthenticationException) method below. This will indicate to
				 * the browser its credentials are no longer authorized, causing
				 * it to prompt the user to login again.
				 */

				.and().oauth2Login().loginPage("/login").userInfoEndpoint().userService(customService)
				/*
				 * Authorize google authentication then obtaining the user
				 * attributes from the userInfo End point
				 */
				.and().successHandler(oauth2LoginSuccessHandler).and().logout().logoutSuccessUrl("/log-out").logoutSuccessUrl("/Sign-In")
				/* redirection to this path "/Sign-In" */
				.clearAuthentication(true).deleteCookies("dummyCookie").and().sessionManagement().maximumSessions(1);
		/*
		 * invalidate the session , destroy the cookies and generate just one
		 * session per user
		 */

		http.logout().deleteCookies("JSESSIONID").and().sessionManagement().maximumSessions(1);

		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		/*
		 * With addFilterBefore() method, it tells that jwtAuthenticationFilter
		 * will triggered before UsernamePasswordAuthenticationFilter.
		 */

	}

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	 

	@Bean
	public UserDetailsService userDetailsService() {
		return new User_Details_Service_Impl();
	}

	@Bean
	public AuthenticationManager customAuthenticationManager() throws Exception {
		return authenticationManager();
	}

	@Bean
	public LogoutSuccessHandler logoutSuccessHandler() {
		return new Custom_Logout_Success_Handler();
	}

	@Bean
	public ServletListenerRegistrationBean<HttpSessionListener> sessionListener() {
		return new ServletListenerRegistrationBean<HttpSessionListener>(new Session_Created());
	}

	@Bean
	public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
		return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
}
