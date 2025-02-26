<%--
- form.jsp
-
- Copyright (c) 2019 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<acme:form>
	<acme:form-textbox code="authenticated.investor.form.label.firm" path="firm"/>
	<h5><acme:message code="authenticated.investor.form.label.sector"  /></h5>
       <select name="sector">
          <c:forEach var="item" items="${params}">
            <option value="${item}"${item == sector ? 'selected':''}>${item}</option>
          </c:forEach>
        </select>
        <br>
        <br>
	<acme:form-textbox code="authenticated.investor.form.label.profile" path="profile"/>
	
	<acme:form-submit test="${command == 'create'}" code="authenticated.investor.form.button.create" action="/authenticated/investor/create"/>
	<acme:form-submit test="${command == 'update'}" code="authenticated.investor.form.button.update" action="/authenticated/investor/update"/>
	<acme:form-return code="authenticated.investor.form.button.return"/>
</acme:form>
