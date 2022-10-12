package org.fimohealth.user.controller;

import org.fimohealth.user.domain.Error;
import org.fimohealth.user.domain.LoginRequest;
import org.fimohealth.user.domain.Users;
import org.fimohealth.user.repository.UserRepository;
import org.fimohealth.user.validation.ValidateOnPatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/user", produces = "application/json")
public class UserController {

    private final UserRepository repository;
    private final Validator validator;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    /**
     * Controller method to create a new user
     */
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Users createUser(@RequestBody Users user) {
        validateRequest(user, false);
        user.setPassword(encodePassword(user.getPassword()));
        return repository.save(user);
    }

    /**
     * Patches a user with email and password. Throws exception if age is patched.
     * @param email
     * @param users
     * @return
     */
    @PatchMapping(value = "/{email}", consumes = "application/json")
    public Users updateUser(@PathVariable("email") String email, @RequestBody Users users) {
        validateRequest(users, true);
        return repository.findByEmail(email)
                .map(user -> {
                    if (users.getPassword() != null)
                        user.setPassword(encodePassword(users.getPassword()));
                    if (users.getEmail() != null)
                        user.setEmail(users.getEmail());
                    return repository.save(user);
                }).orElse(null);
    }

    /**
     * Controller method to fetch all users in the given age group
     */
    @GetMapping
    public Collection<Users> getUsersInAgeGroup(@RequestParam("minAge") int minAge, @RequestParam("maxAge") int maxAge) {
        return repository.findAllUsersInGivenAgeGroup(minAge, maxAge);
    }

    /**
     * Deletes a user by email, if it is present.
     * @param email
     */
    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("email") String email) {
        repository.findByEmail(email).ifPresent(user -> {
            repository.deleteById(user.getUuid());
        });
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest) {
        boolean successfulLogin = repository.findByEmailAndPassword(loginRequest.getEmail(),
                encodePassword(loginRequest.getPassword())).isPresent();
        LOGGER.info("Login Successful: " + successfulLogin);
        return successfulLogin ? ResponseEntity.status(HttpStatus.OK).body("Login Successful!")
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed!");
    }

    /**
     * Handler method to handle the validation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleConstraintViolations(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                .stream()
                .map(Error::build)
                .collect(Collectors.toList());
    }

    /**
     * If there are any validation errors, exception is thrown to be handled by the UserController#handleConstraintViolations
     * @param user
     * @param isPatch
     */
    private void validateRequest(Users user, boolean isPatch) {
        Set<ConstraintViolation<Users>> constraintViolations = isPatch
                ? validator.validate(user, ValidateOnPatch.class) : validator.validate(user);
        if (constraintViolations.size() > 0) {
            LOGGER.error("There are constraint violation errors");
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    private String encodePassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }
}
