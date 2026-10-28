<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<footer class="site-footer">
    <div class="container">
        <div class="footer-grid">
            <div class="footer-col">
                <h4>The Grand Azure Hotel &amp; Suites</h4>
                <p>An exquisite coastal sanctuary delivering exceptional hospitality, refined culinary artistry, and world-class luxury accommodations overlooking Marina Bay.</p>
                <p style="color:var(--color-gold-dark); font-size:0.85rem;">Bachelor of Engineering Computer Science Project &bull; Java, JSP, Servlets &amp; MongoDB</p>
            </div>

            <div class="footer-col">
                <h4>Quick Navigation</h4>
                <ul class="footer-links">
                    <li><a href="${pageContext.request.contextPath}/home">Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/rooms">Rooms &amp; Suites</a></li>
                    <li><a href="${pageContext.request.contextPath}/customer/dashboard">Guest Dashboard</a></li>
                    <li><a href="${pageContext.request.contextPath}/customer/bookings">My Reservations</a></li>
                </ul>
            </div>

            <div class="footer-col">
                <h4>Room Categories</h4>
                <ul class="footer-links">
                    <li><a href="${pageContext.request.contextPath}/rooms?roomType=SUITE">Executive Suites</a></li>
                    <li><a href="${pageContext.request.contextPath}/rooms?roomType=DELUXE">Deluxe Ocean Rooms</a></li>
                    <li><a href="${pageContext.request.contextPath}/rooms?roomType=FAMILY">Family Haven Villas</a></li>
                    <li><a href="${pageContext.request.contextPath}/rooms?roomType=STANDARD">Classic Standards</a></li>
                </ul>
            </div>

            <div class="footer-col">
                <h4>Concierge &amp; Reservations</h4>
                <p><strong>Address:</strong><br>${applicationScope.hotelAddress != null ? applicationScope.hotelAddress : "777 Oceanfront Boulevard, Marina Bay, CA 90291"}</p>
                <p><strong>Phone:</strong> ${applicationScope.hotelPhone != null ? applicationScope.hotelPhone : "+1 (800) 555-0199"}</p>
                <p><strong>Email:</strong> ${applicationScope.hotelEmail != null ? applicationScope.hotelEmail : "reservations@grandazure.com"}</p>
            </div>
        </div>

        <div class="footer-bottom">
            <div>&copy; 2026 The Grand Azure Hotel &amp; Suites. All Rights Reserved.</div>
            <div>Powered by Java 17 Servlets, JSP &amp; MongoDB Community Server</div>
        </div>
    </div>
</footer>

<!-- JavaScript Assets -->
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/booking.js"></script>
<c:if test="${isAdminPage}">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</c:if>

</body>
</html>

