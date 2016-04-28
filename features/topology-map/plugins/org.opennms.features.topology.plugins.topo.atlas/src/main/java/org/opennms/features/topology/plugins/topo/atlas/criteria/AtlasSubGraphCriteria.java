/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.topology.plugins.topo.atlas.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.opennms.features.topology.api.NamespaceAware;
import org.opennms.features.topology.api.topo.Criteria;
import org.opennms.features.topology.plugins.topo.atlas.DefaultAtlasVertex;

public class AtlasSubGraphCriteria extends Criteria implements NamespaceAware {
    private final String namespace;

    private List<DefaultAtlasVertex> vertices = new ArrayList<>();

    public AtlasSubGraphCriteria(final String namespace) {
        this.namespace = namespace;
    }

    public void setNewRoot(DefaultAtlasVertex vertex) {
        if (vertices.contains(vertex)) {
            int index = vertices.indexOf(vertex);
            vertices = vertices.subList(0, index + 1);
        } else {
            vertices.add(vertex);
        }
    }

    public List<DefaultAtlasVertex> getVertices() {
        return vertices;
    }

    @Override
    public ElementType getType() {
        return ElementType.GRAPH;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public boolean contributesTo(String namespace) {
        return getNamespace().equals(namespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, vertices);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof AtlasSubGraphCriteria) {
            AtlasSubGraphCriteria other = (AtlasSubGraphCriteria) obj;
            boolean equals = Objects.equals(namespace, other.namespace)
                    && Objects.equals(vertices, other.vertices);
            return equals;
        }
        return false;
    }
}
