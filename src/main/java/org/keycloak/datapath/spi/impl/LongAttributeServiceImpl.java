/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.datapath.spi.impl;

import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.datapath.model.LongAttributesMapping;
import org.keycloak.datapath.spi.LongAttributeService;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;

import javax.persistence.EntityManager;
import java.util.List;

public class LongAttributeServiceImpl implements LongAttributeService {

    private final KeycloakSession session;

    public LongAttributeServiceImpl(KeycloakSession session) {
        this.session = session;
        if (getRealm() == null) {
            throw new IllegalStateException("The service cannot accept a session without a realm in its context.");
        }
    }

    private EntityManager getEntityManager() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    protected RealmModel getRealm() {
        return session.getContext().getRealm();
    }

    @Override
    public List<LongAttributesMapping> getAttributeList(String userId) {
        return getEntityManager().createNamedQuery("findByUserId", LongAttributesMapping.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public void addAttributes(List<LongAttributesMapping> attributes) {
        attributes.forEach(attribute -> {
            LongAttributesMapping entity = new LongAttributesMapping();
            String id = KeycloakModelUtils.generateId();
            //entity.setId(id);
            entity.setAttributeKey(attribute.getAttributeKey());
            entity.setAttributeValue(attribute.getAttributeValue());
            getEntityManager().persist(entity);
        });
    }

    public void close() {
        // Nothing to do.
    }

}
