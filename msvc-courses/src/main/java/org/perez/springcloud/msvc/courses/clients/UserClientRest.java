package org.perez.springcloud.msvc.courses.clients;

import org.perez.springcloud.msvc.courses.models.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "msvc-users", url = "localhost:8001")
public interface UserClientRest {

    @GetMapping("/{id}")
    User findById(@PathVariable Long id);

    @PostMapping
    User save(@RequestBody User user);

    @GetMapping("/users-by-course")
    List<User> getUsersByCourse(@RequestParam Iterable<Long> ids);
}
