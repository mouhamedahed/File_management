package tn.esprit.spring.Contoller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/test_session")
public class Test_Session_Servlet_Rest  extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Test_Session_Servlet_Rest() {
		super();
		// TODO Auto-generated constructor stub
	}

	 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        HttpSession session = request.getSession();
        
        PrintWriter writer = response.getWriter();
        writer.println("Session ID: " + session.getId());
        writer.println("Creation Time: " + new Date(session.getCreationTime()));
        writer. println("Last Accessed Time: " + new Date(session.getLastAccessedTime()));
	}
}
