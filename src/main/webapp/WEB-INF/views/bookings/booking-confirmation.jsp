<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Reservation Confirmed" />
<c:set var="currentNav" value="bookings" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<main class="section">
    <div class="container container-narrow">
        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <div class="summary-card" style="text-align: center; padding: 3rem 2rem;">
            <div style="width: 72px; height: 72px; background: var(--color-success-bg); color: var(--color-success); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 2.5rem; margin: 0 auto 1.5rem; border: 2px solid #A7F3D0;">
                &#10004;
            </div>

            <div class="section-subtitle">Reservation Confirmed</div>
            <h1 style="font-size: 2.25rem; margin-bottom: 0.5rem;">We Look Forward to Welcoming You</h1>
            <p style="color: var(--color-text-muted); font-size: 1.05rem; margin-bottom: 2rem;">
                Your reservation has been securely saved to the MongoDB database. A confirmation has been registered to your account.
            </p>

            <!-- Confirmation Badge Box -->
            <div style="background: var(--color-gold-light); border: 2px solid var(--color-gold); border-radius: var(--radius-lg); padding: 1.5rem; margin-bottom: 2.5rem; display: inline-block; min-width: 320px;">
                <div style="font-size: 0.8rem; text-transform: uppercase; color: var(--color-gold-dark); font-weight: 700; letter-spacing: 1px;">
                    Confirmation Number
                </div>
                <div style="font-size: 2rem; font-weight: 700; color: var(--color-primary); font-family: monospace; letter-spacing: 2px; margin-top: 0.25rem;">
                    ${booking.bookingNumber}
                </div>
                <div style="margin-top: 0.5rem;">
                    <span class="badge badge-confirmed">${booking.status.displayName}</span>
                </div>
            </div>

            <!-- Receipt Detail Table -->
            <div style="text-align: left; background: var(--color-bg); padding: 1.75rem; border-radius: var(--radius-md); border: 1px solid var(--color-border); margin-bottom: 2.5rem;">
                <h3 style="font-size: 1.15rem; margin-bottom: 1rem; border-bottom: 1px solid var(--color-border); padding-bottom: 0.5rem;">
                    Reservation Summary
                </h3>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.25rem; font-size: 0.95rem;">
                    <div>
                        <span style="color: var(--color-text-muted);">Guest Name:</span><br>
                        <strong>${booking.customerName}</strong>
                    </div>
                    <div>
                        <span style="color: var(--color-text-muted);">Email:</span><br>
                        <strong>${booking.customerEmail}</strong>
                    </div>
                    <div>
                        <span style="color: var(--color-text-muted);">Accommodations:</span><br>
                        <strong>${booking.roomTitle} (Room #${booking.roomNumber})</strong>
                    </div>
                    <div>
                        <span style="color: var(--color-text-muted);">Guests:</span><br>
                        <strong>${booking.numberOfGuests} Guests</strong>
                    </div>
                    <div>
                        <span style="color: var(--color-text-muted);">Check-In:</span><br>
                        <strong>${booking.checkIn} (From 3:00 PM)</strong>
                    </div>
                    <div>
                        <span style="color: var(--color-text-muted);">Check-Out:</span><br>
                        <strong>${booking.checkOut} (Until 11:00 AM)</strong>
                    </div>
                </div>

                <div style="border-top: 1px dashed var(--color-border); padding-top: 1rem; display: flex; justify-content: space-between; align-items: baseline;">
                    <div>
                        <span style="font-size: 0.85rem; color: var(--color-text-muted);">Total Rate (${booking.numberOfNights} nights + taxes):</span>
                    </div>
                    <div>
                        <span style="font-size: 1.5rem; font-weight: 700; color: var(--color-primary);">
                            $<fmt:formatNumber value="${booking.totalAmount}" pattern="#,##0.00" />
                        </span>
                    </div>
                </div>
            </div>

            <!-- Action Controls -->
            <div style="display: flex; justify-content: center; gap: 1rem; flex-wrap: wrap;">
                <button onclick="window.print()" class="btn btn-outline-primary">
                    &#128438; Print Receipt
                </button>
                <a href="${pageContext.request.contextPath}/customer/bookings" class="btn btn-gold">
                    View My Reservations &rarr;
                </a>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-primary">
                    Return to Homepage
                </a>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

