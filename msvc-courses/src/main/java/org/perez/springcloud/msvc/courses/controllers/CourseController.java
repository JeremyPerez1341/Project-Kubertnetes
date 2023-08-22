package org.perez.springcloud.msvc.courses.controllers;

import feign.FeignException;
import jakarta.validation.Valid;
import org.perez.springcloud.msvc.courses.models.User;
import org.perez.springcloud.msvc.courses.models.entity.Course;
import org.perez.springcloud.msvc.courses.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public ResponseEntity<List<Course>> findAll() {
        return ResponseEntity.ok(courseService.listAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> findById(@PathVariable Long id) {
        Optional<Course> courseOptional = courseService.getCourseByIdWithUsers(id); //courseService.getCourseById(id);
        if(courseOptional.isPresent()){
            return ResponseEntity.ok(courseOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createCourse(@Valid @RequestBody Course course, BindingResult result) {
        // error handling
        if(result.hasErrors()){
            return validate(result);
        }

        Course courseDB = courseService.saveCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@Valid @RequestBody Course course, BindingResult result, @PathVariable Long id) {
        // error handling
        if(result.hasErrors()){
            return validate(result);
        }

        Optional<Course> courseOptional = courseService.getCourseById(id);
        if(!courseOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }
        courseOptional.get().setName(course.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.saveCourse(courseOptional.get()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourseById(id);
        if(!courseService.getCourseById(id).isPresent()){
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("assign-user/{courseId}")
    public ResponseEntity<?> assignUser(@RequestBody User user, @PathVariable Long courseId) {
        Optional<User> o;
        try {
            o = courseService.assignUser(user, courseId);
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found" + e.getMessage()));
        }

        if(o.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("create-user/{courseId}")
    public ResponseEntity<?> createUser(@RequestBody User user, @PathVariable Long courseId) {
        Optional<User> o;
        try {
            o = courseService.createUser(user, courseId);
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not created" + e.getMessage()));
        }

        if(o.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("delete-user/{courseId}")
    public ResponseEntity<?> deleteUser(@RequestBody User user, @PathVariable Long courseId) {
        Optional<User> o;
        try {
            o = courseService.deallocateUser(user, courseId);
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found" + e.getMessage()));
        }

        if(o.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(o.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete-course-user/{id}")
    public ResponseEntity<?> deleteCourseUserById(@PathVariable Long id) {
        courseService.deleteCourseUserById(id);
        return ResponseEntity.noContent().build();
    }

    private static ResponseEntity<Map<String, Object>> validate(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "The field " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
