package org.keycloak.datapath.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.keycloak.datapath.model.LongAttributesMapping;
import org.keycloak.datapath.spi.LongAttributeService;
import org.keycloak.models.KeycloakSession;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

/**
 * @author apapros@bluesoft.net.pl
 */
public class LongAttributesRestResource {

    private static final Logger LOGGER = Logger.getLogger(LongAttributesRestResource.class);

    private final ObjectMapper MAPPER = new ObjectMapper();


    private final KeycloakSession session;

    public LongAttributesRestResource(KeycloakSession session) {
        this.session = session;
    }


    @GET
    @Path("/{userId}/longAttributes")
    public Response getLongAttributes(@PathParam("userId") String userId, @Context UriInfo uriInfo,
                                      @Context HttpServletRequest httpServletRequest,
                                      @Context HttpHeaders headers) throws JsonProcessingException {
        final LongAttributeService attributeService = this.session.getProvider(LongAttributeService.class);
        List<LongAttributesMapping> attributesMappings = attributeService.getAttributeList(userId);
        return Response.ok(MAPPER.writeValueAsBytes(attributesMappings)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @POST
    @Path("{userId}/longAttributes")
    public Response addOrUpdateLongAttributes(@PathParam("userId") String userId, List<LongAttributesMapping> attributes) throws JsonProcessingException {

        final LongAttributeService attributeService = this.session.getProvider(LongAttributeService.class);
        attributeService.addAttributes(attributes, userId);
        return okResponse();
    }

    @DELETE
    @Path("{userId}/longAttributes")
    public Response deleteLongAttribute(@PathParam("userId") String userId, LongAttributesMapping attribute) throws JsonProcessingException {
        final LongAttributeService attributeService = this.session.getProvider(LongAttributeService.class);
        attributeService.deleteAttribute(attribute, userId);
        return okResponse();
    }

    @POST
    @Path("{userId}/update/longAttributes")
    public Response updateLongAttributes(@PathParam("userId") String userId, List<LongAttributesMapping> attributes) throws JsonProcessingException {

        final LongAttributeService attributeService = this.session.getProvider(LongAttributeService.class);
        attributeService.updateAttributes(attributes, userId);
        return okResponse();
    }


    private Response okResponse() throws JsonProcessingException {
        return Response.ok(MAPPER.writeValueAsBytes("OK")).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
