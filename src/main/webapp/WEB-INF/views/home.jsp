<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Luxury Hotel &amp; Oceanfront Suites" />
<c:set var="currentNav" value="home" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<main>
    <!-- Hero Section -->
    <section class="hero">
        <div class="container">
            <div class="hero-subtitle">World-Class Hospitality &bull; Marina Bay</div>
            <h1 class="hero-title">Experience Unparalleled Coastal Luxury &amp; Timeless Elegance</h1>
            <p class="hero-desc">Indulge in bespoke oceanfront suites, award-winning gastronomic dining, and tranquil wellness experiences tailored to your every desire.</p>
        </div>
    </section>

    <!-- Floating Room Search Card -->
    <div class="container search-banner-container">
        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <div class="search-card">
            <form action="${pageContext.request.contextPath}/rooms" method="get">
                <div class="search-form-grid">
                    <div class="form-group">
                        <label for="searchCheckIn">Check-In Date</label>
                        <input type="date" id="searchCheckIn" name="checkIn" class="form-control" value="${defaultCheckIn}" required>
                    </div>

                    <div class="form-group">
                        <label for="searchCheckOut">Check-Out Date</label>
                        <input type="date" id="searchCheckOut" name="checkOut" class="form-control" value="${defaultCheckOut}" required>
                    </div>

                    <div class="form-group">
                        <label for="searchGuests">Guests</label>
                        <select id="searchGuests" name="guests" class="form-control">
                            <option value="1">1 Guest</option>
                            <option value="2" selected>2 Guests</option>
                            <option value="3">3 Guests</option>
                            <option value="4">4 Guests</option>
                            <option value="5">5+ Guests</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="searchRoomType">Room Category</label>
                        <select id="searchRoomType" name="roomType" class="form-control">
                            <option value="ALL">All Categories</option>
                            <option value="SUITE">Executive Suites</option>
                            <option value="DELUXE">Deluxe Rooms</option>
                            <option value="FAMILY">Family Villas</option>
                            <option value="STANDARD">Standard Rooms</option>
                        </select>
                    </div>

                    <div>
                        <button type="submit" class="btn btn-gold btn-block" style="height: 44px;">
                            <span>Check Availability</span> &rarr;
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <!-- Featured Accommodations Section -->
    <section class="section">
        <div class="container">
            <div class="section-header">
                <div class="section-subtitle">Exquisite Living</div>
                <h2 class="section-title">Signature Accommodations</h2>
                <p class="section-desc">Each guest room and suite is meticulously appointed with fine Italian linens, artisanal marble baths, and tranquil ocean vistas.</p>
            </div>

            <div class="rooms-grid">
                <c:forEach var="room" items="${featuredRooms}">
                    <div class="room-card">
                        <div class="room-card-image">
                            <img src="${pageContext.request.contextPath}/${room.imageUrl}" alt="${room.title}">
                            <div class="room-badge">${room.roomType.displayName}</div>
                            <div class="room-price-tag">
                                $<fmt:formatNumber value="${room.pricePerNight}" pattern="#,##0.00" />
                                <span>/ night</span>
                            </div>
                        </div>

                        <div class="room-card-body">
                            <c:if test="${not empty room.hotelName}">
                                <div style="font-size: 0.8rem; color: var(--color-gold-dark); font-weight: 600; margin-bottom: 0.35rem;">
                                    &#127976; ${room.hotelName}
                                </div>
                            </c:if>
                            <h3 class="room-card-title">${room.title}</h3>
                            <p class="room-card-desc">${room.description}</p>

                            <div class="room-card-meta">
                                <div class="room-meta-item">
                                    <span>&#128101;</span> Up to ${room.capacity} Guests
                                </div>
                                <div class="room-meta-item">
                                    <span>&#128719;</span> Room #${room.roomNumber}
                                </div>
                            </div>

                            <div class="room-amenities-tags">
                                <c:forEach var="amenity" items="${room.amenities}" varStatus="status">
                                    <c:if test="${status.index < 4}">
                                        <span class="amenity-tag">${amenity}</span>
                                    </c:if>
                                </c:forEach>
                            </div>

                            <div class="room-card-footer">
                                <a href="${pageContext.request.contextPath}/room-details?id=${room.idString}&checkIn=${defaultCheckIn}&checkOut=${defaultCheckOut}&guests=2"
                                   class="btn btn-outline-primary btn-block">
                                    View Specifications &amp; Book
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <div style="text-align: center; margin-top: 3rem;">
                <a href="${pageContext.request.contextPath}/rooms" class="btn btn-gold btn-lg">
                    Browse All Rooms &amp; Suites &rarr;
                </a>
            </div>
        </div>
    </section>

    <!-- Amenities & Highlights Section -->
    <section class="section section-alt">
        <div class="container">
            <div class="section-header">
                <div class="section-subtitle">Hospitality Reimagined</div>
                <h2 class="section-title">Curated Guest Privileges</h2>
                <p class="section-desc">Designed to deliver an extraordinary sanctuary for holiday travelers, executive retreats, and memorable family vacations.</p>
            </div>

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 2rem;">
                <div style="background:var(--color-bg); padding:2rem; border-radius:var(--radius-lg); text-align:center; border:1px solid var(--color-border);">
                    <div style="font-size:2.5rem; margin-bottom:1rem; color:var(--color-gold);">&#127863;</div>
                    <h3 style="font-size:1.25rem; margin-bottom:0.75rem;">Michelin-Inspired Dining</h3>
                    <p style="color:var(--color-text-muted); font-size:0.9rem;">Savor authentic Mediterranean seafood and artisanal seasonal menus crafted by our executive culinary masters.</p>
                </div>

                <div style="background:var(--color-bg); padding:2rem; border-radius:var(--radius-lg); text-align:center; border:1px solid var(--color-border);">
                    <div style="font-size:2.5rem; margin-bottom:1rem; color:var(--color-gold);">&#128704;</div>
                    <h3 style="font-size:1.25rem; margin-bottom:0.75rem;">Azure Wellness Spa</h3>
                    <p style="color:var(--color-text-muted); font-size:0.9rem;">Indulge in holistic organic facials, deep-tissue therapy, and restorative thermal plunge pools overlooking the bay.</p>
                </div>

                <div style="background:var(--color-bg); padding:2rem; border-radius:var(--radius-lg); text-align:center; border:1px solid var(--color-border);">
                    <div style="font-size:2.5rem; margin-bottom:1rem; color:var(--color-gold);">&#127946;</div>
                    <h3 style="font-size:1.25rem; margin-bottom:0.75rem;">Heated Infinity Pool</h3>
                    <p style="color:var(--color-text-muted); font-size:0.9rem;">Unwind in private curtained cabanas with bespoke cocktail service beside our signature heated saltwater infinity edge.</p>
                </div>

                <div style="background:var(--color-bg); padding:2rem; border-radius:var(--radius-lg); text-align:center; border:1px solid var(--color-border);">
                    <div style="font-size:2.5rem; margin-bottom:1rem; color:var(--color-gold);">&#128084;</div>
                    <h3 style="font-size:1.25rem; margin-bottom:0.75rem;">White-Glove Concierge</h3>
                    <p style="color:var(--color-text-muted); font-size:0.9rem;">Private airport yacht charters, personalized excursion planning, and round-the-clock priority room service.</p>
                </div>
            </div>
        </div>
    </section>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

