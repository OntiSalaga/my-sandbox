package demo.app.example1;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// DISABLE SECURITY AND ALLOW ALL REQUESTS
		http.authorizeRequests().anyRequest().permitAll();
		http.httpBasic().disable();
		http.csrf().disable();
	}

}
