package org.keycloak.datapath.model;

import org.keycloak.Config.Scope;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

/**
 * @author apapros@bluesoft.net.pl
 */
public class DatapathJpaEntityProviderFactory implements JpaEntityProviderFactory {

	protected static final String ID = "datapath-entity-provider";
	
    @Override
    public JpaEntityProvider create(KeycloakSession session) {
        return new DatapathJpaEntityProvider();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void init(Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

}
