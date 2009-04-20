<%--
//============================================================================
//
// Copyright (c) 2009+ desmax74
// Copyright (c) 2009+ The OpenNMS Group, Inc.
// All rights reserved everywhere.
//
// This program was developed and is maintained by Rocco RIONERO
// ("the author") and is subject to dual-copyright according to
// the terms set in "The OpenNMS Project Contributor Agreement".
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
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
// USA.
//
// The author can be contacted at the following email address:
//
//       Massimiliano Dess�
//       desmax74@yahoo.it
//
//
//-----------------------------------------------------------------------------
// OpenNMS Network Management System is Copyright by The OpenNMS Group, Inc.
//============================================================================
--%>
<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>
<div><span><spring:message code="ui.role.name"/>:</span>${authority.name}</div><div><br/></div>
<div><span><spring:message code="authority.description"/>:</span>${authority.description}</div><div><br/></div>
<form action="authority.selection.page" method="post">
    <div class="lista">
        <div>
            <spring:message code="authority.user.assign"/>:<br/><br/>
            <select multiple="multiple" name="included" id="included" class="selectmultiple">
                <c:forEach var="itemAuthority" items="${authorityItems}" varStatus="status">
                    <option value="${itemAuthority.categoryId}">${itemAuthority.categoryName}</option>
                </c:forEach>
            </select>
        </div>
        <div style="vertical-align: bottom">
            <br/><br/><input type="button" onclick="javascript:moveFromList($('included'), $('available'));" value=" >> "/>
            <br/><br/><input type="button" onclick="javascript:moveFromList($('available'), $('included'));" value=" << " />
        </div>
        <div>
            <spring:message code="authority.user.available"/>:<br/><br/>
            <select multiple="multiple" id="available" class="selectmultiple">
                <c:forEach var="freeItem" items="${items}" varStatus="status">
                    <option value="${freeItem.categoryId}">${freeItem.categoryName}</option>
                </c:forEach>
            </select>
        </div>
    </div>
    <br/>
    <div class="pulsanti">
        <input type="hidden" name="includedHidden" id="includedHidden"/>
        <input type="hidden" name="aid" value="${param.aid}" />
        <input type="submit" onclick="javascript:setInputList($('included'),$('includedHidden'));" value="<spring:message code="authority.user.assignable"/>"/>
    </div>
</form>
<br/>