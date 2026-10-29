<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Guest Sign In" />
<c:set var="currentNav" value="login" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<main class="auth-wrapper">
    <div class="auth-card">
        <div class="auth-header">
            <img src="${pageContext.request.contextPath}/assets/images/logo.svg" alt="Hotel Emblem" style="width:48px; height:48px; margin:0 auto 1rem;">
            <h1>Sign In</h1>
            <p>Access your hotel reservations and account preferences.</p>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <form action="${pageContext.request.contextPath}/login" method="post">
            <!-- CSRF Protection Token -->
            <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">

            <div class="form-group" style="margin-bottom: 1.25rem;">
                <label for="loginEmail">Email Address</label>
                <input type="email" id="loginEmail" name="email" class="form-control" value="<c:out value='${email}' />" required placeholder="name@example.com" autocomplete="email">
            </div>

            <div class="form-group" style="margin-bottom: 1.5rem;">
                <label for="loginPassword">Password</label>
                <input type="password" id="loginPassword" name="password" class="form-control" required placeholder="Enter your password" autocomplete="current-password">
            </div>

            <button type="submit" class="btn btn-gold btn-block btn-lg">
                Sign In to Account &rarr;
            </button>
        </form>

        <div style="background-color: var(--color-gold-light); border: 1px dashed var(--color-gold); border-radius: var(--radius-md); padding: 1rem; margin-top: 1.5rem; font-size: 0.85rem; line-height: 1.6;">
            <strong>Demo / Testing Credentials:</strong><br>
            &#127976; <strong>Hotel 1 (The Grand Azure):</strong> <code>partner@grandazure.com</code> &bull; <code>Partner@123</code><br>
            &#127976; <strong>Hotel 2 (Ocean Breeze Resort):</strong> <code>partner@oceanbreeze.com</code> &bull; <code>Partner@123</code><br>
            &#128100; <strong>System Admin:</strong> <code>admin@hotelreservation.com</code> &bull; <code>Admin@12345</code>
        </div>

        <div class="auth-footer">
            Don't have an account yet? <a href="${pageContext.request.contextPath}/register">Register here</a>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

