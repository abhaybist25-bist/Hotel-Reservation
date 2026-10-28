<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <c:set var="pageTitle" value="500 - Server Error" />
        <jsp:include page="/WEB-INF/views/common/header.jsp" />
        <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

        <main class="section">
            <div class="container container-narrow" style="text-align: center; padding: 4rem 1rem;">
                <div style="font-size: 4rem; color: var(--color-danger); margin-bottom: 1rem;">&#9888;</div>
                <h1 style="font-size: 2.5rem; margin-bottom: 1rem;">500 - System Encountered an Issue</h1>
                <p style="color: var(--color-text-muted); font-size: 1.1rem; max-width: 500px; margin: 0 auto 2rem;">
                    An unexpected error occurred while processing your request. Our engineering team has been notified.
                    If MongoDB server is not running locally, please ensure the MongoDB service is active.
                </p>
                <div style="display: flex; justify-content: center; gap: 1rem;">
                    <a href="${pageContext.request.contextPath}/home" class="btn btn-gold">Return to Homepage</a>
                    <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-primary">Browse Rooms</a>
                </div>
            </div>
        </main>

        <jsp:include page="/WEB-INF/views/common/footer.jsp" />