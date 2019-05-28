package org.keycloak.datapath.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.keycloak.common.ClientConnection;
import org.keycloak.datapath.model.LongAttributesMapping;
import org.keycloak.datapath.spi.LongAttributeService;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.admin.AdminAuth;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

/**
 * @author apapros@bluesoft.net.pl
 */
public class LongAttributesRestResource {

    private static final Logger LOGGER = Logger.getLogger(LongAttributesRestResource.class);
    private static final String AUTHENTICATED_MESSAGE = "authenticated admin access for: ";
    private static final String ERROR_CAUGHT_DURING_AUTHORIZATION = "Error caught during authorization: )";

    private final ObjectMapper MAPPER = new ObjectMapper();


    private final KeycloakSession session;

    protected AppAuthManager authManager;

    public LongAttributesRestResource(KeycloakSession session) {
        this.session = session;
        this.authManager = new AppAuthManager();
    }

    @Context
    protected ClientConnection clientConnection;


    @GET
    @Path("/{userId}/longAttributes")
    public Response getLongAttributes(@PathParam("userId") String userId, @Context UriInfo uriInfo,
                                      @Context HttpServletRequest httpServletRequest,
                                      @Context HttpHeaders headers) {
        try {
            AdminAuth auth = authenticateRealmAdminRequest(headers);
            LOGGER.debug(AUTHENTICATED_MESSAGE + auth.getUser().getUsername());
            final LongAttributeService attributeService = this.session.getProvider(LongAttributeService.class);
            List<LongAttributesMapping> attributesMappings = attributeService.getAttributeList(userId);
            return Response.ok(MAPPER.writeValueAsBytes(attributesMappings)).type(MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            LOGGER.error(ERROR_CAUGHT_DURING_AUTHORIZATION + e.getMessage());
            return unauthorizedResponse();
        }
    }

    @POST
    @Path("{userId}/longAttributes")
    public Response addOrUpdateLongAttributes(@Context HttpHeaders headers, @PathParam("userId") String userId, List<LongAttributesMapping> attributes) {
        try {
            AdminAuth auth = authenticateRealmAdminRequest(headers);
            LOGGER.debug(AUTHENTICATED_MESSAGE + auth.getUser().getUsername());
            final LongAttributeService attributeService = this.session.getProvider(LongAttributeService.class);
            attributeService.addAttributes(attributes, userId);
            return okResponse();
        } catch (Exception e) {
            LOGGER.error(ERROR_CAUGHT_DURING_AUTHORIZATION + e.getMessage());
            return unauthorizedResponse();
        }
    }

    @DELETE
    @Path("{userId}/longAttributes")
    public Response deleteLongAttribute(@Context HttpHeaders headers, @PathParam("userId") String userId, LongAttributesMapping attribute) {
        try {
            AdminAuth auth = authenticateRealmAdminRequest(headers);
            LOGGER.debug(AUTHENTICATED_MESSAGE + auth.getUser().getUsername());
            final LongAttributeService attributeService = this.session.getProvider(LongAttributeService.class);
            attributeService.deleteAttribute(attribute, userId);
            return okResponse();
        } catch (Exception e) {
            LOGGER.error(ERROR_CAUGHT_DURING_AUTHORIZATION + e.getMessage());
            return unauthorizedResponse();
        }
    }

    @PUT
    @Path("{userId}/longAttributes")
    public Response updateLongAttributes(@Context HttpHeaders headers, @PathParam("userId") String userId, List<LongAttributesMapping> attributes) {
        try {
            AdminAuth auth = authenticateRealmAdminRequest(headers);
            LOGGER.debug(AUTHENTICATED_MESSAGE + auth.getUser().getUsername());
            final LongAttributeService attributeService = this.session.getProvider(LongAttributeService.class);
            attributeService.updateAttributes(attributes, userId);
            return okResponse();

        } catch (Exception e) {
            LOGGER.error(ERROR_CAUGHT_DURING_AUTHORIZATION + e.getMessage());
            return unauthorizedResponse();
        }

    }


    private Response okResponse() throws JsonProcessingException {
        return Response.ok(MAPPER.writeValueAsBytes("OK")).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    private Response unauthorizedResponse() {
        return Response.status(Response.Status.FORBIDDEN).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    private AdminAuth authenticateRealmAdminRequest(HttpHeaders headers) {
        String tokenString = authManager.extractAuthorizationHeaderToken(headers);
        if (tokenString == null) throw new NotAuthorizedException("Bearer");
        AccessToken token;
        try {
            JWSInput input = new JWSInput(tokenString);
            token = input.readJsonContent(AccessToken.class);
        } catch (JWSInputException e) {
            throw new NotAuthorizedException("Bearer token format error");
        }
        String realmName = token.getIssuer().substring(token.getIssuer().lastIndexOf('/') + 1);
        RealmManager realmManager = new RealmManager(session);
        RealmModel realm = realmManager.getRealmByName(realmName);
        if (realm == null) {
            throw new NotAuthorizedException("Unknown realm in token");
        }
        session.getContext().setRealm(realm);
        AuthenticationManager.AuthResult authResult = authManager.authenticateBearerToken(session, realm, session.getContext().getUri(), clientConnection, headers);
        if (authResult == null) {
            LOGGER.debug("Token not valid");
            throw new NotAuthorizedException("Bearer");
        }

        ClientModel client = realm.getClientByClientId(token.getIssuedFor());
        if (client == null) {
            throw new ClientErrorException("Could not find client for authorization", Response.Status.NOT_FOUND);

        }

        return new AdminAuth(realm, authResult.getToken(), authResult.getUser(), client);
    }
}
