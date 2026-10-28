<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="${room.title}" />
<c:set var="currentNav" value="rooms" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<main class="section">
    <div class="container">
        <!-- Breadcrumb -->
        <div style="margin-bottom: 1.5rem; font-size: 0.9rem; color: var(--color-text-muted);">
            <a href="${pageContext.request.contextPath}/home">Home</a> &gt;
            <a href="${pageContext.request.contextPath}/rooms">Rooms</a> &gt;
            <span style="color: var(--color-primary); font-weight: 600;">${room.title}</span>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <div style="display: grid; grid-template-columns: 1fr 380px; gap: 3rem; align-items: start;">
            <!-- Main Room Showcase -->
            <div>
                <div style="position: relative; border-radius: var(--radius-xl); overflow: hidden; height: 420px; box-shadow: var(--shadow-lg); margin-bottom: 2rem;">
                    <img src="${pageContext.request.contextPath}/${room.imageUrl}" alt="${room.title}" style="width:100%; height:100%; object-fit:cover;">
                    <div class="room-badge" style="font-size: 0.85rem; padding: 0.5rem 1rem;">
                        ${room.roomType.displayName} &bull; Room #${room.roomNumber}
                    </div>
                </div>

                <h1 style="font-size: 2.25rem; margin-bottom: 1rem;">${room.title}</h1>

                <div style="display: flex; gap: 2rem; margin-bottom: 2rem; padding: 1.25rem; background: var(--color-surface); border-radius: var(--radius-md); border: 1px solid var(--color-border);">
                    <div>
                        <div style="font-size: 0.8rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 600;">Guest Capacity</div>
                        <div style="font-size: 1.25rem; font-weight: 700; color: var(--color-primary);">&#128101; Up to ${room.capacity} Guests</div>
                    </div>
                    <div>
                        <div style="font-size: 0.8rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 600;">Nightly Rate</div>
                        <div style="font-size: 1.25rem; font-weight: 700; color: var(--color-gold-dark);">
                            $<fmt:formatNumber value="${room.pricePerNight}" pattern="#,##0.00" />
                        </div>
                    </div>
                    <div>
                        <div style="font-size: 0.8rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 600;">Status</div>
                        <div style="font-size: 1.25rem; font-weight: 700; color: var(--color-success);">&#10004; Available</div>
                    </div>
                </div>

                <h2 style="font-size: 1.5rem; margin-bottom: 1rem;">Room Overview &amp; Specifications</h2>
                <p style="font-size: 1.05rem; line-height: 1.8; color: var(--color-text-muted); margin-bottom: 2.5rem;">
                    ${room.description}
                </p>

                <h2 style="font-size: 1.5rem; margin-bottom: 1.25rem;">Included Signature Amenities</h2>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 3rem;">
                    <c:forEach var="amenity" items="${room.amenities}">
                        <div style="display: flex; align-items: center; gap: 0.75rem; padding: 0.75rem 1rem; background: var(--color-surface); border-radius: var(--radius-md); border: 1px solid var(--color-border);">
                            <span style="color: var(--color-gold); font-size: 1.2rem;">&#10004;</span>
                            <span style="font-size: 0.95rem; font-weight: 500;">${amenity}</span>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <!-- Booking Sidebar Card -->
            <div style="background: var(--color-surface); padding: 2rem; border-radius: var(--radius-xl); border: 1px solid var(--color-border); box-shadow: var(--shadow-xl); position: sticky; top: 6rem;">
                <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 1.5rem; padding-bottom: 1rem; border-bottom: 1px solid var(--color-border);">
                    <div>
                        <span style="font-size: 2rem; font-weight: 700; color: var(--color-primary); font-family: var(--font-heading);">
                            $<fmt:formatNumber value="${room.pricePerNight}" pattern="#,##0.00" />
                        </span>
                        <span style="color: var(--color-text-muted); font-size: 0.9rem;"> / night</span>
                    </div>
                    <div class="badge badge-confirmed">Best Rate Guarantee</div>
                </div>

                <!-- Hidden inputs for JavaScript dynamic calculation -->
                <input type="hidden" id="roomPricePerNight" value="${room.pricePerNight}">
                <input type="hidden" id="roomMaxCapacity" value="${room.capacity}">

                <form action="${pageContext.request.contextPath}/book" method="get" id="roomBookingForm">
                    <input type="hidden" name="roomId" value="${room.idString}">

                    <div class="form-group" style="margin-bottom: 1.1rem;">
                        <label for="bookingCheckIn">Check-In Date</label>
                        <input type="date" id="bookingCheckIn" name="checkIn" class="form-control" value="${checkIn}" required>
                    </div>

                    <div class="form-group" style="margin-bottom: 1.1rem;">
                        <label for="bookingCheckOut">Check-Out Date</label>
                        <input type="date" id="bookingCheckOut" name="checkOut" class="form-control" value="${checkOut}" required>
                    </div>

                    <div class="form-group" style="margin-bottom: 1.5rem;">
                        <label for="bookingGuests">Guests (Max: ${room.capacity})</label>
                        <select id="bookingGuests" name="guests" class="form-control">
                            <c:forEach var="g" begin="1" end="${room.capacity}">
                                <option value="${g}" ${guests == g ? 'selected' : ''}>${g} ${g == 1 ? 'Guest' : 'Guests'}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- Live Calculation Preview -->
                    <div style="background: var(--color-bg); padding: 1rem; border-radius: var(--radius-md); margin-bottom: 1.5rem; border: 1px dashed var(--color-border);">
                        <div style="display: flex; justify-content: space-between; font-size: 0.9rem; margin-bottom: 0.4rem;">
                            <span>Duration:</span>
                            <strong id="displayNights">Calculating...</strong>
                        </div>
                        <div style="display: flex; justify-content: space-between; font-size: 0.95rem; font-weight: 700; color: var(--color-primary);">
                            <span>Estimated Subtotal:</span>
                            <span id="displaySubtotal">Calculating...</span>
                        </div>
                        <div style="font-size: 0.75rem; color: var(--color-text-muted); margin-top: 0.25rem;">
                            *Taxes (${applicationScope.taxPercentage}%) calculated on review step
                        </div>
                    </div>

                    <button type="submit" class="btn btn-gold btn-block btn-lg">
                        Continue to Reservation &rarr;
                    </button>
                </form>

                <div style="margin-top: 1.25rem; font-size: 0.8rem; color: var(--color-text-muted); text-align: center;">
                    &#128274; Free cancellation up to check-in day &bull; No upfront deposit
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

