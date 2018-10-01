package com.soft.security.oauth2.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import com.soft.security.oauth2.server.configuration.CustomAuthenticationProvider;
import com.soft.security.oauth2.service.UserService;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(basePackages = { "com.soft.security" })
public class Application {

	@Autowired
	CustomAuthenticationProvider authProvider;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	public void authenticationManager(AuthenticationManagerBuilder builder, UserService userservice) throws Exception {
		builder.authenticationProvider(authProvider);
	}

	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
}
