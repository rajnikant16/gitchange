/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
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

package org.opennms.features.topology.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.opennms.features.topology.api.topo.StatusProvider;
import org.opennms.features.topology.api.topo.VertexRef;

// This class is abstract only because the history is applied based on the class name
public abstract class AbstractStatusToggleOperation extends AbstractCheckedOperation {

    private StatusProvider m_statusProvider;

    @Override
    public void execute(List<VertexRef> targets, OperationContext operationContext) {
        GraphContainer container = operationContext.getGraphContainer();

        // if already selected, deselect
        if (container.getVertexStatusProvider() == m_statusProvider) {
            container.setVertexStatusProvider(null);
        } else { // otherwise select
            container.setVertexStatusProvider(m_statusProvider);
        }
        container.redoLayout();
    }

    @Override
    public boolean display(List<VertexRef> targets, OperationContext operationContext) {
        return true;
    }

    @Override
    public String getId() {
        return getClass().getSimpleName();
    }

    @Override
    protected boolean isChecked(GraphContainer container) {
        return container.getVertexStatusProvider() != null && container.getVertexStatusProvider() == m_statusProvider;
    }

    @Override
    public Map<String, String> createHistory(GraphContainer container){
        return Collections.singletonMap(getClass().getName(), Boolean.toString(isChecked(container)));
    }

    @Override
    public void applyHistory(GraphContainer container, Map<String, String> settings) {
        String historyValue = settings.get(getClass().getName());
        boolean statusEnabled = Boolean.TRUE.toString().equals(historyValue);
        if (statusEnabled) {
            container.setVertexStatusProvider(m_statusProvider);
        } else {
            container.setVertexStatusProvider(null);
        }
    }

    public void setStatusProvider(StatusProvider statusProvider) {
        m_statusProvider = statusProvider;
    }
}