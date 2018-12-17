package demo.app.example1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationEntryPoint /*extends BasicAuthenticationEntryPoint*/ {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationEntryPoint.class);
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