package com.contact.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(this.userDetailsService());
		provider.setPasswordEncoder(this.passwordEncoder());
		return provider;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
		.authorizeHttpRequests(req -> {
			req.requestMatchers("/user/**").hasRole("USER")
			.requestMatchers("/admin/**").hasRole("ADMIN")
			.anyRequest().permitAll();
		})
		.formLogin(formLogin -> {
			formLogin.loginPage("/login")
			.loginProcessingUrl("/do-login")
			.defaultSuccessUrl("/user/dashboard");
			});
//		.logout(t -> {
//			t.logoutUrl("/logout")
//			.logoutSuccessUrl("/login?logout") 
//			.invalidateHttpSession(true)
//			.deleteCookies("JSESSIONID");
//			});
			
		return http.build();
	}
	
	

}
