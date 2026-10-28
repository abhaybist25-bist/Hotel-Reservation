<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <c:set var="pageTitle" value="My Reservations" />
            <c:set var="currentNav" value="bookings" />
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

            <main class="section">
                <div class="container">
                    <div
                        style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; flex-wrap: wrap; gap: 1rem;">
                        <div>
                            <div class="section-subtitle">Guest Records</div>
                            <h1 class="section-title" style="margin-bottom: 0.25rem;">My Reservations</h1>
                            <p class="section-desc">Review your current, completed, and cancelled hotel stays.</p>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-gold">
                                &#10133; New Reservation
                            </a>
                        </div>
                    </div>

                    <jsp:include page="/WEB-INF/views/common/messages.jsp" />

                    <div class="admin-card">
                        <div class="admin-card-header">
                            <h2>Reservation History</h2>
                            <div style="font-size: 0.85rem; color: var(--color-text-muted);">
                                Total: <strong>${bookings != null ? bookings.size() : 0}</strong> records
                            </div>
                        </div>

                        <div class="table-responsive">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Confirmation #</th>
                                        <th>Room Details</th>
                                        <th>Check-In &bull; Check-Out</th>
                                        <th>Nights</th>
                                        <th>Total Price</th>
                                        <th>Booked On</th>
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
                                                        <a href="${pageContext.request.contextPath}/customer/booking-details?id=${b.idString}"
                                                            style="font-family:monospace; font-weight:700; color:var(--color-primary);">
                                                            ${b.bookingNumber}
                                                        </a>
                                                    </td>
                                                    <td>
                                                        <strong>${b.roomTitle}</strong><br>
                                                        <span
                                                            style="font-size:0.8rem; color:var(--color-text-muted);">Room
                                                            #${b.roomNumber} &bull; ${b.numberOfGuests} Guests</span>
                                                    </td>
                                                    <td>
                                                        ${b.checkIn} &rarr; ${b.checkOut}
                                                    </td>
                                                    <td>${b.numberOfNights}</td>
                                                    <td>
                                                        <strong>$
                                                            <fmt:formatNumber value="${b.totalAmount}"
                                                                pattern="#,##0.00" />
                                                        </strong>
                                                    </td>
                                                    <td>
                                                        <fmt:formatDate value="${b.createdAt}" pattern="MMM dd, yyyy" />
                                                    </td>
                                                    <td>
                                                        <span
                                                            class="badge badge-${b.status.name().toLowerCase()}">${b.status.displayName}</span>
                                                    </td>
                                                    <td>
                                                        <div style="display:flex; gap:0.5rem; align-items:center;">
                                                            <a href="${pageContext.request.contextPath}/customer/booking-details?id=${b.idString}"
                                                                class="btn btn-outline-primary btn-sm">
                                                                Details
                                                            </a>
                                                            <c:if test="${b.eligibleForCancellation}">
                                                                <form
                                                                    action="${pageContext.request.contextPath}/customer/cancel-booking"
                                                                    method="post" style="display:inline;"
                                                                    onsubmit="return confirm('Are you sure you want to cancel reservation #${b.bookingNumber}? This will release the room availability immediately.');">
                                                                    <input type="hidden" name="_csrf"
                                                                        value="${sessionScope.CSRF_TOKEN}">
                                                                    <input type="hidden" name="bookingId"
                                                                        value="${b.idString}">
                                                                    <button type="submit"
                                                                        class="btn btn-danger btn-sm">Cancel</button>
                                                                </form>
                                                            </c:if>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td colspan="8"
                                                    style="text-align: center; padding: 3rem; color: var(--color-text-muted);">
                                                    No reservations found. Ready to make your first booking?
                                                    <div style="margin-top: 1rem;">
                                                        <a href="${pageContext.request.contextPath}/rooms"
                                                            class="btn btn-gold btn-sm">Search Rooms</a>
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