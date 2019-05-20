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

package org.keycloak.datapath.spi;

import org.keycloak.datapath.model.LongAttributesMapping;
import org.keycloak.provider.Provider;

import java.util.List;

public interface LongAttributeService extends Provider {

    List<LongAttributesMapping> getAttributeList(String userId);

    void addAttributes(List<LongAttributesMapping> attributes, String userId);

    void deleteAttribute(LongAttributesMapping attribute, String userId);

}
