package com.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

//para o spring gerenciar precisa de alguma notation, Component eh a mais generica
@Component
public class FilterTaskAuth extends OncePerRequestFilter {
    
    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var servletPath = request.getServletPath();

        System.out.println(servletPath);

        if(servletPath.startsWith("/tasks")){

            var authorization = request.getHeader("Authorization");

            authorization = authorization.substring("Basic".length()).trim();

            var authDecode = Base64.getDecoder().decode(authorization);

            var authString = new String(authDecode);

            String[] userCredentials = authString.split(":");
            String username = userCredentials[0];
            String password = userCredentials[1];

            var userExists = userRepository.findByUsername(username);

            if(userExists == null){
                response.sendError(401, "Usuário não encontrado");
            }
            else {
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), userExists.getPassword());

                if(passwordVerify.verified){
                    request.setAttribute("userId", userExists.getId());
                    filterChain.doFilter(request, response);
                }
                else{
                    response.sendError(401, "Usuario ou senha incorretos!");
                }
            }
        }
        else {
            filterChain.doFilter(request, response);
        }
    }
}
