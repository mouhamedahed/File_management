package tn.esprit.spring.Aop;

import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Aspect_Config_Method_Execution_Duration {
	long First_time;
	long Second_time;
	Logger logAspect = Logger.getLogger(this.getClass().getName());

	@Pointcut("execution(* Esprit.PiDev.RestController.*.*(..))")
	public void this_Method() {
	}

	@Before(value = "this_Method()")
	public void Before_Call_Main(JoinPoint thisJoinPoint) {
		First_time = System.currentTimeMillis();
		logAspect.info("***********");
		logAspect.info("before : " + thisJoinPoint.getSignature());
		System.out.println("before : " + thisJoinPoint.getSignature().getName());
	}

	@After(value = "this_Method()")
	public void After_Call_Main(JoinPoint thisJoinPoint) {
		Second_time = System.currentTimeMillis();
		logAspect.info("***********");
		logAspect.info("after : " + thisJoinPoint.getSignature().getName());
		logAspect.info("execution time : " + (Second_time - First_time));
	}

	@Around("execution(* Esprit.PiDev.RestController.*.*(..))")
	public Object profile(ProceedingJoinPoint pjp) throws Throwable {
		long start = System.currentTimeMillis();
		Object obj = pjp.proceed();
		long elapsedTime = System.currentTimeMillis() - start;
		logAspect.info("Method execution time: " + elapsedTime + " milliseconds.");
		return obj;
	}

}
