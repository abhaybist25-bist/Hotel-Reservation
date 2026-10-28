<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="404 - Page Not Found" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<main class="section">
    <div class="container container-narrow" style="text-align: center; padding: 4rem 1rem;">
        <div style="font-size: 4rem; color: var(--color-gold); margin-bottom: 1rem;">&#128269;</div>
        <h1 style="font-size: 2.5rem; margin-bottom: 1rem;">404 - Room or Page Not Found</h1>
        <p style="color: var(--color-text-muted); font-size: 1.1rem; max-width: 500px; margin: 0 auto 2rem;">
            The requested hotel accommodation, reservation record, or URL could not be located in our reservation registry.
        </p>
        <div style="display: flex; justify-content: center; gap: 1rem;">
            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-gold">Browse Accommodations</a>
            <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-primary">Return Home</a>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

