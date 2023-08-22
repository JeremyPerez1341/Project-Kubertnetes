package org.perez.springcloud.msvc.courses.services;

import org.perez.springcloud.msvc.courses.models.User;
import org.perez.springcloud.msvc.courses.models.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    List<Course> listAllCourses();
    Optional<Course> getCourseById(Long id);
    Course saveCourse(Course course);
    void deleteCourseById(Long id);
    Optional<Course> getCourseByIdWithUsers(long id);
    void deleteCourseUserById(Long id);

    Optional<User> assignUser(User user, Long courseId);
    Optional<User> createUser(User user, Long courseId);
    Optional<User> deallocateUser(User user, Long courseId);
}
