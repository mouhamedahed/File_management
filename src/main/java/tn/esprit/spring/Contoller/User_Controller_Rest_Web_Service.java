package tn.esprit.spring.Contoller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tn.esprit.spring.Entity.Confirmation_Token_User;
import tn.esprit.spring.Entity.Dbo_Role;
import tn.esprit.spring.Entity.Dbo_User;
import tn.esprit.spring.Entity.Dbo_User_Provider;
import tn.esprit.spring.Entity.ERole;
import tn.esprit.spring.Entity.RequestApiForm.JwtResponse;
import tn.esprit.spring.Entity.RequestApiForm.MessageResponse;
import tn.esprit.spring.Entity.RequestApiForm.RequestLogin;
import tn.esprit.spring.Entity.RequestApiForm.SignupRequest;
import tn.esprit.spring.Exception.API_Request_Exception_NotFound;
import tn.esprit.spring.ExterneService.Custom_Oauth2_User;
import tn.esprit.spring.Repository.ConfirmationTokenRepository;
import tn.esprit.spring.Repository.Role_Repository;
import tn.esprit.spring.Repository.User_Repository;
import tn.esprit.spring.Security.JwtUtils;
import tn.esprit.spring.Service.Email_Sender_Service;
import tn.esprit.spring.Service.Session_UserDetails;
import tn.esprit.spring.Service.User_Service;

@SuppressWarnings("ALL")
@CrossOrigin("*")
@RestController

public class User_Controller_Rest_Web_Service {
    @Autowired
    private User_Service I_User_Service;

    @Autowired
    User_Repository Jpa_User_Repository;
    @Autowired
    Role_Repository Jpa_Role_Repository;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Email_Sender_Service emailSenderService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;


    private final org.jboss.logging.Logger logger = LoggerFactory.logger(User_Controller_Rest_Web_Service.class);

    @RequestMapping("/LoggingTest")
    public String index() {
        logger.trace("A TRACE Message");
        logger.debug("A DEBUG Message");
        logger.info("An INFO Message");
        logger.warn("A WARN Message");
        logger.error("An ERROR Message");
        logger.fatal("A FATAL Message ");

        return "hi ! Check out the Logs to see the output...";
    }


    /* Authentication API */
    @RequestMapping("/Sign-In")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody RequestLogin loginRequest, HttpServletRequest request,
                                              Authentication authentication, BindingResult bindingResult) throws UnsupportedEncodingException {
        Map<String, Object> Errors = new HashMap<>();
        /* Check if input is null */
        if (loginRequest.getEmail().toString().equalsIgnoreCase("")) {
            Errors.put("Errors : the Email is Required ,'" + bindingResult.getFieldError() + "' can not be null  !",
                    true);

            return ResponseEntity.badRequest().body(new MessageResponse("" + Errors));
        }
        /* Check if email is valid and exists */
        Optional<Dbo_User> userAuth = Jpa_User_Repository.findByEmailIgnoreCase(loginRequest.getEmail());
        if (userAuth.isPresent() == false) {
            return ResponseEntity.badRequest().body(new MessageResponse(
                    "Error: Couldn’t find account with this email  during Sign-In OR this mail may be NOT VALID!"));
        }
        //Check if account is locked
        if (Jpa_User_Repository.getaccountNonLocked(loginRequest.getEmail()) == false) {
            return ResponseEntity.badRequest().body(new MessageResponse("Your account is locked right now"));
        }
        /* Check if account is activated */
        Dbo_User user = Jpa_User_Repository.findByEmail(loginRequest.getEmail());
        if (Jpa_User_Repository.isActif(loginRequest.getEmail()) == false) {
            return ResponseEntity.badRequest().body(new MessageResponse(
                    "Error: Sorry Mr'" + user.getFullName() + " your account is not already activated  !"));
        }

        /*
         * check the attempts authentication then locked his account if attempts
         * =3
         */
        /*
         * when attempts <3 and it was a success login all attempts reset
         * to 0
         */
        /* and Check if account is activated */
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            if (user.getFailedAttempt() > 0) {
                I_User_Service.resetFailedAttempts(user.getEmail()); // when
                // User
                // logged
                // In
                // reset
                // all
                // failed
                // attempts
                // to
                // null
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            logger.info("token is :" + jwt);
            Session_UserDetails userDetails = (Session_UserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            try {
                Jpa_User_Repository.updateUserTimeLoggedIn(userDetails.getId(), new Date());

                Jpa_User_Repository.updateUserSessionId(request.getSession().getId(), userDetails.getId());
                logger.info(
                        "Session reference is : " + request.getSession().getId() + " Id User : " + userDetails.getId());
                logger.info("Time_Logged_In has been updated for user with First name : " + userDetails.getUsername());
            } catch (Exception e) {
                logger.error("Something is wrong time logged in not updated " + e.getMessage());
            }

            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
                    userDetails.getEmail(), roles));
        } catch (BadCredentialsException e) { // BAD CREDENTIALS
            if (Jpa_User_Repository.getaccountNonLocked(loginRequest.getEmail()) == true // if
                    // account
                    // not
                    // locked
                    // and
                    // verified
                    && Jpa_User_Repository.isActif(loginRequest.getEmail()) == true) {
                if (Jpa_User_Repository.getfailedAttempt(loginRequest.getEmail()) < User_Service.MAX_FAILED_ATTEMPTS // if
                        // number
                        // of
                        // failed
                        // attempts
                        // less
                        // then
                        // 3
                        - 1) {
                    I_User_Service.increaseFailedAttempts(user); // if
                    // authentication
                    // failed
                    // ==>
                    // failed_attempts
                    // +=1
                } else {
                    I_User_Service.lock(user); // if number of failed attempts =
                    // 3 then lock the account
                    return ResponseEntity.badRequest()
                            .body(new MessageResponse("Your account has been locked due to 3 failed attempts."
                                    + " It will be unlocked after 24 hours."));
                }
            } else if (Jpa_User_Repository.getaccountNonLocked(loginRequest.getEmail()) == false
                    && Jpa_User_Repository.isActif(loginRequest.getEmail()) == true) { // if
                // account
                // is
                // locked
                // and
                // verified
                if (I_User_Service.unlockWhenTimeExpired(user)) { // if block
                    // duration
                    // is not
                    // less than
                    // 24 hours
                    // then
                    // unlock
                    // this
                    // account
                    return ResponseEntity.badRequest()
                            .body(new MessageResponse("Your account has been unlocked. Please try to login again."));
                }
            }
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Sorry Mr'" + user.getFullName()
                    + " Check your credentials please ' PASSWORD NOT MATCHED ' !"));
        }
    }

    /* Forgot password CallBack */
    // Receive the address and send an email
    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
    public ResponseEntity<?> forgotUserPassword(@RequestBody Dbo_User user) {
        Dbo_User userFogot = Jpa_User_Repository.findByEmail(user.getEmail());

        // Create token
        Confirmation_Token_User confirmationToken = new Confirmation_Token_User(userFogot);

        // Save it
        confirmationTokenRepository.save(confirmationToken);
        logger.info("token for reset is : " + confirmationToken.getConfirmationToken());

        // Create the email
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userFogot.getEmail());
        mailMessage.setSubject("Complete Password Reset!");
        mailMessage.setFrom("ayoub.benzahra@esprit.tn");
        mailMessage.setText("To complete the password reset process, please click here: "
                + "http://localhost:8900/confirm-reset?token=" + confirmationToken.getConfirmationToken());

        // Send the email
        emailSenderService.sendEmail(mailMessage);

        return ResponseEntity
                .ok(new MessageResponse("Request to reset password received. Check your inbox for the reset link."));

    }

    // Endpoint to confirm the token
    @RequestMapping(value = "/confirm-reset", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> validateResetToken(@RequestParam("token") String confirmationToken) {
        Confirmation_Token_User token = confirmationTokenRepository.findByconfirmationToken(confirmationToken);

        if (token != null) {
            Dbo_User userToConfirm = Jpa_User_Repository.findByEmailIgnoreCase(token.getUser().getEmail()).orElse(null);
            userToConfirm.setActif(true);
            Jpa_User_Repository.save(userToConfirm);

            return ResponseEntity.ok(new MessageResponse(
                    "link of token_Password_Reset has been verifed Now you can change your password"));
        } else {
            return ResponseEntity.badRequest().body("The link is invalid or broken!. ");
        }

    }

    // Endpoint to update a user's password
    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public ResponseEntity<?> resetUserPassword(@RequestBody Dbo_User user) {

        // Use email to find user
        Dbo_User tokenUser = Jpa_User_Repository.findByEmail(user.getEmail());
        tokenUser.setPassword(passwordEncoder.encode(user.getPassword()));
        Jpa_User_Repository.save(tokenUser);
        return ResponseEntity
                .ok(new MessageResponse("Password successfully reset. You can now log in with the new credentials."));

    }

    /* END Forgot password CallBack */
    /* REGISTRATION */
    @RequestMapping("/Sign-Up")
    public ResponseEntity<?> add_user(@Valid @RequestBody SignupRequest signUpRequest, BindingResult bindingResult) {

        if (Jpa_User_Repository.existsByFirstName(signUpRequest.getFirstName())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (Jpa_User_Repository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        Map<String, Object> Errors = new HashMap<>();
        if (bindingResult.hasErrors()) {
            Errors.put("Errors : Please check the properties type validation , or ,'" + bindingResult.getFieldError()
                    + "' can not be null  !", true);

            return ResponseEntity.badRequest().body(new MessageResponse("" + Errors));
        }

        // Create new user's account
        Dbo_User dbo_User = new Dbo_User(signUpRequest.getEmail(), signUpRequest.getFirstName(),
                signUpRequest.getLastName(), false, signUpRequest.getDate(),
                passwordEncoder.encode(signUpRequest.getPassword()));
        Set<String> strRoles = signUpRequest.getRole();
        Set<Dbo_Role> roles = new HashSet<>();

        if (strRoles == null) {
            Dbo_Role userRole = Jpa_Role_Repository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(MultiRole -> {
                switch (MultiRole) {
                    case "admin":
                        Dbo_Role ROLE_ADMIN = Jpa_Role_Repository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(ROLE_ADMIN);

                        break;
                    case "dir":
                        Dbo_Role ROLE_DIRECTEUR = Jpa_Role_Repository.findByName(ERole.ROLE_DIRECTEUR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(ROLE_DIRECTEUR);

                        break;
                    case "sousDir":
                        Dbo_Role ROLE_SOUDIRECTEUR = Jpa_Role_Repository.findByName(ERole.ROLE_SOUDIRECTEUR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(ROLE_SOUDIRECTEUR);

                        break;
                    case "parent":
                        Dbo_Role ROLE_PARENT = Jpa_Role_Repository.findByName(ERole.ROLE_PARENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(ROLE_PARENT);

                        break;
                    case "child":
                        Dbo_Role ROLE_ENFANT = Jpa_Role_Repository.findByName(ERole.ROLE_ENFANT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(ROLE_ENFANT);

                        break;
                    default:
                        Dbo_Role user = Jpa_Role_Repository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(user);
                }
            });
        }
        dbo_User.setActif(true);
        dbo_User.setRole(roles);
        dbo_User.setAccountNonLocked(true);
        dbo_User.setDate(signUpRequest.getDate());
        dbo_User.setDbo_User_Provider(Dbo_User_Provider.LOCAL);
        dbo_User.setCreatedTime(new Date());
        Jpa_User_Repository.save(dbo_User);

        /* Email Service and confirmation user account with token generated */
        Confirmation_Token_User confirmationToken = new Confirmation_Token_User(dbo_User);

        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(dbo_User.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("benzahra.ayoub15@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                + "http://localhost:8900/confirm-account?token=" + confirmationToken.getConfirmationToken());

        emailSenderService.sendEmail(mailMessage);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    /* confirm account after persisting user in the database */
    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public String confirmUserAccount(@RequestParam("token") String confirmationToken) {

        Confirmation_Token_User token = confirmationTokenRepository.findByconfirmationToken(confirmationToken);
        if (token != null) {
            Dbo_User user = Jpa_User_Repository.findByEmail(token.getUser().getEmail());
            user.setActif(true);
            Jpa_User_Repository.save(user);
        }

        return "Your account has been activated by this valid token : "+confirmationToken  ;
    }

    /* END REGISTRATION */
    /* Logged out API */
    @PostMapping("/log-out")
    public ResponseEntity<?> logout(HttpSession session, Authentication auth, HttpServletRequest request) {
        logger.info("Before logout");
        session.invalidate();
        SecurityContextHolder.getContext().setAuthentication(auth);
        Session_UserDetails userDetails = (Session_UserDetails) auth.getPrincipal();
        for (Cookie cookie : request.getCookies()) {
            cookie.setMaxAge(0);
        }
        Jpa_User_Repository.updateUserTimeLoggedOut(userDetails.getId(), new Date());
        logger.info("time logged out has been updated with user id : " + userDetails.getId());
        return ResponseEntity.ok(new MessageResponse("session logged out"));
    }

    /* Google sign-In */
    @RequestMapping("/oauth2/authorization/google")
    public Object SignInWithGoogle(BindingResult bindingResult, HttpServletRequest request,
                                   HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Custom_Oauth2_User OauthUser = (Custom_Oauth2_User) authentication.getPrincipal();
        return OauthUser;

    }

    /* Get all the users */
    @GetMapping("/retrieve-all-users")
    public List<Dbo_User> getUsers() {
        List<Dbo_User> list = I_User_Service.retrieveAllUsers();
        return list;
    }

    /* Get user by his identify */
    @GetMapping("/retrieve-user/{user-id}")
    public Optional<Dbo_User> retrieveUserById(@PathVariable("user-id") Long userId, HttpServletRequest request,
                                               HttpServletResponse response) throws UserPrincipalNotFoundException {
        if (!Jpa_User_Repository.existsById(userId)) {
            throw new API_Request_Exception_NotFound("User not found with this Identify : " + userId);
        }
        return I_User_Service.findById(userId);
    }

    /*
     * Delete user by identify Only user with role ADMIN is authorized to use
     * this API
     */
    @Secured(value = {"ROLE_ADMIN"})
    @DeleteMapping("/remove-user/{user-id}")
    public void removeUser(@PathVariable("user-id") Long userId) {

        if (!Jpa_User_Repository.existsById(userId)) {
            throw new API_Request_Exception_NotFound("User not found with this Identify : " + userId);
        }
        Jpa_User_Repository.deleteById(userId);
    }

    /* update account Only user with role ADMIN is authorized to use this API */
    @Secured(value = {"ROLE_ADMIN"})
    @PutMapping("/updateUser/{user-id}")
    public Object updateUser(@RequestBody Dbo_User dbo_User, @PathVariable("user-id") Long id,
                             BindingResult bindingResult) {
        Dbo_User userToUpdate = Jpa_User_Repository.findById(id).orElse(null);

        String lastName = dbo_User.getLastName();
        userToUpdate.setLastName(lastName);
        String firstname = dbo_User.getFirstName();
        userToUpdate.setFirstName(firstname);
        Date date = dbo_User.getDate();
        userToUpdate.setDate(date);
        boolean actif = dbo_User.isActif();
        userToUpdate.setActif(actif);
        if (dbo_User.getRole() == null) {
            userToUpdate.setRole(userToUpdate.getRole());
        }
        Map<String, Object> Errors = new HashMap<>();

        // Optional<Dbo_User> existingUserWithEmail
        // =Jpa_User_Repository.findByEmailIgnoreCase(dbo_User.getEmail());
        // Restricted without Confirmation

        Optional<Dbo_User> existingUserWithFirstName = Jpa_User_Repository
                .findByFirstNameIgnoreCase(dbo_User.getFirstName());
        Optional<Dbo_User> existingUserWithLastName = Jpa_User_Repository
                .findByLastNameIgnoreCase(dbo_User.getLastName());

        if (existingUserWithFirstName.isPresent()) {

            Errors.put("Errors : this First Name is already exist please choose another one !", true);
            for (FieldError fe : bindingResult.getFieldErrors()) {
                Errors.put(fe.getField(), fe.getDefaultMessage());
            }
            return Errors;
        } else if (existingUserWithLastName.isPresent()) {

            Errors.put("Errors : this Last Name is already exist please choose another one !", true);
            for (FieldError fe : bindingResult.getFieldErrors()) {
                Errors.put(fe.getField(), fe.getDefaultMessage());
            }
            return Errors;
        }

        return Jpa_User_Repository.save(userToUpdate);

    }

    /* END updating account all roles are authorized to use this API */
    @Secured(value = {"ROLE_ADMIN", "ROLE_DIRECTEUR", "ROLE_SOUSDIRECTEUR", "ROLE_USER"})
    @RequestMapping(value = "/getLogedUser")
    public Map<String, Object> getLogedUser(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        SecurityContext context = (SecurityContext) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
        String username = context.getAuthentication().getName();
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority ga : context.getAuthentication().getAuthorities()) {
            roles.add(ga.getAuthority());
        }
        Map<String, Object> params = new HashMap<>();
        params.put("Email", username);
        params.put("role of " + username, roles);
        return params;
    }


}
