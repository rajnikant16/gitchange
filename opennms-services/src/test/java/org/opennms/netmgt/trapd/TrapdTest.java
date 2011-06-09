//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2006 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Modifications:
//
// 2008 Mar 10: Use MockEventIpcManager in synchronous mode to speed
//              up tests and do the right verifications on events. - dj@opennms.org
// 2008 Jan 26: Test startup of trapd with Spring. - dj@opennms.org
//
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//
package org.opennms.netmgt.trapd;

import static org.junit.Assert.assertNotNull;

import java.net.InetAddress;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.netmgt.config.TrapdConfigFactory;
import org.opennms.netmgt.dao.db.OpenNMSConfigurationExecutionListener;
import org.opennms.netmgt.dao.db.TemporaryDatabaseExecutionListener;
import org.opennms.netmgt.mock.MockEventIpcManager;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.snmp.SnmpObjId;
import org.opennms.netmgt.snmp.SnmpUtils;
import org.opennms.netmgt.snmp.SnmpV1TrapBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * TODO: Merge with {@link org.opennms.netmgt.trapd.TrapHandlerTestCase}?
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({
    OpenNMSConfigurationExecutionListener.class,
    TemporaryDatabaseExecutionListener.class,
    DependencyInjectionTestExecutionListener.class
})
@ContextConfiguration(locations={
        "classpath:META-INF/opennms/applicationContext-dao.xml",
        "classpath*:/META-INF/opennms/component-dao.xml",
        "classpath:META-INF/opennms/mockEventIpcManager.xml",
        "classpath:META-INF/opennms/applicationContext-commonConfigs.xml",
        "classpath:META-INF/opennms/applicationContext-daemon.xml",
        "classpath:META-INF/opennms/applicationContext-trapDaemon.xml",
        // Overrides the port that Trapd binds to and sets newSuspectOnTrap to 'true'
        "classpath:org/opennms/netmgt/trapd/applicationContext-trapDaemonTest-snmpTrapPort.xml",
        "classpath:META-INF/opennms/smallEventConfDao.xml"
})
public class TrapdTest implements InitializingBean {
    @Resource(name="snmpTrapPort")
    Integer m_snmpTrapPort;

    @Autowired
    Trapd m_trapd;

    @Autowired
    MockEventIpcManager m_mockEventIpcManager;

    @Autowired
    TrapdConfigFactory m_trapdConfigFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        assertNotNull(m_trapd);
        assertNotNull(m_mockEventIpcManager);
    }

    @Before
    public void onSetUpInTransactionIfEnabled() throws Exception {
        m_mockEventIpcManager.setSynchronous(true);

        m_trapd.onStart();
    }

    @After
    public void onTearDownInTransactionIfEnabled() throws Exception {
        m_trapd.onStop();

        m_mockEventIpcManager.getEventAnticipator().verifyAnticipated(3000, 0, 0, 0, 0);
    }

    @Test
    public void testSnmpV1TrapSend() throws Exception {
        String localhost = "127.0.0.1";
        InetAddress localAddr = InetAddressUtils.addr(localhost);

        SnmpV1TrapBuilder pdu = SnmpUtils.getV1TrapBuilder();
        pdu.setEnterprise(SnmpObjId.get(".1.3.6.1.4.1.5813"));
        pdu.setGeneric(1);
        pdu.setSpecific(0);
        pdu.setTimeStamp(666L);
        pdu.setAgentAddress(localAddr);

        EventBuilder defaultTrapBuilder = new EventBuilder("uei.opennms.org/default/trap", "trapd");
        defaultTrapBuilder.setInterface(localAddr);
        m_mockEventIpcManager.getEventAnticipator().anticipateEvent(defaultTrapBuilder.getEvent());

        EventBuilder newSuspectBuilder = new EventBuilder("uei.opennms.org/internal/discovery/newSuspect", "trapd");
        newSuspectBuilder.setInterface(localAddr);
        m_mockEventIpcManager.getEventAnticipator().anticipateEvent(newSuspectBuilder.getEvent());

        pdu.send(localhost, m_snmpTrapPort, "public");

        // Allow time for Trapd and Eventd to do their magic
        Thread.sleep(5000);
    }
}
