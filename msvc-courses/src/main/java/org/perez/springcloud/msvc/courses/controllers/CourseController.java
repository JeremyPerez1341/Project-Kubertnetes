package org.perez.springcloud.msvc.courses.controllers;

import org.perez.springcloud.msvc.courses.entity.Course;
import org.perez.springcloud.msvc.courses.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
        Optional<Course> courseOptional = courseService.getCourseById(id);
        if(courseOptional.isPresent()){
            return ResponseEntity.ok(courseOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        Course courseDB = courseService.saveCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@RequestBody Course course, @PathVariable Long id) {
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

}
