<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    ib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

    pageTitle" value="Reservation #${booking.bookingNumber}" />
    currentNav" value="bookings" />
    page="/WEB-INF/views/common/header.jsp" />
    page="/WEB-INF/views/common/navbar.jsp" />

    "section">
    clacontainer container-narrow">
    <div smargin-bottom: 1.5rem; font-size: 0.9rem; color: var(--color-text-muted);">
        <a hageContext.request.contextPath}/customer/dashboard">Dashboard</a> &gt;
        <a href="${pageContext.request.contextPath}/customer/bookings">My Reservations</a> &gt;
        "color: var(--color-primary); font-weight: 600;">#${booking.bookingNumber}</span>
    </div>

    <jsp:i page="/WEB-INF/views/common/messages.jsp" />

    <divsummary-card">
        <!-- Header -->
        disp
        le="font-size: 0.8rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 700;">Booking
        Confirmation</div>
        <h1 stylont- <s"badge badge-${booking.status.name().toLowerCase()}" style="font-size: 0.85rem; paddiatus.displayName}
        </span>
    </div>
   </div>
           <!-- Room Inf
   <div style=" displ flex; gap: 1.5rem; align-items: center; margin-bottom: 1.5rem;">
            <img srpageContext.request.contextPath}/${booking.roomImageUrl}" alt="${booking.roomTitle}"
                style="width: 140px; height: 100px; object-fit: cover; border-radius: var(--radius-md);">

            <h2 style="font-size: 1.35rem; margin-bottom: 0.25rem;">${booking.roomTitle}</h2>
            <div style="color: var(--color-text-muted); font-size: 0.9rem;">
                Room #${booking.roomNumber} &bull; ${booking.roomType.displayName}
                </di color: var(--color-text-mu ze: ${booking.numberOfGuests} Guests </div>
                </di </div>

                <!-- Stay T
 <div style="display grid; grid-template-columns: 1fr 1fr 1fr; gap: 1rem; background: var(--color-bg); padding: 1.25rem; border-radius: var(--radius-md); margin-bottom: 1.5rem;">
       <div>
             <div stfont-size: 0.75rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 700;">Check-In</div>
               <le="font-size: 1.1rem; font-weight: 600; color: var(--color-primary);">${booking.checkIn}</div>
             style="font-size: 0.75rem; color: var(--color-text-muted);">From 3:00 PM</div>
                </div>
     <div>
             stytle="font-size: 1.1rem; font-weight: 600; color: var(--color-primary);">${booking.checkOut}</div>
           <div le="font-size: 0.75rem; color: var(--color-text-muted);">Until 11:00 AM</div>
         </div> -size: 0.75rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 700;">Stay Duration</div>
                    font-size: 1.1rem; font-weight: 600; color: var(--color-primary);">${booking.nu
</div>

<!-- Price Breakdown Snapshot -->
                <h3 style="font-size: 1. kdown-table">
                    <td>Room Re Snapshot ($<fmt:formatNumber value="${booking.pricePerNightAtBooking}" pattert">$
                            <fmt:formatNumber value="${booking.subtotal}" pattern="#,##0.00" />
                    </td>
                    </tr>
                    <tr>
                        <tding &amp; Hospitality Taxes</td>
                            <td moun ">
        <td>Total Amount Paid / Due (USD)</td>t">$
                                <fmt:formatNumber value="${booking.totalAmount}" pattern="#,##0.00" />
                            </td>
                    </tr>
                    </table>

                    <!-- imestamps -->
                    font-size: 0.8rem; color: var(--color-text-muted); margin-bottom: 2rem; border-top: 1px sol :
                    <fmt:formatDate value="${booking.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" />
            </div>
            t Updated:
            <fmt:formatDate value="${booking.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss" />
            </div>
            ${not empty booking.cancelledAt}">
            color: var(--color-danger
            <!-- Actx; justify-content: space-between; align-items: center; flex-wrap: 
                "${pageContext.request.contextPath}/customer/bookings" class="btn btn-outline-primary">
                r; Back to Reservations
                    
                    ick="window.print()"int
                

                <c:ioking.eligibleForCancellation}">
                    ="${pageContext.requ"hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">ype="hidden" name="bookingId" value="${booking.idString}">
                ton type="submit" class="btn btn-danger">
                Cancel Reservation
                        </button>
            rm>
             
        </div> 
ws/common/footer.jsp" />