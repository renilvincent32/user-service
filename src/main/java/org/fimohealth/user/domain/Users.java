package org.fimohealth.user.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.fimohealth.user.validation.ReadOnly;
import org.fimohealth.user.validation.Unique;
import org.fimohealth.user.validation.ValidateOnPatch;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import java.util.UUID;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID uuid;

    @Email
    @Unique
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // to ensure that password is ignored while fetching the user data
    private String password;

    @ReadOnly(groups = ValidateOnPatch.class)
    private Integer age;

    public UUID getUuid() {
        return uuid;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Integer getAge() {
        return age;
    }

    public Users setEmail(String email) {
        this.email = email;
        return this;
    }

    public Users setPassword(String password) {
        this.password = password;
        return this;
    }

    public Users setAge(Integer age) {
        this.age = age;
        return this;
    }
}