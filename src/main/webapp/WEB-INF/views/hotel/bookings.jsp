<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Guest Bookings - ${hotel.name}" />
<c:set var="isAdminPage" value="true" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="admin-layout">
    <jsp:include page="/WEB-INF/views/common/hotel-sidebar.jsp" />

    <div class="admin-main">
        <div class="admin-header">
            <div>
                <div class="section-subtitle">${hotel.name}</div>
                <h1>Guest Bookings &amp; Check-In</h1>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <div class="admin-card">
            <div class="admin-card-header">
                <h2>Reservations for ${hotel.name} (${bookings != null ? bookings.size() : 0})</h2>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Confirmation #</th>
                            <th>Guest Details</th>
                            <th>Room #</th>
                            <th>Dates</th>
                            <th>Guests</th>
                            <th>Total Amount</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty bookings}">
                                <c:forEach var="b" items="${bookings}">
                                    <tr>
                                        <td>
                                            <span style="font-family:monospace; font-weight:700;">${b.bookingNumber}</span>
                                        </td>
                                        <td>
                                            <strong>${b.customerName}</strong><br>
                                            <span style="font-size:0.8rem; color:var(--color-text-muted);">${b.customerEmail} &bull; ${b.customerPhone}</span>
                                        </td>
                                        <td>Room ${b.roomNumber} (${b.roomType.displayName})</td>
                                        <td>${b.checkIn} &rarr; ${b.checkOut} (${b.numberOfNights} nts)</td>
                                        <td>${b.numberOfGuests}</td>
                                        <td><strong>$<fmt:formatNumber value="${b.totalAmount}" pattern="#,##0.00" /></strong></td>
                                        <td>
                                            <span class="badge badge-${b.status.name().toLowerCase()}">
                                                ${b.status.displayName}
                                            </span>
                                        </td>
                                        <td>
                                            <c:if test="${b.status.name() == 'CONFIRMED'}">
                                                <form action="${pageContext.request.contextPath}/hotel/bookings/status" method="post" style="display:inline;">
                                                    <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                                                    <input type="hidden" name="id" value="${b.idString}">
                                                    <input type="hidden" name="newStatus" value="CHECKED_IN">
                                                    <button type="submit" class="btn btn-outline-primary btn-sm">Check In</button>
                                                </form>
                                            </c:if>
                                            <c:if test="${b.status.name() == 'CHECKED_IN'}">
                                                <form action="${pageContext.request.contextPath}/hotel/bookings/status" method="post" style="display:inline;">
                                                    <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                                                    <input type="hidden" name="id" value="${b.idString}">
                                                    <input type="hidden" name="newStatus" value="CHECKED_OUT">
                                                    <button type="submit" class="btn btn-gold btn-sm">Check Out</button>
                                                </form>
                                            </c:if>
                                            <c:if test="${b.status.name() == 'CHECKED_OUT' || b.status.name() == 'CANCELLED'}">
                                                <span style="font-size:0.8rem; color:var(--color-text-muted);">Completed</span>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="8" style="text-align:center; padding:3rem; color:var(--color-text-muted);">
                                        No bookings received yet for ${hotel.name}.
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
