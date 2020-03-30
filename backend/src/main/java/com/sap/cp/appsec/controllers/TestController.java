package com.sap.cp.appsec.controllers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.cloud.security.xsuaa.client.OAuth2TokenResponse;
import com.sap.cloud.security.xsuaa.token.Token;
import com.sap.cloud.security.xsuaa.tokenflows.TokenFlowException;
import com.sap.cloud.security.xsuaa.tokenflows.XsuaaTokenFlows;

@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    /**
     * The injected factory for XSUAA token tokenflows.
     */
    private XsuaaTokenFlows tokenFlows;

    @Autowired
    public TestController(XsuaaTokenFlows tokenFlows) {
        this.tokenFlows = tokenFlows;
    }

    /**
     * Returns the detailed information of the XSUAA JWT token.
     * Uses a Token retrieved from the security context of Spring Security.
     *
     * @param token the XSUAA token from the request injected by Spring Security.
     * @return the requested address.
     * @throws Exception in case of an internal error.
     */
    @GetMapping("/v1/sayHello")
    public Map<String, String> sayHello(@AuthenticationPrincipal Token token) {

        logger.info("Got the Xsuaa token: {}", token.getAppToken());
        logger.info(token.toString());

        Map<String, String> result = new HashMap<>();
        result.put("grant type", token.getGrantType());
        result.put("client id", token.getClientId());
        result.put("subaccount id", token.getSubaccountId());
        result.put("logon name", token.getLogonName());
        result.put("family name", token.getFamilyName());
        result.put("given name", token.getGivenName());
        result.put("email", token.getEmail());
        result.put("authorities", String.valueOf(token.getAuthorities()));
        result.put("scopes", String.valueOf(token.getScopes()));

        return result;
    }

    /**
     * Returns some generic information from the JWT token.<br>
     * Uses a Jwt retrieved from the security context of Spring Security.
     *
     * @param jwt the JWT from the request injected by Spring Security.
     * @return the requested address.
     * @throws Exception in case of an internal error.
     */
    @GetMapping("/v2/sayHello")
    public String sayHello(@AuthenticationPrincipal Jwt jwt) {

        logger.info("Got the JWT: {}", jwt);

        logger.info(jwt.toString());

        return "Hello Jwt-Protected World!";
    }

    /**
     * An endpoint showing how to use Spring method security.
     * Only if the request principal has the given scope will the
     * method be called. Otherwise a 403 error will be returned.
     */
    @GetMapping("/v1/method")
    @PreAuthorize("hasAuthority('Read')")
    public String callMethodRemotely() {
        return "Read-protected method called!";
    }
    
    /**
     * REST endpoint showing how to fetch a client credentials Token from XSUAA using the
     * {@link XsuaaTokenFlows} API.
     * @throws TokenFlowException in case of any errors.
     */
    @GetMapping("/v3/requestClientCredentialsToken")
    public String requestClientCredentialsToken() throws TokenFlowException {

        OAuth2TokenResponse clientCredentialsTokenResponse = tokenFlows.clientCredentialsTokenFlow().execute();
        logger.info("Got the Client Credentials Token: {}", clientCredentialsTokenResponse.getAccessToken());

        return clientCredentialsTokenResponse.getDecodedAccessToken().getPayload();
    }

    /**
     * REST endpoint showing how to exchange an access token from XSUAA for another one intended for another service.
     * This endpoint shows how to use the {@link XsuaaTokenFlows} API.
     * <p>
     * The idea behind a user token exchange is to separate service-specific access scopes into separate tokens.
     * For example, if Service A has scopes specific to its functionality and Service B has other scopes, the intention is
     * that there is no single Jwt token that contains all of these scopes.<br>
     * Rather the intention is to have a Jwt token to call Service A (containing just the scopes of Service A),
     * and another one to call Service B (containing just the scopes of Service B). An application calling Service A and
     * B on behalf of a user therefore has to exchange the user's Jwt token against a token for Service A and B respectively
     * before calling these services. This scenario is handled by the user token flow.
     * <p>
     *
     *
     * @param jwt - the Jwt as a result of authentication.
     * @throws TokenFlowException in case of any errors.
     */
    @GetMapping("/v3/requestUserToken")
    public String requestUserToken(@AuthenticationPrincipal Jwt jwt) throws TokenFlowException {
        OAuth2TokenResponse userTokenResponse = tokenFlows.userTokenFlow()
                .token(jwt.getTokenValue())
                .execute();

        logger.info("Got the exchanged token for 3rd party service: {}", userTokenResponse);
        logger.info("You can now call the 3rd party service passing the exchanged token value: {}. ", userTokenResponse);

        return "<p>The access-token (decoded):</p><p>" + userTokenResponse.getDecodedAccessToken().getPayload()
                + "</p><p>The refresh-token: </p><p>" + userTokenResponse.getRefreshToken()
                + "</p><p>The access-token (encoded) can be found in the logs 'cf logs spring-security-xsuaa-usage --recent'</p>";
    }

    /**
     * REST endpoint showing how to retrieve an access token for a refresh token from XSUAA using the
     * {@link XsuaaTokenFlows} API.
     * @param jwt - the Jwt as a result of authentication.
     * @param refreshToken - the refresh token an access token is requested
     * @throws TokenFlowException in case of any errors.
     */
    @GetMapping("/v3/requestRefreshToken/{refreshToken}")
    public String requestRefreshToken(@AuthenticationPrincipal Jwt jwt, @PathVariable("refreshToken") String refreshToken) throws TokenFlowException {

        OAuth2TokenResponse refreshTokenResponse = tokenFlows.refreshTokenFlow()
        		.refreshToken(refreshToken)
                .execute();
 
        logger.info("Got the access token for the refresh token: {}", refreshTokenResponse.getAccessToken());
        logger.info("You could now inject this into Spring's SecurityContext, using: SpringSecurityContext.init(...).");

        return refreshTokenResponse.getDecodedAccessToken().getPayload();
    }

}
