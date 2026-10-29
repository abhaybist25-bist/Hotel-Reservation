<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Partner Dashboard - ${hotel.name}" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="admin-layout">
    <jsp:include page="/WEB-INF/views/common/hotel-sidebar.jsp" />

    <main class="admin-content">
        <!-- Hero Header -->
        <div style="background: linear-gradient(135deg, #0b1a30 0%, #1e3a8a 100%); color: #fff; padding: 2rem; border-radius: 12px; margin-bottom: 2rem; box-shadow: var(--shadow-md); display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
            <div>
                <div style="color: #f59e0b; font-size: 1.25rem; margin-bottom: 0.25rem; letter-spacing: 2px;">
                    <c:forEach begin="1" end="${hotel.starRating}">&#9733;</c:forEach>
                </div>
                <h1 style="color: #fff; margin: 0 0 0.5rem; font-size: 2rem; font-weight: 700;">
                    <c:out value="${hotel.name}" />
                </h1>
                <p style="color: #cbd5e1; margin: 0; font-size: 0.95rem;">
                    &#128205; <c:out value="${hotel.address}" />, <c:out value="${hotel.city}" />, <c:out value="${hotel.state}" /> &bull; Front Desk: <c:out value="${hotel.phone}" />
                </p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/hotel/profile" class="btn btn-gold btn-lg">
                    &#9998; Edit Hotel Details
                </a>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <!-- Quick Stats Cards -->
        <div class="stats-grid" style="margin-bottom: 2rem;">
            <div class="stat-card">
                <div class="stat-icon" style="background-color: #eff6ff; color: #2563eb;">&#128719;</div>
                <div class="stat-info">
                    <div class="stat-value">${totalRooms}</div>
                    <div class="stat-label">Total Rooms in Inventory</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon" style="background-color: #f0fdf4; color: #16a34a;">&#9989;</div>
                <div class="stat-info">
                    <div class="stat-value">${activeRoomsCount}</div>
                    <div class="stat-label">Active Bookable Rooms</div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon" style="background-color: #fefce8; color: #ca8a04;">&#128221;</div>
                <div class="stat-info">
                    <div class="stat-value">${totalBookings}</div>
                    <div class="stat-label">Guest Reservations</div>
                </div>
            </div>
        </div>

        <!-- Quick Actions & Hotel Profile Card -->
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 2rem;">
            <div class="admin-card">
                <h3 style="margin-bottom: 1rem; color: var(--color-primary); display: flex; align-items: center; gap: 0.5rem;">
                    <span>&#128640;</span> Quick Hotel Actions
                </h3>
                <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                    <a href="${pageContext.request.contextPath}/hotel/profile" class="btn btn-outline-primary" style="text-align: left; padding: 0.85rem 1.25rem;">
                        <strong>&#127976; Edit Property Details</strong><br>
                        <small style="color: #64748b;">Change hotel name, address, star rating, phone, and amenities.</small>
                    </a>
                    <a href="${pageContext.request.contextPath}/hotel/rooms/add" class="btn btn-outline-primary" style="text-align: left; padding: 0.85rem 1.25rem;">
                        <strong>&#10133; Add New Room</strong><br>
                        <small style="color: #64748b;">Create and publish a new room or suite for this hotel.</small>
                    </a>
                    <a href="${pageContext.request.contextPath}/hotel/rooms" class="btn btn-outline-primary" style="text-align: left; padding: 0.85rem 1.25rem;">
                        <strong>&#128719; Manage Room Inventory</strong><br>
                        <small style="color: #64748b;">View pricing, toggle availability, and edit room amenities.</small>
                    </a>
                    <a href="${pageContext.request.contextPath}/hotel/bookings" class="btn btn-outline-primary" style="text-align: left; padding: 0.85rem 1.25rem;">
                        <strong>&#128221; View Guest Bookings</strong><br>
                        <small style="color: #64748b;">Check in guests and audit reservations for your hotel.</small>
                    </a>
                </div>
            </div>

            <div class="admin-card">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                    <h3 style="margin: 0; color: var(--color-primary); display: flex; align-items: center; gap: 0.5rem;">
                        <span>&#127976;</span> Hotel Details Snapshot
                    </h3>
                    <a href="${pageContext.request.contextPath}/hotel/profile" class="btn btn-outline-gold btn-sm">&#9998; Edit</a>
                </div>

                <div style="font-size: 0.9rem; line-height: 1.8;">
                    <div><strong>Hotel Name:</strong> <c:out value="${hotel.name}" /></div>
                    <div><strong>Rating:</strong> <span style="color: #f59e0b;"><c:forEach begin="1" end="${hotel.starRating}">&#9733;</c:forEach></span> (${hotel.starRating} Stars)</div>
                    <div><strong>Address:</strong> <c:out value="${hotel.address}" />, <c:out value="${hotel.city}" /></div>
                    <div><strong>Contact Email:</strong> <c:out value="${hotel.email}" /></div>
                    <div><strong>Front Desk:</strong> <c:out value="${hotel.phone}" /></div>
                    <div><strong>Hours:</strong> Check-in: ${hotel.checkInTime} &bull; Check-out: ${hotel.checkOutTime}</div>
                    <div style="margin-top: 0.75rem;">
                        <strong>Key Amenities:</strong>
                        <div style="display: flex; flex-wrap: wrap; gap: 0.35rem; margin-top: 0.35rem;">
                            <c:forEach var="amenity" items="${hotel.amenities}">
                                <span class="badge" style="background-color: #f1f5f9; color: #334155; padding: 0.2rem 0.5rem; font-size: 0.75rem; border-radius: 4px;">
                                    ${amenity}
                                </span>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent Reservations Table -->
        <div class="admin-card">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
                <h3 style="margin: 0; color: var(--color-primary);">Recent Guest Reservations</h3>
                <a href="${pageContext.request.contextPath}/hotel/bookings" class="btn btn-outline-primary btn-sm">View All &rarr;</a>
            </div>

            <c:choose>
                <c:when test="${not empty recentBookings}">
                    <div class="table-responsive">
                        <table class="admin-table">
                            <thead>
                                <tr>
                                    <th>Booking #</th>
                                    <th>Guest</th>
                                    <th>Room</th>
                                    <th>Check-In &rarr; Check-Out</th>
                                    <th>Total</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="b" items="${recentBookings}">
                                    <tr>
                                        <td><strong>${b.bookingNumber}</strong></td>
                                        <td>
                                            ${b.customerName}<br>
                                            <small style="color: #64748b;">${b.customerEmail}</small>
                                        </td>
                                        <td>Room ${b.roomNumber} (${b.roomType.displayName})</td>
                                        <td>${b.checkIn} &rarr; ${b.checkOut} (${b.numberOfNights} nts)</td>
                                        <td><strong>$<fmt:formatNumber value="${b.totalAmount}" pattern="#,##0.00" /></strong></td>
                                        <td>
                                            <span class="badge badge-${b.status.name().toLowerCase()}">
                                                ${b.status.displayName}
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <p style="color: #64748b; text-align: center; padding: 2rem 0;">No guest reservations recorded yet for ${hotel.name}.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
