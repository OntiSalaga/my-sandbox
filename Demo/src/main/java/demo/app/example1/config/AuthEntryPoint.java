package demo.app.example1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPoint.class);
/*
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
			throws IOException, ServletException {

		logger.warn("Authentication exception " + authEx.getMessage());
		super.commence(request, response, authEx);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		setRealmName("SomeRealm");
		super.afterPropertiesSet();
	}
*/
}