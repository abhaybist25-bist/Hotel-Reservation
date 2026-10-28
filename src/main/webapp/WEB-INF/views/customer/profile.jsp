<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <c:set var="pageTitle" value="My Profile" />
            <c:set var="currentNav" value="profile" />
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

            <main class="section">
                <div class="container container-narrow">
                    <div class="section-header" style="text-align: left; margin-bottom: 2rem;">
                        <div class="section-subtitle">Account Preferences</div>
                        <h1 class="section-title">Guest Profile</h1>
                        <p class="section-desc">View your registered details and hotel privileges.</p>
                    </div>

                    <jsp:include page="/WEB-INF/views/common/messages.jsp" />

                    <div class="summary-card">
                        <div
                            style="display: flex; align-items: center; gap: 1.5rem; margin-bottom: 2rem; padding-bottom: 1.5rem; border-bottom: 1px solid var(--color-border);">
                            <div
                                style="width: 72px; height: 72px; background: var(--color-primary); color: var(--color-gold); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 2rem; font-weight: 700;">
                                ${profileUser.fullName.substring(0, 1).toUpperCase()}
                            </div>
                            <div>
                                <h2 style="font-size: 1.5rem; margin-bottom: 0.25rem;">${profileUser.fullName}</h2>
                                <span class="badge badge-confirmed">${profileUser.role.displayName} Member</span>
                            </div>
                        </div>

                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 2rem;">
                            <div>
                                <div
                                    style="font-size: 0.8rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 700;">
                                    Email Address</div>
                                <div style="font-size: 1.05rem; font-weight: 600; color: var(--color-primary);">
                                    ${profileUser.email}</div>
                            </div>

                            <div>
                                <div
                                    style="font-size: 0.8rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 700;">
                                    Contact Phone</div>
                                <div style="font-size: 1.05rem; font-weight: 600; color: var(--color-primary);">
                                    ${profileUser.phone}</div>
                            </div>

                            <div>
                                <div
                                    style="font-size: 0.8rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 700;">
                                    Account Registered</div>
                                <div style="font-size: 1.05rem; font-weight: 600; color: var(--color-primary);">
                                    <fmt:formatDate value="${profileUser.createdAt}" pattern="MMMM dd, yyyy" />
                                </div>
                            </div>

                            <div>
                                <div
                                    style="font-size: 0.8rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 700;">
                                    Security Status</div>
                                <div style="font-size: 1.05rem; font-weight: 600; color: var(--color-success);">
                                    &#128274; BCrypt Hash Protected</div>
                            </div>
                        </div>

                        <div
                            style="border-top: 1px solid var(--color-border); padding-top: 1.5rem; display: flex; gap: 1rem;">
                            <a href="${pageContext.request.contextPath}/customer/bookings" class="btn btn-gold">
                                View My Reservations &rarr;
                            </a>
                            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-primary">
                                Book Accommodations
                            </a>
                        </div>
                    </div>
                </div>
            </main>

            <jsp:include page="/WEB-INF/views/common/footer.jsp" />