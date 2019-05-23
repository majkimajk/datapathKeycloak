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

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        TypedQuery<LongAttributesMapping> namedQuery = this.getEntityManager().createNamedQuery("LongAttributesMapping.findByUserId", LongAttributesMapping.class);
        namedQuery.setParameter("userId", userId);
        return namedQuery.getResultList();
    }

    @Override
    public void addAttributes(List<LongAttributesMapping> attributes, String userId) {
        attributes.forEach(attribute -> {
            EntityManager entityManager = getEntityManager();
            Optional<LongAttributesMapping> optionalEntityToUpdate = findAttribute(userId, attribute.getAttributeKey(), entityManager);
            if (!optionalEntityToUpdate.isPresent()) {
                LongAttributesMapping entity = new LongAttributesMapping();
                entity.setUserId(userId);
                entity.setAttributeKey(attribute.getAttributeKey());
                entity.setAttributeValue(attribute.getAttributeValue());
                entityManager.persist(entity);
            } else {
                LongAttributesMapping entityToUpdate = optionalEntityToUpdate.get();
                entityToUpdate.setAttributeValue(attribute.getAttributeValue());
                entityManager.merge(entityToUpdate);
            }
        });
    }

    @Override
    public void deleteAttribute(LongAttributesMapping attribute, String userId) {
        EntityManager entityManager = getEntityManager();
        Optional<LongAttributesMapping> optionalEntityToDelete = findAttribute(userId, attribute.getAttributeKey(), entityManager);
        optionalEntityToDelete.ifPresent(entityManager::remove);
    }

    @Override
    public void updateAttributes(List<LongAttributesMapping> newAttributes, String userId) {
        EntityManager entityManager = getEntityManager();
        List<LongAttributesMapping> currentAttributes = getAttributeList(userId);
        List<LongAttributesMapping> attrToRemove = getAttributesToRemove(currentAttributes, newAttributes);
        attrToRemove.forEach(entityManager::remove);
        addAttributes(newAttributes, userId);
    }

    private List<LongAttributesMapping> getAttributesToRemove(List<LongAttributesMapping> currentAttributes, List<LongAttributesMapping> newAttributes) {
        return currentAttributes.stream()
                .filter(currentAttribute -> {
                    boolean isToRemove = true;
                    for (LongAttributesMapping newAttribute : newAttributes) {
                        if (currentAttribute.getAttributeKey().equals(newAttribute.getAttributeKey())) {
                            isToRemove = false;
                        }
                    }
                    return isToRemove;
                })
                .collect(Collectors.toList());
    }

    private Optional<LongAttributesMapping> findAttribute(String userId, String attibuteKey, EntityManager entityManager) {
        try {
            TypedQuery<LongAttributesMapping> namedQuery = entityManager.createNamedQuery("LongAttributesMapping.findByUserIdAndAttributeKey", LongAttributesMapping.class);
            namedQuery.setParameter("userId", userId);
            namedQuery.setParameter("attributeKey", attibuteKey);
            LongAttributesMapping singleResult = namedQuery.getSingleResult();
            return Optional.ofNullable(singleResult);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void close() {
        // Nothing to do.
    }

}
