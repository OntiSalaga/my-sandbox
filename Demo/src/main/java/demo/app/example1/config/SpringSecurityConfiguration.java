package demo.app.example1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		// SECUITY ENABLED BY DEFAULT
//		super.configure(http);
//	}
//
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// DISABLE SECURITY AND ALLOW ALL REQUESTS
		http.authorizeRequests().anyRequest().permitAll();
	}

//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		// No Authentication for pattern "/greet/**"
//		// Full Authentication for others
//		http.authorizeRequests().antMatchers("/greet/**").permitAll().and().authorizeRequests().anyRequest()
//				.fullyAuthenticated().and().httpBasic();
//	}

}
