<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Accommodations &amp; Suites" />
<c:set var="currentNav" value="rooms" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<main class="section">
    <div class="container">
        <div class="section-header" style="text-align: left; margin-bottom: 2rem;">
            <div class="section-subtitle">Luxury Inventory</div>
            <h1 class="section-title">Rooms, Suites &amp; Residences</h1>
            <p class="section-desc">Filter available rooms by stay dates, category, and nightly rates.</p>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <div style="display: grid; grid-template-columns: 280px 1fr; gap: 2.5rem; align-items: start;">
            <!-- Filter Sidebar -->
            <div style="background: var(--color-surface); padding: 1.75rem; border-radius: var(--radius-lg); border: 1px solid var(--color-border); box-shadow: var(--shadow-sm);">
                <h3 style="font-size: 1.15rem; margin-bottom: 1.25rem; padding-bottom: 0.5rem; border-bottom: 2px solid var(--color-gold);">
                    Refine Search
                </h3>

                <form action="${pageContext.request.contextPath}/rooms" method="get">
                    <div class="form-group" style="margin-bottom: 1.1rem;">
                        <label for="filterCheckIn">Check-In</label>
                        <input type="date" id="filterCheckIn" name="checkIn" class="form-control" value="${criteria.checkIn}" required>
                    </div>

                    <div class="form-group" style="margin-bottom: 1.1rem;">
                        <label for="filterCheckOut">Check-Out</label>
                        <input type="date" id="filterCheckOut" name="checkOut" class="form-control" value="${criteria.checkOut}" required>
                    </div>

                    <div class="form-group" style="margin-bottom: 1.1rem;">
                        <label for="filterGuests">Guests</label>
                        <select id="filterGuests" name="guests" class="form-control">
                            <c:forEach var="g" begin="1" end="6">
                                <option value="${g}" ${criteria.guests == g ? 'selected' : ''}>${g} ${g == 1 ? 'Guest' : 'Guests'}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group" style="margin-bottom: 1.1rem;">
                        <label for="filterRoomType">Room Category</label>
                        <select id="filterRoomType" name="roomType" class="form-control">
                            <option value="ALL">All Categories</option>
                            <c:forEach var="type" items="${roomTypes}">
                                <option value="${type.name()}" ${criteria.roomType == type ? 'selected' : ''}>${type.displayName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.5rem; margin-bottom: 1.1rem;">
                        <div class="form-group">
                            <label for="filterMinPrice">Min ($)</label>
                            <input type="number" id="filterMinPrice" name="minPrice" class="form-control" value="${criteria.minPrice}" placeholder="100" min="0">
                        </div>
                        <div class="form-group">
                            <label for="filterMaxPrice">Max ($)</label>
                            <input type="number" id="filterMaxPrice" name="maxPrice" class="form-control" value="${criteria.maxPrice}" placeholder="600" min="0">
                        </div>
                    </div>

                    <div class="form-group" style="margin-bottom: 1.5rem;">
                        <label for="filterSortBy">Sort By</label>
                        <select id="filterSortBy" name="sortBy" class="form-control">
                            <option value="price_asc" ${criteria.sortBy == 'price_asc' ? 'selected' : ''}>Price: Low to High</option>
                            <option value="price_desc" ${criteria.sortBy == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
                            <option value="capacity_desc" ${criteria.sortBy == 'capacity_desc' ? 'selected' : ''}>Guest Capacity</option>
                        </select>
                    </div>

                    <button type="submit" class="btn btn-gold btn-block">
                        Apply Filters
                    </button>
                    <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-primary btn-block btn-sm" style="margin-top:0.5rem;">
                        Reset Filters
                    </a>
                </form>
            </div>

            <!-- Room Listings -->
            <div>
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; padding-bottom: 0.75rem; border-bottom: 1px solid var(--color-border);">
                    <div style="font-weight: 600; color: var(--color-text-muted);">
                        Showing <strong>${rooms != null ? rooms.size() : 0}</strong> Available Rooms
                    </div>
                    <div style="font-size: 0.85rem; color: var(--color-text-muted);">
                        Stay: <strong>${criteria.checkIn}</strong> &rarr; <strong>${criteria.checkOut}</strong>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${not empty rooms}">
                        <div class="rooms-grid">
                            <c:forEach var="room" items="${rooms}">
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
                                        <h3 class="room-card-title">${room.title}</h3>
                                        <p class="room-card-desc">${room.description}</p>

                                        <div class="room-card-meta">
                                            <div class="room-meta-item">
                                                <span>&#128101;</span> Capacity: ${room.capacity}
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
                                            <a href="${pageContext.request.contextPath}/room-details?id=${room.idString}&checkIn=${criteria.checkIn}&checkOut=${criteria.checkOut}&guests=${criteria.guests}"
                                               class="btn btn-gold btn-block">
                                                Select Room &amp; Book &rarr;
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div style="background:var(--color-surface); padding:4rem 2rem; border-radius:var(--radius-lg); text-align:center; border:1px solid var(--color-border);">
                            <div style="font-size:3rem; margin-bottom:1rem; color:var(--color-gold);">&#128719;</div>
                            <h3 style="font-size:1.5rem; margin-bottom:0.75rem;">No Rooms Available For Selected Criteria</h3>
                            <p style="color:var(--color-text-muted); max-width:500px; margin:0 auto 1.5rem;">
                                All rooms in this category are fully booked or do not match your capacity and price range. Please try adjusting your dates or filters.
                            </p>
                            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-gold">Clear Filters</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

