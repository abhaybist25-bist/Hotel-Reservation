<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="403 - Access Denied" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<main class="section">
    <div class="container container-narrow" style="text-align: center; padding: 4rem 1rem;">
        <div style="font-size: 4rem; color: var(--color-danger); margin-bottom: 1rem;">&#128274;</div>
        <h1 style="font-size: 2.5rem; margin-bottom: 1rem;">403 - Access Restricted</h1>
        <p style="color: var(--color-text-muted); font-size: 1.1rem; max-width: 500px; margin: 0 auto 2rem;">
            You do not possess the required permissions or role privileges to view this administrative or protected resource.
        </p>
        <div style="display: flex; justify-content: center; gap: 1rem;">
            <a href="${pageContext.request.contextPath}/home" class="btn btn-gold">Return to Homepage</a>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-primary">Sign In as Administrator</a>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

