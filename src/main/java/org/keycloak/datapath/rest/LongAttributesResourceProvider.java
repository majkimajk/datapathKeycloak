package org.keycloak.datapath.rest;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

/**
 * @author apapros@bluesoft.net.pl
 */
public class LongAttributesResourceProvider implements RealmResourceProvider {

    private KeycloakSession session;

    public LongAttributesResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return new LongAttributesRestResource(session);
    }


    @Override
    public void close() {
    }
}
