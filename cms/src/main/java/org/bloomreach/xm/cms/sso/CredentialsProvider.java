package org.bloomreach.xm.cms.sso;

import static org.hippoecm.repository.security.ldap.LdapConstants.PROPERTY_CREDENTIALS;
import static org.hippoecm.repository.security.ldap.LdapConstants.PROPERTY_PRINCIPAL;

import com.google.common.base.Strings;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.hippoecm.repository.security.ldap.LdapContextFactory;

/**
 * Utility interface to extract the LDAP principal and credentials from environment variables.
 */
public interface CredentialsProvider {

  /**
   * Extracts credentials from env vars.
   *
   * @param providerNode Node with LDAP config.
   * @param lcf The initialised LdapContextFactory.
   * @return LdapContextFactory with the extracted credentials set.
   * @throws RepositoryException Indicates an error reading the config.
   */
  default LdapContextFactory extractCredentials(Node providerNode, LdapContextFactory lcf) throws RepositoryException {
    if (providerNode.hasProperty(PROPERTY_PRINCIPAL)) {
      String principalEnvVar = providerNode.getProperty(PROPERTY_PRINCIPAL).getString();
      String principal = System.getenv(principalEnvVar);
      if (!Strings.isNullOrEmpty(principal)) {
        lcf.setSystemUsername(principal);
      } else {
        throw new RepositoryException("No value found for " + principalEnvVar);
      }
    }
    if (providerNode.hasProperty(PROPERTY_CREDENTIALS)) {
      String passwordEnvVar = providerNode.getProperty(PROPERTY_CREDENTIALS).getString();
      String password = System.getenv(passwordEnvVar);
      if (!Strings.isNullOrEmpty(password)) {
        lcf.setSystemPassword(password.toCharArray());
      } else {
        throw new RepositoryException("No value found for " + passwordEnvVar);
      }
    }

    return lcf;
  }
}
