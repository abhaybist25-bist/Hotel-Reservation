<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Administrator Dashboard" />
<c:set var="isAdminPage" value="true" />
<c:set var="adminSection" value="dashboard" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="admin-layout">
    <jsp:include page="/WEB-INF/views/common/admin-sidebar.jsp" />

    <div class="admin-main">
        <div class="admin-header">
            <div>
                <div class="section-subtitle">Management Console</div>
                <h1>System Overview &amp; Analytics</h1>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin/rooms/add" class="btn btn-gold">
                    &#10133; Add New Room
                </a>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <!-- Live KPI Stats Grid -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon stat-icon-gold">&#128719;</div>
                <div class="stat-info">
                    <h3>Total Rooms</h3>
                    <div class="stat-value">${stats.totalRooms}</div>
                    <span style="font-size:0.75rem; color:var(--color-success);">${stats.activeRooms} Active</span>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon stat-icon-blue">&#128221;</div>
                <div class="stat-info">
                    <h3>Total Bookings</h3>
                    <div class="stat-value">${stats.totalBookings}</div>
                    <span style="font-size:0.75rem; color:var(--color-text-muted);">Historical count</span>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon stat-icon-green">&#128197;</div>
                <div class="stat-info">
                    <h3>Upcoming Stays</h3>
                    <div class="stat-value">${stats.upcomingBookings}</div>
                    <span style="font-size:0.75rem; color:var(--color-success);">Confirmed</span>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon stat-icon-red">&#10006;</div>
                <div class="stat-info">
                    <h3>Cancelled</h3>
                    <div class="stat-value">${stats.cancelledBookings}</div>
                    <span style="font-size:0.75rem; color:var(--color-danger);">Released dates</span>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon stat-icon-gold">&#128101;</div>
                <div class="stat-info">
                    <h3>Customers</h3>
                    <div class="stat-value">${stats.registeredCustomers}</div>
                    <span style="font-size:0.75rem; color:var(--color-text-muted);">Registered accounts</span>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon stat-icon-green">&#128178;</div>
                <div class="stat-info">
                    <h3>Total Revenue</h3>
                    <div class="stat-value">$<fmt:formatNumber value="${stats.totalRevenue}" pattern="#,##0" /></div>
                    <span style="font-size:0.75rem; color:var(--color-success);">From non-cancelled stays</span>
                </div>
            </div>
        </div>

        <!-- Recent Reservations Table -->
        <div class="admin-card">
            <div class="admin-card-header">
                <h2>Recent Reservations</h2>
                <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-outline-primary btn-sm">
                    View All Reservations &rarr;
                </a>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Confirmation #</th>
                            <th>Customer</th>
                            <th>Room</th>
                            <th>Stay Dates</th>
                            <th>Total Amount</th>
                            <th>Status</th>
                            <th>Audit Details</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty recentBookings}">
                                <c:forEach var="b" items="${recentBookings}">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/admin/bookings/details?id=${b.idString}" style="font-family:monospace; font-weight:700;">
                                                ${b.bookingNumber}
                                            </a>
                                        </td>
                                        <td>
                                            <strong>${b.customerName}</strong><br>
                                            <span style="font-size:0.8rem; color:var(--color-text-muted);">${b.customerEmail}</span>
                                        </td>
                                        <td>
                                            Room #${b.roomNumber} &bull; ${b.roomType.displayName}
                                        </td>
                                        <td>
                                            ${b.checkIn} &rarr; ${b.checkOut} (${b.numberOfNights}n)
                                        </td>
                                        <td>
                                            <strong>$<fmt:formatNumber value="${b.totalAmount}" pattern="#,##0.00" /></strong>
                                        </td>
                                        <td>
                                            <span class="badge badge-${b.status.name().toLowerCase()}">${b.status.displayName}</span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/admin/bookings/details?id=${b.idString}" class="btn btn-outline-primary btn-sm">
                                                Inspect &rarr;
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="7" style="text-align:center; padding:2rem; color:var(--color-text-muted);">
                                        No reservations found in the database.
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

