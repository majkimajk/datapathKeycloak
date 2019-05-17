package org.keycloak.datapath.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.keycloak.datapath.model.LongAttributesMapping;
import org.keycloak.datapath.spi.LongAttributeService;
import org.keycloak.models.KeycloakSession;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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



    @POST
    @Path("/longAttributes")
    public Response getLongAttributes(@Context UriInfo uriInfo,
                                 @Context HttpServletRequest httpServletRequest,
                                 @Context HttpHeaders headers) throws JsonProcessingException {
        final LongAttributeService attributeService = this.session.getProvider(LongAttributeService.class);
        List<LongAttributesMapping> attributesMappings = attributeService.getAttributeList("333");
        return createTokenNotActiveResponse();
    }

    private Response createTokenNotActiveResponse() throws JsonProcessingException {
        return Response.ok(MAPPER.writeValueAsBytes("aaaa")).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
