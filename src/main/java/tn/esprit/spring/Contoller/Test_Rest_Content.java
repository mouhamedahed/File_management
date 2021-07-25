package tn.esprit.spring.Contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import tn.esprit.spring.Entity.RequestApiForm.MessageResponse;
import tn.esprit.spring.Exception.API_Request_Exception_BAD_REQUEST_STATUS_400;
import tn.esprit.spring.Exception.API_Request_Exception_INTERNAL_SERVER_ERROR_STATUS_500;
import tn.esprit.spring.Exception.API_Request_Exception_UNAUTHORIZED_STATUS_403;

 
  

@RestController
public class Test_Rest_Content {
 

    

    @GetMapping("/all")
    public String allAccess() {

        return "Public Content.";
    }

    @GetMapping("/testException/{STATUS_CODE}")
    public void TestExceptionHandler(@PathVariable("id_Exception") Long STATUS_CODE) {
        if (STATUS_CODE == 403) {
            throw new API_Request_Exception_UNAUTHORIZED_STATUS_403("OK EXCEPTION 500 HAS BEEN INJECTED");

        }
        if (STATUS_CODE == 500) {
            throw new API_Request_Exception_INTERNAL_SERVER_ERROR_STATUS_500("OK EXCEPTION 403 HAS BEEN INJECTED");

        }
        if (STATUS_CODE == 400) {
            throw new API_Request_Exception_BAD_REQUEST_STATUS_400("OK EXCEPTION 403 HAS BEEN INJECTED");
        }

    }


    @GetMapping("/user")
    @PostAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_DIRECTEUR') or hasRole('ROLE_SOUSDIRECTEUR')")
    public ResponseEntity<?> userAccess() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            throw new API_Request_Exception_UNAUTHORIZED_STATUS_403(auth.getName().toUpperCase() + " IS UNAUTHORIZED CONTENT WITH THIS AUTHOROTIES !!! ");
        }
        return ResponseEntity.ok(new MessageResponse("Content allowed to USER , Directeur and sousDirecteur !"));

    }

    @GetMapping("/dir")
    @PreAuthorize("hasRole('ROLE_DIRECTEUR')")
    public String moderatorAccess() {
        return "Moderator Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

}
