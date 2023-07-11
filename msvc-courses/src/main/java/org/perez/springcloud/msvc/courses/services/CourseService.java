package org.perez.springcloud.msvc.courses.services;

import org.perez.springcloud.msvc.courses.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    List<Course> listAllCourses();
    Optional<Course> getCourseById(Long id);
    Course saveCourse(Course course);
    void deleteCourseById(Long id);
}
