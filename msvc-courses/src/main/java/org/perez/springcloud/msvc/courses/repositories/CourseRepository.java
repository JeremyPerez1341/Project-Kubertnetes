package org.perez.springcloud.msvc.courses.repositories;

import org.perez.springcloud.msvc.courses.entity.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Long> {
}
