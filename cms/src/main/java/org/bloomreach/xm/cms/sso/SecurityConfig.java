package org.bloomreach.xm.cms.sso;

import static org.bloomreach.xm.cms.sso.SsoConstants.LOCAL_LOGIN_ENABLED;
import static org.bloomreach.xm.cms.sso.SsoConstants.LOCAL_LOGIN_HEADER;

import com.azure.spring.cloud.autoconfigure.aad.AadWebSecurityConfigurerAdapter;
import com.google.common.base.Strings;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

/**
 * Security configuration to place desired endpoints behind auth.
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends AadWebSecurityConfigurerAdapter {

  private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);

    //check if SSO is enabled
    String sso = System.getenv(SsoConstants.SSO_ENABLED);
    if (!Strings.isNullOrEmpty(sso) && Boolean.valueOf(sso)) {
      log.info("SSO is enabled - securing CMS endpoints.");
      http.authorizeHttpRequests(authorize -> authorize
          //utility servlets/endpoints are not secured, they have their own internal auth
          .antMatchers(new String[]{
              "/ws/indexexport", "/ping",
              "/repository/**",
              "/angular/**", "/skin/**", "/ckeditor/**", "/**.svg",
              "/logging/**",
              "/ws/**",
              SsoConstants.LOGOUT_URL //used by Azure for logging out
          }).permitAll()
          //allow local login via request headers
          .requestMatchers(new RequestHeaderRequestMatcher(LOCAL_LOGIN_HEADER, LOCAL_LOGIN_ENABLED)).permitAll()
          //everyting else is secured
          .anyRequest().authenticated());
      http.addFilterAfter(new LoginFilter(), FilterSecurityInterceptor.class);

      //logout handler to forward user to Azure's logout page
      http.logout().logoutSuccessHandler(new LogoutSuccessHandler() {
        @Override
        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
          boolean localLogin = request.getHeader(LOCAL_LOGIN_HEADER) != null
              && request.getHeader(LOCAL_LOGIN_HEADER).equalsIgnoreCase(LOCAL_LOGIN_ENABLED);
          if (!localLogin) {
            //the JSP uses Javascript to break out of the iframing done by the navapp
            String url = "https://login.microsoftonline.com/" + System.getenv("SSO_TENANT_ID") + "/oauth2/v2.0/logout";
            request.setAttribute("logoutUrl", url.toString());
            request.getRequestDispatcher(SsoConstants.LOGOUT_JSP).forward(request, response);
            response.flushBuffer();
          }
        }
      });
    } else {
      log.info("SSO is disabled.");
      http.authorizeHttpRequests(authorize -> authorize
          .anyRequest().permitAll());
    }

    //CSRF not supported by CMS
    http.csrf().disable();
    //same origin needed by CMS
    http.headers().frameOptions().sameOrigin();
  }
}
