package com.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    //faz com que o String auto gerencie o ciclo de vida, instancia, new ... sem a anotation fica com oNull
    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody UserModel userModel) {
        var user = userRepository.findByUsername((userModel.getUsername()));

        if (user != null){
            System.out.println("Usuario ja existe");
        return ResponseEntity.status(400).body("Usuario ja cadastrado");
        }

        var passwordHashed = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(passwordHashed);

        user =  userRepository.save(userModel);

        return ResponseEntity.status(201).body(user);
    }

}
