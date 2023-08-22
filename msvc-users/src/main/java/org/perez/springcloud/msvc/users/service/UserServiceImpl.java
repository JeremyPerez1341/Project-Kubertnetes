package org.perez.springcloud.msvc.users.service;

import org.perez.springcloud.msvc.users.clients.CourseClientRest;
import org.perez.springcloud.msvc.users.models.entity.User;
import org.perez.springcloud.msvc.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private CourseClientRest clientRest;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CourseClientRest clientRest) {
        this.userRepository = userRepository;
        this.clientRest = clientRest;
    }

    @Override
    @Transactional(readOnly=true)
    public List<User> findAll() {
        return (List<User>)userRepository.findAll();
    }

    @Override
    @Transactional(readOnly=true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
        clientRest.deleteCourseUserById(id);
    }

    @Override
    @Transactional(readOnly=true)
    public List<User> listByIds(Iterable<Long> ids) {
        return (List<User>) userRepository.findAllById(ids);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
