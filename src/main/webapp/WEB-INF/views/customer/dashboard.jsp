<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Guest Dashboard" />
<c:set var="currentNav" value="dashboard" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<main class="section">
    <div class="container">
        <!-- Dashboard Header -->
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; flex-wrap: wrap; gap: 1rem;">
            <div>
                <div class="section-subtitle">Welcome Back</div>
                <h1 class="section-title" style="margin-bottom: 0.25rem;">${sessionScope.currentUser.fullName}</h1>
                <p class="section-desc">Manage your hotel reservations, upcoming stays, and preferences.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/rooms" class="btn btn-gold btn-lg">
                    &#10133; Book New Room
                </a>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <!-- Metrics Overview Grid -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon stat-icon-gold">&#128719;</div>
                <div class="stat-info">
                    <h3>Total Stays</h3>
                    <div class="stat-value">${totalBookings}</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon stat-icon-green">&#128197;</div>
                <div class="stat-info">
                    <h3>Upcoming Stays</h3>
                    <div class="stat-value">${upcomingCount}</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon stat-icon-blue">&#10004;</div>
                <div class="stat-info">
                    <h3>Completed</h3>
                    <div class="stat-value">${pastCount}</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon stat-icon-red">&#10006;</div>
                <div class="stat-info">
                    <h3>Cancelled</h3>
                    <div class="stat-value">${cancelledCount}</div>
                </div>
            </div>
        </div>

        <!-- Upcoming Reservations Table -->
        <div class="admin-card">
            <div class="admin-card-header">
                <h2>Upcoming Reservations</h2>
                <a href="${pageContext.request.contextPath}/customer/bookings" class="btn btn-outline-primary btn-sm">
                    View Complete History &rarr;
                </a>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Confirmation #</th>
                            <th>Room &amp; Category</th>
                            <th>Stay Dates</th>
                            <th>Guests</th>
                            <th>Total Rate</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty recentBookings}">
                                <c:forEach var="b" items="${recentBookings}">
                                    <tr>
                                        <td><strong>${b.bookingNumber}</strong></td>
                                        <td>
                                            ${b.roomTitle}<br>
                                            <span style="font-size:0.8rem; color:var(--color-text-muted);">Room #${b.roomNumber} &bull; ${b.roomType.displayName}</span>
                                        </td>
                                        <td>
                                            ${b.checkIn} &rarr; ${b.checkOut}<br>
                                            <span style="font-size:0.8rem; color:var(--color-text-muted);">${b.numberOfNights} Nights</span>
                                        </td>
                                        <td>${b.numberOfGuests}</td>
                                        <td><strong>$<fmt:formatNumber value="${b.totalAmount}" pattern="#,##0.00" /></strong></td>
                                        <td>
                                            <span class="badge badge-${b.status.name().toLowerCase()}">${b.status.displayName}</span>
                                        </td>
                                        <td>
                                            <div style="display:flex; gap:0.5rem;">
                                                <a href="${pageContext.request.contextPath}/customer/booking-details?id=${b.idString}" class="btn btn-outline-primary btn-sm">Details</a>
                                                <c:if test="${b.eligibleForCancellation}">
                                                    <form action="${pageContext.request.contextPath}/customer/cancel-booking" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to cancel booking #${b.bookingNumber}?');">
                                                        <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                                                        <input type="hidden" name="bookingId" value="${b.idString}">
                                                        <button type="submit" class="btn btn-danger btn-sm">Cancel</button>
                                                    </form>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="7" style="text-align: center; padding: 2.5rem; color: var(--color-text-muted);">
                                        You currently have no active upcoming reservations. Ready to plan your getaway?
                                        <div style="margin-top: 1rem;">
                                            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-gold btn-sm">Browse Accommodations</a>
                                        </div>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

