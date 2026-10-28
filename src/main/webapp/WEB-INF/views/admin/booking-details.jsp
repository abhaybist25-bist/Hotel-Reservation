<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Audit Booking #${booking.bookingNumber}" />
<c:set var="isAdminPage" value="true" />
<c:set var="adminSection" value="bookings" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="admin-layout">
    <jsp:include page="/WEB-INF/views/common/admin-sidebar.jsp" />

    <div class="admin-main">
        <div class="admin-header">
            <div>
                <div class="section-subtitle">Database Audit &bull; Reservation Record</div>
                <h1>Booking #${booking.bookingNumber}</h1>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-outline-primary">
                    &larr; Back to Reservations
                </a>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem; align-items: start;">
            <!-- Left Column: Details -->
            <div class="admin-card" style="padding: 2rem;">
                <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 1.5rem; padding-bottom: 1rem; border-bottom: 1px solid var(--color-border);">
                    <div>
                        <div style="font-size: 0.8rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 700;">MongoDB Document ID</div>
                        <code style="font-size: 0.95rem; color: var(--color-primary);">${booking.idString}</code>
                    </div>
                    <div>
                        <span class="badge badge-${booking.status.name().toLowerCase()}" style="font-size: 0.85rem; padding: 0.4rem 0.85rem;">
                            ${booking.status.displayName}
                        </span>
                    </div>
                </div>

                <!-- Customer Info -->
                <h3 style="font-size: 1.15rem; margin-bottom: 1rem;">Customer Information</h3>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; background: var(--color-bg); padding: 1.25rem; border-radius: var(--radius-md); margin-bottom: 1.5rem;">
                    <div>
                        <span style="font-size: 0.8rem; color: var(--color-text-muted);">Full Name:</span><br>
                        <strong>${booking.customerName}</strong>
                    </div>
                    <div>
                        <span style="font-size: 0.8rem; color: var(--color-text-muted);">Email:</span><br>
                        <strong>${booking.customerEmail}</strong>
                    </div>
                    <div>
                        <span style="font-size: 0.8rem; color: var(--color-text-muted);">Phone:</span><br>
                        <strong>${booking.customerPhone}</strong>
                    </div>
                    <div>
                        <span style="font-size: 0.8rem; color: var(--color-text-muted);">Customer User ID:</span><br>
                        <code>${booking.userIdString}</code>
                    </div>
                </div>

                <!-- Room & Stay Details -->
                <h3 style="font-size: 1.15rem; margin-bottom: 1rem;">Accommodations &amp; Calendar</h3>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; background: var(--color-bg); padding: 1.25rem; border-radius: var(--radius-md); margin-bottom: 1.5rem;">
                    <div>
                        <span style="font-size: 0.8rem; color: var(--color-text-muted);">Reserved Room:</span><br>
                        <strong>${booking.roomTitle} (Room #${booking.roomNumber})</strong>
                    </div>
                    <div>
                        <span style="font-size: 0.8rem; color: var(--color-text-muted);">Room Type:</span><br>
                        <strong>${booking.roomType.displayName}</strong>
                    </div>
                    <div>
                        <span style="font-size: 0.8rem; color: var(--color-text-muted);">Check-In Date:</span><br>
                        <strong>${booking.checkIn}</strong>
                    </div>
                    <div>
                        <span style="font-size: 0.8rem; color: var(--color-text-muted);">Check-Out Date:</span><br>
                        <strong>${booking.checkOut}</strong>
                    </div>
                    <div>
                        <span style="font-size: 0.8rem; color: var(--color-text-muted);">Number of Nights:</span><br>
                        <strong>${booking.numberOfNights} Nights</strong>
                    </div>
                    <div>
                        <span style="font-size: 0.8rem; color: var(--color-text-muted);">Guests:</span><br>
                        <strong>${booking.numberOfGuests} Guests</strong>
                    </div>
                </div>

                <!-- Financial Snapshot -->
                <h3 style="font-size: 1.15rem; margin-bottom: 0.5rem;">Financial Snapshot (Persisted at Booking)</h3>
                <table class="price-breakdown-table">
                    <tr>
                        <td>Room Rate at Booking ($<fmt:formatNumber value="${booking.pricePerNightAtBooking}" pattern="#,##0.00" /> &times; ${booking.numberOfNights}n)</td>
                        <td class="amount">$<fmt:formatNumber value="${booking.subtotal}" pattern="#,##0.00" /></td>
                    </tr>
                    <tr>
                        <td>Taxes Calculated</td>
                        <td class="amount">$<fmt:formatNumber value="${booking.taxes}" pattern="#,##0.00" /></td>
                    </tr>
                    <tr class="total-row">
                        <td>Total Amount Charged / Due</td>
                        <td class="amount">$<fmt:formatNumber value="${booking.totalAmount}" pattern="#,##0.00" /></td>
                    </tr>
                </table>

                <!-- Timestamps -->
                <div style="font-size: 0.8rem; color: var(--color-text-muted); margin-top: 1.5rem; border-top: 1px solid var(--color-border); padding-top: 1rem;">
                    <div>MongoDB Document Created: <fmt:formatDate value="${booking.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" /></div>
                    <div>Last Modified: <fmt:formatDate value="${booking.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss" /></div>
                    <c:if test="${not empty booking.cancelledAt}">
                        <div style="color: var(--color-danger);">Cancelled: <fmt:formatDate value="${booking.cancelledAt}" pattern="yyyy-MM-dd HH:mm:ss" /></div>
                    </c:if>
                </div>
            </div>

            <!-- Right Column: Administrative Workflow Controls -->
            <div class="admin-card" style="padding: 1.5rem;">
                <h3 style="font-size: 1.15rem; margin-bottom: 1rem; border-bottom: 1px solid var(--color-border); padding-bottom: 0.5rem;">
                    Lifecycle State Action
                </h3>

                <p style="font-size: 0.85rem; color: var(--color-text-muted); margin-bottom: 1.25rem;">
                    Current Status: <strong style="color:var(--color-primary);">${booking.status.displayName}</strong><br>
                    ${booking.status.description}
                </p>

                <c:choose>
                    <c:when test="${booking.status == 'CONFIRMED'}">
                        <!-- Transition to CHECKED_IN or CANCELLED -->
                        <form action="${pageContext.request.contextPath}/admin/bookings/status" method="post" style="margin-bottom: 1rem;">
                            <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                            <input type="hidden" name="bookingId" value="${booking.idString}">
                            <input type="hidden" name="targetStatus" value="CHECKED_IN">
                            <button type="submit" class="btn btn-primary btn-block">
                                &#10004; Check In Guest
                            </button>
                        </form>

                        <form action="${pageContext.request.contextPath}/admin/bookings/status" method="post" class="admin-cancel-booking-form" data-booking-number="${booking.bookingNumber}">
                            <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                            <input type="hidden" name="bookingId" value="${booking.idString}">
                            <input type="hidden" name="targetStatus" value="CANCELLED">
                            <button type="submit" class="btn btn-danger btn-block btn-sm">
                                &#10006; Cancel Reservation
                            </button>
                        </form>
                    </c:when>

                    <c:when test="${booking.status == 'CHECKED_IN'}">
                        <!-- Transition to CHECKED_OUT -->
                        <form action="${pageContext.request.contextPath}/admin/bookings/status" method="post">
                            <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                            <input type="hidden" name="bookingId" value="${booking.idString}">
                            <input type="hidden" name="targetStatus" value="CHECKED_OUT">
                            <button type="submit" class="btn btn-gold btn-block">
                                &#128228; Complete Check Out
                            </button>
                        </form>
                    </c:when>

                    <c:otherwise>
                        <div style="background: var(--color-bg); padding: 1rem; border-radius: var(--radius-md); font-size: 0.85rem; color: var(--color-text-muted); text-align: center;">
                            This reservation is in a terminal state (<strong>${booking.status.displayName}</strong>). No further state transitions are permitted by business rules.
                        </div>
                    </c:otherwise>
                </c:choose>

                <div style="margin-top: 1.5rem; border-top: 1px solid var(--color-border); padding-top: 1rem;">
                    <button onclick="window.print()" class="btn btn-outline-primary btn-block btn-sm">
                        &#128438; Print Audit Dossier
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

