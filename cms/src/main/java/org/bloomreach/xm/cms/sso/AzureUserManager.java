package org.bloomreach.xm.cms.sso;

import com.google.common.base.Strings;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import org.hippoecm.repository.security.ldap.LdapUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * User manager to allow authentication of SSO users by using the LDAP connector.
 * Anyone logged in via SSO means they are also in LDAP.
 */
public class AzureUserManager extends LdapUserManager {

  private static Logger log = LoggerFactory.getLogger(AzureUserManager.class);

  @Override
  public boolean authenticate(SimpleCredentials creds) throws RepositoryException {
    // check spring context for SSO login
    final SecurityContext context = SecurityContextHolder.getContext();
    if (context != null) {
      final Authentication authentication = context.getAuthentication();
      if (authentication != null) {
        // SSO user has to match the creds passed in
        final Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedPrincipal) {
          final String username = ((AuthenticatedPrincipal) principal).getName();
          log.debug("SSO principal is '{}'.", username);
          if (!Strings.isNullOrEmpty(username) && username.equalsIgnoreCase(creds.getUserID())) {
            return authentication.isAuthenticated();
          }
        }
      }
    }

    //LDAP authentication through CMS login form is disabled, only SSO logins allowed
    log.debug("Direct LDAP authentication is disabled.");
    return false;
  }
}
