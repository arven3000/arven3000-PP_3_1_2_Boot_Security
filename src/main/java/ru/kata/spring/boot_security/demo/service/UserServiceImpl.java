package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleRepository;
import ru.kata.spring.boot_security.demo.dao.UserRepository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void saveUser(User user) {
        Optional<User> userFromDB = userRepository.findByEmail(user.getUsername());
        long id = user.getRoles().stream().findFirst().get().getId();
        Role role = roleRepository.findById(id).get();
        if (userFromDB.isEmpty()) {
            user.setRoles(Collections.singleton(role));
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else if (!userFromDB.get().getPassword().equals(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> listUsers() {
        return userRepository.getAll();
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(long id) {
        return userRepository.getUserById(id);
    }

    @Transactional
    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
