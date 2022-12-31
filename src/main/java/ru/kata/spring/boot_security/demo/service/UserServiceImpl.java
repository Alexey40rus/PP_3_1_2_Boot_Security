package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepositories;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService  {

    private final UserRepositories userRepositories;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepositories userRepositories,
                           @Lazy BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepositories = userRepositories;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    public User findByUserName(String username) {
        return userRepositories.findByUsername(username);
    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), user.getAuthorities());
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(user.getRoles());
        userRepositories.save(user);
    }

    @Override
    @Transactional
    public void updateUser(User updateUser) {
        updateUser.setPassword(bCryptPasswordEncoder.encode(updateUser.getPassword()));
        userRepositories.save(updateUser);
    }

    @Override
    @Transactional
    public void deleteUser(int id) {
        userRepositories.deleteById(id);
    }

    @Override
    public User getUserById(int id) {
        return userRepositories.getById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepositories.findAll();
    }
}
