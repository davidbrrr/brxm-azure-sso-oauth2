package org.bloomreach.xm.cms.sso;

import org.apache.wicket.request.flow.RedirectToUrlException;
import org.hippoecm.frontend.logout.CmsLogoutService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;

/**
 * Logout service to redirect user after internal logout is done.
 */
public class LogoutService extends CmsLogoutService {
  public LogoutService(IPluginContext context, IPluginConfig config) {
    super(context, config);
  }

  @Override
  protected void redirectPage() {
    //Azure requires a redirect to /logout to correctly sign user out of SSO
    throw new RedirectToUrlException(SsoConstants.LOGOUT_URL);
  }
}
