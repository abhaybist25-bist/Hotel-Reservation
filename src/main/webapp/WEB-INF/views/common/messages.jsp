<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Display Request Errors --%>
<c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible" role="alert">
        <div><strong>Notice:</strong> <c:out value="${error}" /></div>
        <button type="button" class="alert-dismiss" aria-label="Close">&times;</button>
    </div>
</c:if>

<%-- Display Flash Errors from Session --%>
<c:if test="${not empty sessionScope.flashError}">
    <div class="alert alert-danger alert-dismissible" role="alert">
        <div><strong>Notice:</strong> <c:out value="${sessionScope.flashError}" /></div>
        <button type="button" class="alert-dismiss" aria-label="Close">&times;</button>
    </div>
    <c:remove var="flashError" scope="session" />
</c:if>

<%-- Display Flash Success Messages --%>
<c:if test="${not empty sessionScope.flashSuccess}">
    <div class="alert alert-success alert-dismissible" role="alert">
        <div><strong>Success:</strong> <c:out value="${sessionScope.flashSuccess}" /></div>
        <button type="button" class="alert-dismiss" aria-label="Close">&times;</button>
    </div>
    <c:remove var="flashSuccess" scope="session" />
</c:if>

<%-- Display General Info Messages --%>
<c:if test="${not empty info}">
    <div class="alert alert-info alert-dismissible" role="alert">
        <div><c:out value="${info}" /></div>
        <button type="button" class="alert-dismiss" aria-label="Close">&times;</button>
    </div>
</c:if>

