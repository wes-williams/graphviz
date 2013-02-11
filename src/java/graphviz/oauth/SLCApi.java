package graphviz.oauth;

import org.scribe.builder.api.*;
import org.scribe.extractors.*;
import org.scribe.model.*;
import org.scribe.utils.*;
import org.scribe.oauth.*;

public class SLCApi extends DefaultApi20
{
  private static final String AUTHORIZATION_URL = "https://api.sandbox.inbloom.org/api/oauth/authorize?client_id=%s&Realm=SandboxIDP&response_type=code&redirect_uri=%s";

  @Override
  public String getAccessTokenEndpoint()
  {
    return "https://api.sandbox.inbloom.org/api/oauth/token?grant_type=authorization_code";
  }

  @Override
  public String getAuthorizationUrl(OAuthConfig config)
  {
    return String.format(AUTHORIZATION_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
  }

  @Override
  public AccessTokenExtractor getAccessTokenExtractor()
  {
    return new JsonTokenExtractor();
  }

}
