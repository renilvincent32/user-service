package org.fimohealth.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	/**
	 * There are hibernate validators and Spring validators on the classpath, but Hiberante validators are
	 * enabled by default. For this app, we need Spring validators, as we need access to Spring application
	 * context for one of the validators (EmailUniqueValidator.java). So we disable Hibernate validator
	 * setting and define the localValidatorFactoryBean to override Spring validators.
	 */
	@Bean
	public Validator localValidatorFactoryBean() {
		return new LocalValidatorFactoryBean();
	}

}
