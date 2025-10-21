package org.bloomreach.xm.cms.sso;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.hippoecm.repository.security.ldap.LdapContextFactory;
import org.hippoecm.repository.security.ldap.LdapSecurityProvider;

/**
 * Extends the default sync job to read LDAP credentials from an environment variable.
 * The name of the env vars should be set in the repository instead of the actual principal and password.
 */
public class CustomSyncJob extends LdapSecurityProvider.SyncJob implements CredentialsProvider {

  @Override
  public LdapContextFactory getContextFactory(Node providerNode) throws RepositoryException {
    LdapContextFactory lcf = super.getContextFactory(providerNode);
    return this.extractCredentials(providerNode, lcf);
  }
}
