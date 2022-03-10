package com.revature.quizzard.servlets;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.quizzard.dtos.requests.LoginRequest;
import com.revature.quizzard.dtos.responses.Principal;
import com.revature.quizzard.models.AppUser;
import com.revature.quizzard.services.TokenService;
import com.revature.quizzard.services.UserService;
import com.revature.quizzard.util.exceptions.AuthenticationException;
import com.revature.quizzard.util.exceptions.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AuthServlet extends HttpServlet {

    private final TokenService tokenService;
    private final UserService userService;
    private final ObjectMapper mapper;

    @Autowired
    public AuthServlet(TokenService tokenService, UserService userService, ObjectMapper mapper) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getServletContext().getInitParameter("programmaticParam"));
    }

    // Login endpoint
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter writer = resp.getWriter();

        try {

            LoginRequest loginRequest = mapper.readValue(req.getInputStream(), LoginRequest.class);
            Principal principal = new Principal(userService.login(loginRequest));
            String payload = mapper.writeValueAsString(principal);

            // Stateful session management
//            HttpSession httpSession = req.getSession();
//            httpSession.setAttribute("authUser", principal);

            String token = tokenService.generateToken(principal);
            resp.setHeader("Authorization", token);
            resp.setContentType("application/json");
            writer.write(payload);


        } catch (InvalidRequestException | DatabindException e) {
            resp.setStatus(400);
        } catch (AuthenticationException e) {
            resp.setStatus(401); // UNAUTHORIZED (no user found with provided credentials)
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
        }
    }

}
