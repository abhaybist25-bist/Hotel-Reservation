<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Create Guest Account" />
<c:set var="currentNav" value="register" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<main class="auth-wrapper">
    <div class="auth-card">
        <div class="auth-header">
            <img src="${pageContext.request.contextPath}/assets/images/logo.svg" alt="Hotel Emblem" style="width:48px; height:48px; margin:0 auto 1rem;">
            <h1>Guest Registration</h1>
            <p>Create an account to reserve accommodations and view your bookings.</p>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <div id="passwordMismatchError" class="alert alert-danger" style="display:none;"></div>

        <form action="${pageContext.request.contextPath}/register" method="post" id="registrationForm">
            <!-- CSRF Protection Token -->
            <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">

            <div class="form-group" style="margin-bottom: 1.1rem;">
                <label for="regFullName">Full Legal Name</label>
                <input type="text" id="regFullName" name="fullName" class="form-control" value="<c:out value='${fullName}' />" required placeholder="Alexander Vance" autocomplete="name">
            </div>

            <div class="form-group" style="margin-bottom: 1.1rem;">
                <label for="regEmail">Email Address</label>
                <input type="email" id="regEmail" name="email" class="form-control" value="<c:out value='${email}' />" required placeholder="alex@example.com" autocomplete="email">
            </div>

            <div class="form-group" style="margin-bottom: 1.1rem;">
                <label for="regPhone">Phone Number</label>
                <input type="tel" id="regPhone" name="phone" class="form-control" value="<c:out value='${phone}' />" required placeholder="+1 (555) 234-5678" autocomplete="tel">
            </div>

            <div class="form-group" style="margin-bottom: 1.1rem;">
                <label for="regPassword">Password (Minimum 6 characters)</label>
                <input type="password" id="regPassword" name="password" class="form-control" required placeholder="Create a secure password" minlength="6" autocomplete="new-password">
            </div>

            <div class="form-group" style="margin-bottom: 1.5rem;">
                <label for="regConfirmPassword">Confirm Password</label>
                <input type="password" id="regConfirmPassword" name="confirmPassword" class="form-control" required placeholder="Re-enter password" minlength="6" autocomplete="new-password">
            </div>

            <button type="submit" class="btn btn-gold btn-block btn-lg">
                Complete Registration &rarr;
            </button>
        </form>

        <div class="auth-footer">
            Already registered? <a href="${pageContext.request.contextPath}/login">Sign in here</a>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

