package org.keycloak.datapath.model;

import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author apapros@bluesoft.net.pl
 */
public class DatapathJpaEntityProvider implements JpaEntityProvider {

    @Override
    public List<Class<?>> getEntities() {
        List<Class<?>> entities = new ArrayList<>();
        entities.add(LongAttributesMapping.class);
        return entities;
    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/datapath-changelog.xml";
    }

    @Override
    public void close() {
    }

    @Override
    public String getFactoryId() {
        return DatapathJpaEntityProviderFactory.ID;
    }
}
