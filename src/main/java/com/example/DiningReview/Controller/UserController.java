package com.example.DiningReview.Controller;
import com.example.DiningReview.Model.User;
import com.example.DiningReview.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequestMapping("/user")
@RestController
public class UserController {
    public final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PutMapping("/{displayName}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@PathVariable String displayName ,@RequestBody User updatedUser) {
        validateUser(updatedUser);
        Optional<User> userToUpdateOptional = userRepository.findUserByDisplayName(displayName);
        if(!userToUpdateOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        User userToUpdate = userToUpdateOptional.get();

        if(updatedUser.getDisplayName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if(!updatedUser.getCity().isEmpty()) {
            userToUpdate.setCity(updatedUser.getCity());
        }
        if(!updatedUser.getState().isEmpty()) {
            userToUpdate.setState(updatedUser.getState());
        }
        if(!updatedUser.getZipCode().isEmpty()) {
            userToUpdate.setZipCode(updatedUser.getZipCode());
        }
        if(!ObjectUtils.isEmpty(updatedUser.getPeanut())) {
            userToUpdate.setPeanut(updatedUser.getPeanut());
        }
        if(!ObjectUtils.isEmpty(updatedUser.getEgg())) {
            userToUpdate.setEgg(updatedUser.getEgg());
        }
        if(!ObjectUtils.isEmpty(updatedUser.getDairy())) {
            userToUpdate.setDairy(updatedUser.getDairy());
        }

        userRepository.save(userToUpdate);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addUser(@RequestBody User user) {
        validateUser(user);
        userRepository.save(user);
    }

    @GetMapping
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{displayName}")
    public User getUser(@PathVariable String displayName) {
        validateDisplayName(displayName);

        Optional<User> optionalUser= userRepository.findUserByDisplayName(displayName);
        if(!optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        User user = optionalUser.get();
        user.setId(null);
        return user;
    }

    private void validateUser(User user) {
        validateDisplayName(user.getDisplayName());

        Optional<User> existingUser= userRepository.findUserByDisplayName(user.getDisplayName());
        if(existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    private void validateDisplayName(String displayName) {
        if(ObjectUtils.isEmpty(displayName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
