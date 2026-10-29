<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Create Guest Account" />
<c:set var="currentNav" value="register" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<main class="auth-wrapper">
    <div class="auth-card" style="max-width: 520px;">
        <div class="auth-header">
            <img src="${pageContext.request.contextPath}/assets/images/logo.svg" alt="Hotel Emblem" style="width:48px; height:48px; margin:0 auto 1rem;">
            <h1>Create Account</h1>
            <p>Join as a guest to make reservations, or as a hotel partner to list and manage your property.</p>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <div id="passwordMismatchError" class="alert alert-danger" style="display:none;"></div>

        <form action="${pageContext.request.contextPath}/register" method="post" id="registrationForm">
            <!-- CSRF Protection Token -->
            <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">

            <!-- Account Type Selector -->
            <div style="background-color: #f1f5f9; padding: 0.35rem; border-radius: 8px; display: flex; gap: 0.25rem; margin-bottom: 1.5rem;">
                <label style="flex: 1; text-align: center; margin: 0; cursor: pointer; padding: 0.6rem; border-radius: 6px; font-weight: 600; font-size: 0.9rem;"
                       id="customerLabel" class="active-tab">
                    <input type="radio" name="accountType" value="customer" ${empty accountType || accountType == 'customer' ? 'checked' : ''} style="display: none;" onchange="toggleAccountType()">
                    &#128100; Guest Account
                </label>
                <label style="flex: 1; text-align: center; margin: 0; cursor: pointer; padding: 0.6rem; border-radius: 6px; font-weight: 600; font-size: 0.9rem;"
                       id="hotelLabel">
                    <input type="radio" name="accountType" value="hotel" ${accountType == 'hotel' ? 'checked' : ''} style="display: none;" onchange="toggleAccountType()">
                    &#127976; Hotel Partner
                </label>
            </div>

            <!-- Hotel-Specific Fields (revealed when Hotel Partner is selected) -->
            <div id="hotelPartnerFields" style="display: ${accountType == 'hotel' ? 'block' : 'none'}; background: #faf8f4; border: 1px dashed var(--color-gold); border-radius: 8px; padding: 1.1rem; margin-bottom: 1.25rem;">
                <h4 style="margin: 0 0 0.85rem; color: var(--color-primary); font-size: 1rem;">&#127976; Hotel Property Details</h4>
                <div class="form-group" style="margin-bottom: 0.85rem;">
                    <label for="hotelName">Hotel / Property Name <span style="color:var(--color-danger);">*</span></label>
                    <input type="text" id="hotelName" name="hotelName" class="form-control" value="<c:out value='${hotelName}' />" placeholder="e.g. Sunset Bay Luxury Resort">
                </div>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem;">
                    <div class="form-group">
                        <label for="hotelCity">City / Destination</label>
                        <input type="text" id="hotelCity" name="hotelCity" class="form-control" value="<c:out value='${hotelCity}' />" placeholder="e.g. Miami Beach">
                    </div>
                    <div class="form-group">
                        <label for="hotelAddress">Street Address</label>
                        <input type="text" id="hotelAddress" name="hotelAddress" class="form-control" value="<c:out value='${hotelAddress}' />" placeholder="e.g. 100 Shore Drive">
                    </div>
                </div>
            </div>

            <div class="form-group" style="margin-bottom: 1.1rem;">
                <label for="regFullName" id="fullNameLabel">Full Legal Name</label>
                <input type="text" id="regFullName" name="fullName" class="form-control" value="<c:out value='${fullName}' />" required placeholder="Alexander Vance" autocomplete="name">
            </div>

            <div class="form-group" style="margin-bottom: 1.1rem;">
                <label for="regEmail">Email Address (Used for Login)</label>
                <input type="email" id="regEmail" name="email" class="form-control" value="<c:out value='${email}' />" required placeholder="name@example.com" autocomplete="email">
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

            <button type="submit" class="btn btn-gold btn-block btn-lg" id="submitBtn">
                Complete Registration &rarr;
            </button>
        </form>

        <script>
            function toggleAccountType() {
                var isHotel = document.querySelector('input[name="accountType"]:checked').value === 'hotel';
                var hotelFields = document.getElementById('hotelPartnerFields');
                var hotelNameInput = document.getElementById('hotelName');
                var customerLabel = document.getElementById('customerLabel');
                var hotelLabel = document.getElementById('hotelLabel');
                var fullNameLabel = document.getElementById('fullNameLabel');
                var submitBtn = document.getElementById('submitBtn');

                if (isHotel) {
                    hotelFields.style.display = 'block';
                    hotelNameInput.setAttribute('required', 'required');
                    customerLabel.style.backgroundColor = 'transparent';
                    customerLabel.style.color = '#64748b';
                    hotelLabel.style.backgroundColor = '#ffffff';
                    hotelLabel.style.color = 'var(--color-primary)';
                    hotelLabel.style.boxShadow = '0 1px 3px rgba(0,0,0,0.1)';
                    fullNameLabel.innerText = 'Manager / Representative Full Name';
                    submitBtn.innerText = 'Register Hotel Partner Account →';
                } else {
                    hotelFields.style.display = 'none';
                    hotelNameInput.removeAttribute('required');
                    hotelLabel.style.backgroundColor = 'transparent';
                    hotelLabel.style.color = '#64748b';
                    customerLabel.style.backgroundColor = '#ffffff';
                    customerLabel.style.color = 'var(--color-primary)';
                    customerLabel.style.boxShadow = '0 1px 3px rgba(0,0,0,0.1)';
                    fullNameLabel.innerText = 'Full Legal Name';
                    submitBtn.innerText = 'Complete Guest Registration →';
                }
            }
            // Initialize on load
            document.addEventListener('DOMContentLoaded', toggleAccountType);
        </script>

        <div class="auth-footer">
            Already registered? <a href="${pageContext.request.contextPath}/login">Sign in here</a>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

