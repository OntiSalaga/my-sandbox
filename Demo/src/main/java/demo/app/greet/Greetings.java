package demo.app.greet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/greet")
public class Greetings {
	private static final Logger logger = LoggerFactory.getLogger(Greetings.class);

	@RequestMapping("/sayHello")
	public String greeting() {
		logger.info("Hello World!");
		return "Hello World!";
	}
}
