<%--
- form-moment.tag
-
- Copyright (c) 2019 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@tag language="java" body-content="empty"
	import="acme.framework.helpers.MessageHelper"
%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<%@attribute name="path" required="true" type="java.lang.String"%>
<%@attribute name="code" required="true" type="java.lang.String"%>
<%@attribute name="placeholder" required="false" type="java.lang.String"%>
<%@attribute name="readonly" required="false" type="java.lang.Boolean"%>

<jstl:if test="${placeholder == null}">
	<jstl:set var="placeholder" value="${MessageHelper.getMessage('default.placeholder.moment')}"/>	
</jstl:if>


<jstl:if test="${readonly == null}">
	<jstl:set var="readonly" value="false"/>
</jstl:if>

<div class="form-group">
	<input 
		id="${path}" 
		name="${path}"
		value="<acme:print value="${requestScope[path]}"/>"
		type="hidden"
		class="form-control"
		placeHolder="${placeholder}"
		<jstl:if test="${readonly}">
       		readonly
       	</jstl:if>
	/>
	<acme:form-errors path="${path}"/>
</div>
