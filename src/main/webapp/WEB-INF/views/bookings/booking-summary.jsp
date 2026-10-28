<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Review Reservation Summary" />
<c:set var="currentNav" value="rooms" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<main class="section">
    <div class="container container-narrow">
        <div class="section-header" style="text-align: left; margin-bottom: 2rem;">
            <div class="section-subtitle">Step 2 of 3 &bull; Reservation Review</div>
            <h1 class="section-title">Review &amp; Confirm Your Stay</h1>
            <p class="section-desc">Please verify your stay details and room pricing before confirming your booking.</p>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <div class="summary-card">
            <!-- Selected Room Summary Header -->
            <div style="display: flex; gap: 1.5rem; align-items: center; padding-bottom: 1.5rem; border-bottom: 1px solid var(--color-border); margin-bottom: 1.5rem;">
                <img src="${pageContext.request.contextPath}/${room.imageUrl}" alt="${room.title}" style="width: 120px; height: 90px; object-fit: cover; border-radius: var(--radius-md);">
                <div>
                    <div class="badge badge-confirmed" style="margin-bottom: 0.35rem;">${room.roomType.displayName}</div>
                    <h2 style="font-size: 1.4rem; margin-bottom: 0.25rem;">${room.title}</h2>
                    <div style="font-size: 0.85rem; color: var(--color-text-muted);">
                        Room #${room.roomNumber} &bull; Up to ${room.capacity} Guests
                    </div>
                </div>
            </div>

            <!-- Itinerary Details -->
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 1rem; background: var(--color-bg); padding: 1.25rem; border-radius: var(--radius-md); margin-bottom: 1.5rem;">
                <div>
                    <div style="font-size: 0.75rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 700;">Check-In</div>
                    <div style="font-size: 1.05rem; font-weight: 600; color: var(--color-primary);">${checkIn}</div>
                    <div style="font-size: 0.75rem; color: var(--color-text-muted);">From 3:00 PM</div>
                </div>

                <div>
                    <div style="font-size: 0.75rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 700;">Check-Out</div>
                    <div style="font-size: 1.05rem; font-weight: 600; color: var(--color-primary);">${checkOut}</div>
                    <div style="font-size: 0.75rem; color: var(--color-text-muted);">Until 11:00 AM</div>
                </div>

                <div>
                    <div style="font-size: 0.75rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 700;">Duration</div>
                    <div style="font-size: 1.05rem; font-weight: 600; color: var(--color-primary);">${breakdown.numberOfNights} ${breakdown.numberOfNights == 1 ? 'Night' : 'Nights'}</div>
                </div>

                <div>
                    <div style="font-size: 0.75rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 700;">Party Size</div>
                    <div style="font-size: 1.05rem; font-weight: 600; color: var(--color-primary);">${guests} ${guests == 1 ? 'Guest' : 'Guests'}</div>
                </div>
            </div>

            <!-- Itemized Price Breakdown Calculated by Server -->
            <h3 style="font-size: 1.15rem; margin-bottom: 0.5rem;">Itemized Financial Breakdown</h3>
            <table class="price-breakdown-table">
                <tr>
                    <td>Room Rate ($<fmt:formatNumber value="${breakdown.pricePerNight}" pattern="#,##0.00" /> &times; ${breakdown.numberOfNights} nights)</td>
                    <td class="amount">$<fmt:formatNumber value="${breakdown.subtotal}" pattern="#,##0.00" /></td>
                </tr>
                <tr>
                    <td>Hospitality &amp; State Lodging Taxes (${breakdown.taxRatePercentage}%)</td>
                    <td class="amount">$<fmt:formatNumber value="${breakdown.taxAmount}" pattern="#,##0.00" /></td>
                </tr>
                <tr class="total-row">
                    <td>Total Due (USD)</td>
                    <td class="amount">$<fmt:formatNumber value="${breakdown.totalAmount}" pattern="#,##0.00" /></td>
                </tr>
            </table>

            <!-- Demo Payment Box -->
            <div class="demo-payment-box">
                <span class="tag">Academic Demonstration Mode</span>
                <h4 style="font-size: 1rem; margin-bottom: 0.25rem; color: var(--color-primary);">Payment Simulation</h4>
                <p style="font-size: 0.85rem; color: var(--color-text-muted); max-width: 500px; margin: 0 auto;">
                    No payment gateway or real financial data is processed. Clicking "Confirm &amp; Place Reservation" will immediately execute real database persistence in MongoDB with concurrent booking lock protection.
                </p>
            </div>

            <!-- Confirmation Form -->
            <form action="${pageContext.request.contextPath}/booking/confirm" method="post">
                <!-- CSRF Token -->
                <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">

                <!-- Booking Attributes -->
                <input type="hidden" name="roomId" value="${room.idString}">
                <input type="hidden" name="checkIn" value="${checkIn}">
                <input type="hidden" name="checkOut" value="${checkOut}">
                <input type="hidden" name="guests" value="${guests}">

                <div style="display: flex; gap: 1rem; margin-top: 1.5rem;">
                    <a href="${pageContext.request.contextPath}/room-details?id=${room.idString}&checkIn=${checkIn}&checkOut=${checkOut}&guests=${guests}"
                       class="btn btn-outline-primary" style="flex: 1;">
                        &larr; Back to Room
                    </a>
                    <button type="submit" class="btn btn-gold btn-lg" style="flex: 2;">
                        Confirm &amp; Place Reservation &rarr;
                    </button>
                </div>
            </form>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

