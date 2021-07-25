package tn.esprit.spring.Aop;

import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Aspect_Config_Method_Before_Logged_Out  {

	Logger logAspect = Logger.getLogger(this.getClass().getName());
	
	@Pointcut( "execution(* Esprit.PiDev.RestController.*.logout(..))" )
	public void logout(){}
	
	@Before(value = "logout()")
	public void Before_Call_logout(JoinPoint thisJoinPoint ) {
		 
		logAspect.info("Before method '/logout' :" +thisJoinPoint.getSignature() );
		 
	}
}
