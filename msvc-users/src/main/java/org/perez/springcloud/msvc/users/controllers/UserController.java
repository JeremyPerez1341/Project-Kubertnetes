package org.perez.springcloud.msvc.users.controllers;

import jakarta.validation.Valid;
import org.perez.springcloud.msvc.users.models.entity.User;
import org.perez.springcloud.msvc.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
        Optional<User> userOptional = userService.findById(id);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(userOptional.get());
        } else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody User user, BindingResult result) {

        if(!user.getEmail().isEmpty() && userService.findByEmail(user.getEmail()).isPresent()){
            return ResponseEntity.badRequest()
                    .body(Collections
                            .singletonMap("message", "Email already exists"));
        }

        // error handling
        if(result.hasErrors()){
            return validate(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody User user, BindingResult result, @PathVariable Long id) {

        // error handling
        if(result.hasErrors()){
            return validate(result);
        }

        Optional<User> userOptional = userService.findById(id);
        if(userOptional.isPresent()){
            User userDb = userOptional.get();

            if(!user.getEmail().isEmpty() &&
                    !user.getEmail().equalsIgnoreCase(userDb.getEmail()) &&
                    userService.findByEmail(user.getEmail()).isPresent()){
                return ResponseEntity.badRequest()
                        .body(Collections
                                .singletonMap("message", "Email already exists"));
            }

            userDb.setName(user.getName());
            userDb.setEmail(user.getEmail());
            userDb.setPassword(user.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userDb));
        } else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        Optional<User> userOptional = userService.findById(id);
        if(userOptional.isPresent()){
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users-by-course")
    public ResponseEntity<?> getUsersByCourse(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(userService.listByIds(ids));
    }

    private static ResponseEntity<Map<String, Object>> validate(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "The field " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
