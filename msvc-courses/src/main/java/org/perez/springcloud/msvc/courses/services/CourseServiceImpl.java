package org.perez.springcloud.msvc.courses.services;

import org.perez.springcloud.msvc.courses.clients.UserClientRest;
import org.perez.springcloud.msvc.courses.models.User;
import org.perez.springcloud.msvc.courses.models.entity.Course;
import org.perez.springcloud.msvc.courses.models.entity.CourseUser;
import org.perez.springcloud.msvc.courses.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private CourseRepository courseRepository;
    private UserClientRest clientRest;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, UserClientRest clientRest) {
        this.clientRest = clientRest;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> listAllCourses() {
        return (List<Course>) courseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    @Transactional
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    @Transactional
    public void deleteCourseById(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Course> getCourseByIdWithUsers(long id) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        if(courseOptional.isPresent()) {
            Course course = courseOptional.get();
            if(!course.getCourseUsers().isEmpty()) {
                List<Long> ids =course.getCourseUsers().stream()
                        .map(CourseUser::getUserId)
                        .toList();
                List<User> users = clientRest.getUsersByCourse(ids);
                course.setUsers(users);
            }
            return Optional.of(course);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void deleteCourseUserById(Long id) {
        courseRepository.deleteCourseUserById(id);
    }

    @Override
    @Transactional
    public Optional<User> assignUser(User user, Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()){
            User userMsvc = clientRest.findById(user.getId());

            Course course = courseOptional.get();
            CourseUser courseUser = new CourseUser();
            courseUser.setUserId(userMsvc.getId());

            course.addCourseUser(courseUser);
            courseRepository.save(course);
            return Optional.of(userMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<User> createUser(User user, Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()){
            User userNewMsvc = clientRest.save(user);

            Course course = courseOptional.get();
            CourseUser courseUser = new CourseUser();
            courseUser.setUserId(userNewMsvc.getId());

            course.addCourseUser(courseUser);
            courseRepository.save(course);
            return Optional.of(userNewMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<User> deallocateUser(User user, Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()){
            User userMsvc = clientRest.findById(user.getId());

            Course course = courseOptional.get();
            CourseUser courseUser = new CourseUser();
            courseUser.setUserId(userMsvc.getId());

            course.removeCourseUser(courseUser);
            courseRepository.save(course);
            return Optional.of(userMsvc);
        }
        return Optional.empty();
    }
}
