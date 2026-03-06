# Azure Entra ID SSO for BloomReach XM 15

SSO integration using Azure AD OAuth2 with LDAP-backed user management. Toggled via the `SSO_ENABLED` environment variable.

## Authentication Flow

1. User hits CMS → Spring Security redirects to Azure AD
2. Azure AD authenticates → redirects back with OAuth2 token
3. `LoginFilter` extracts the principal, creates JCR credentials with `providerId=ldaps`
4. `AzureUserManager` validates the Spring Security principal matches → authenticates
5. CMS loads roles/groups from LDAP

## Classes

### SecurityConfig
Spring Security configuration extending `AadWebSecurityConfigurerAdapter`. When SSO is enabled, secures all CMS endpoints except utility paths (`/ping`, `/ws/**`, static assets). Supports local login bypass via `x-local-login` header. Configures Azure AD logout redirect.

### LoginFilter
Servlet filter that bridges Spring Security to the CMS. Extracts the authenticated principal from `SecurityContextHolder` and sets `UserCredentials` on the request with a dummy password and `providerId=ldaps`.

### AzureUserManager
Extends `LdapUserManager`. Authenticates users by matching the Spring Security principal against the provided credentials. Direct LDAP password authentication is disabled.

### CustomLdapSecurityProvider
Extends `LdapSecurityProvider`. Reads LDAP bind credentials from environment variables instead of storing them directly in the JCR repository. Uses `CredentialsProvider` for extraction.

### CustomSyncJob
Extends `LdapSecurityProvider.SyncJob`. Same env-var credential resolution as `CustomLdapSecurityProvider` for the LDAP sync process.

### CredentialsProvider
Interface with a default method that resolves LDAP principal/password from environment variable names stored in JCR node properties.

### LogoutService
Extends `CmsLogoutService`. Redirects to `/logout` after CMS-internal logout, triggering the Azure AD sign-out flow.

### SsoConstants
Shared constants: `SSO_ENABLED`, logout URL/JSP path, and local login header name/value.

## Environment Variables

| Variable | Description |
|---|---|
| `SSO_ENABLED` | Set to `true` to enable SSO |
| `SSO_TENANT_ID` | Azure AD tenant ID |
| `SSO_APP_ID` | Azure AD application (client) ID |
| `SSO_APP_SECRET` | Azure AD client secret |
| `SSO_LDAP_PRINCIPAL` | LDAP bind DN |
| `SSO_LDAP_CREDENTIALS` | LDAP bind password |
