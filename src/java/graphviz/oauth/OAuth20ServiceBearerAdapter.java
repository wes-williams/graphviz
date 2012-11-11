package graphviz.oauth;

import org.scribe.model.*;
import org.scribe.oauth.OAuth20ServiceImpl;
import org.scribe.builder.api.DefaultApi20;

public class OAuth20ServiceBearerAdapter extends OAuth20ServiceImpl 
{
  private OAuth20ServiceImpl service;  

  public OAuth20ServiceBearerAdapter(OAuth20ServiceImpl service)
  {
    super(null,null);
    this.service = service;
  }

  public Token getRequestToken() {
    return service.getRequestToken();
  }

  public Token getAccessToken(Token requestToken, Verifier verifier) 
  {
    return service.getAccessToken(requestToken, verifier);
  }

  public void signRequest(Token accessToken, OAuthRequest request)
  {
    // all of this to add this one line...
    request.addHeader("Authorization","Bearer " + accessToken.getToken());
  }

  public String getVersion() 
  {
    return service.getVersion();
  }

  public String getAuthorizationUrl(Token requestToken) 
  {
    return service.getAuthorizationUrl(requestToken);
  }

}
