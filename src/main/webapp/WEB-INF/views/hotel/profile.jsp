<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Edit Hotel Details - Partner Portal" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="admin-layout">
    <jsp:include page="/WEB-INF/views/common/hotel-sidebar.jsp" />

    <main class="admin-content">
        <div class="admin-header">
            <div>
                <h1>Edit Hotel Details</h1>
                <p>Manage your hotel's public profile, address, contact information, star rating, and guest amenities.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/hotel/dashboard" class="btn btn-outline-primary btn-sm">&larr; Back to Dashboard</a>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem; align-items: start;">
            <!-- Main Form Card -->
            <div class="admin-card">
                <h3 style="margin-bottom: 1.5rem; color: var(--color-primary); border-bottom: 1.5px solid var(--color-border); padding-bottom: 0.75rem;">
                    &#127976; Hotel Information &amp; Branding
                </h3>

                <form action="${pageContext.request.contextPath}/hotel/profile" method="post" id="hotelProfileForm">
                    <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">

                    <!-- Hotel Name -->
                    <div class="form-group" style="margin-bottom: 1.25rem;">
                        <label for="hotelName" style="font-weight: 600;">Hotel / Property Name <span style="color:var(--color-danger);">*</span></label>
                        <input type="text" id="hotelName" name="name" class="form-control"
                               value="<c:out value='${hotel.name}' />" required placeholder="e.g. The Grand Azure Hotel & Suites" style="font-size: 1.05rem;">
                    </div>

                    <!-- Star Rating & Property Type -->
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.25rem;">
                        <div class="form-group">
                            <label for="starRating" style="font-weight: 600;">Star Classification</label>
                            <select id="starRating" name="starRating" class="form-control">
                                <option value="5" ${hotel.starRating == 5 ? 'selected' : ''}>&#9733;&#9733;&#9733;&#9733;&#9733; (5 Stars - Luxury)</option>
                                <option value="4" ${hotel.starRating == 4 ? 'selected' : ''}>&#9733;&#9733;&#9733;&#9733;&#9734; (4 Stars - Premium)</option>
                                <option value="3" ${hotel.starRating == 3 ? 'selected' : ''}>&#9733;&#9733;&#9733;&#9734;&#9734; (3 Stars - Standard)</option>
                                <option value="2" ${hotel.starRating == 2 ? 'selected' : ''}>&#9733;&#9733;&#9734;&#9734;&#9734; (2 Stars - Budget)</option>
                                <option value="1" ${hotel.starRating == 1 ? 'selected' : ''}>&#9733;&#9734;&#9734;&#9734;&#9734; (1 Star - Basic)</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="hotelImage" style="font-weight: 600;">Cover Image / Asset Path</label>
                            <input type="text" id="hotelImage" name="imageUrl" class="form-control"
                                   value="<c:out value='${hotel.imageUrl}' />" placeholder="assets/images/hero-hotel.svg">
                        </div>
                    </div>

                    <!-- Description -->
                    <div class="form-group" style="margin-bottom: 1.25rem;">
                        <label for="hotelDescription" style="font-weight: 600;">Hotel Description &amp; Overview</label>
                        <textarea id="hotelDescription" name="description" rows="4" class="form-control"
                                  placeholder="Describe your property, beachfront access, dining experiences, hospitality philosophy..."><c:out value="${hotel.description}" /></textarea>
                        <small style="color: #64748b;">This description is displayed prominently to guests browsing accommodations.</small>
                    </div>

                    <!-- Address & Location Section -->
                    <h4 style="margin: 2rem 0 1rem; color: var(--color-primary); border-bottom: 1px solid var(--color-border); padding-bottom: 0.5rem;">
                        &#128205; Location &amp; Address
                    </h4>

                    <div class="form-group" style="margin-bottom: 1rem;">
                        <label for="hotelAddress" style="font-weight: 600;">Street Address</label>
                        <input type="text" id="hotelAddress" name="address" class="form-control"
                               value="<c:out value='${hotel.address}' />" placeholder="100 Oceanfront Promenade">
                    </div>

                    <div style="display: grid; grid-template-columns: 2fr 1fr 1fr; gap: 1rem; margin-bottom: 1.5rem;">
                        <div class="form-group">
                            <label for="hotelCity" style="font-weight: 600;">City</label>
                            <input type="text" id="hotelCity" name="city" class="form-control"
                                   value="<c:out value='${hotel.city}' />" placeholder="Azure Bay">
                        </div>
                        <div class="form-group">
                            <label for="hotelState" style="font-weight: 600;">State / Province</label>
                            <input type="text" id="hotelState" name="state" class="form-control"
                                   value="<c:out value='${hotel.state}' />" placeholder="California">
                        </div>
                        <div class="form-group">
                            <label for="hotelCountry" style="font-weight: 600;">Country</label>
                            <input type="text" id="hotelCountry" name="country" class="form-control"
                                   value="<c:out value='${hotel.country}' />" placeholder="United States">
                        </div>
                    </div>

                    <!-- Contact & Operating Hours Section -->
                    <h4 style="margin: 2rem 0 1rem; color: var(--color-primary); border-bottom: 1px solid var(--color-border); padding-bottom: 0.5rem;">
                        &#9742; Contact &amp; Front-Desk Hours
                    </h4>

                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
                        <div class="form-group">
                            <label for="hotelPhone" style="font-weight: 600;">Front Desk Phone</label>
                            <input type="tel" id="hotelPhone" name="phone" class="form-control"
                                   value="<c:out value='${hotel.phone}' />" placeholder="+1 (800) 555-0199">
                        </div>
                        <div class="form-group">
                            <label for="hotelEmail" style="font-weight: 600;">Reservations Email</label>
                            <input type="email" id="hotelEmail" name="email" class="form-control"
                                   value="<c:out value='${hotel.email}' />" placeholder="reservations@yourhotel.com">
                        </div>
                    </div>

                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.5rem;">
                        <div class="form-group">
                            <label for="checkInTime" style="font-weight: 600;">Standard Check-In Time</label>
                            <input type="text" id="checkInTime" name="checkInTime" class="form-control"
                                   value="<c:out value='${hotel.checkInTime}' />" placeholder="15:00 or 3:00 PM">
                        </div>
                        <div class="form-group">
                            <label for="checkOutTime" style="font-weight: 600;">Standard Check-Out Time</label>
                            <input type="text" id="checkOutTime" name="checkOutTime" class="form-control"
                                   value="<c:out value='${hotel.checkOutTime}' />" placeholder="11:00 or 11:00 AM">
                        </div>
                    </div>

                    <!-- Amenities Section -->
                    <h4 style="margin: 2rem 0 1rem; color: var(--color-primary); border-bottom: 1px solid var(--color-border); padding-bottom: 0.5rem;">
                        &#10024; Hotel Amenities &amp; Facilities
                    </h4>

                    <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 0.75rem; margin-bottom: 1rem; background: #fafafa; padding: 1.25rem; border-radius: 8px; border: 1px solid var(--color-border);">
                        <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                            <input type="checkbox" name="amenities" value="Free High-Speed Wi-Fi" ${hotel.amenities.contains('Free High-Speed Wi-Fi') ? 'checked' : ''}>
                            <span>Free High-Speed Wi-Fi</span>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                            <input type="checkbox" name="amenities" value="Swimming Pool" ${hotel.amenities.contains('Swimming Pool') || hotel.amenities.contains('Infinity Swimming Pool') || hotel.amenities.contains('Lagoon Pool') ? 'checked' : ''}>
                            <span>Swimming Pool / Cabana</span>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                            <input type="checkbox" name="amenities" value="Luxury Spa & Wellness" ${hotel.amenities.contains('Luxury Spa & Wellness') || hotel.amenities.contains('Spa') ? 'checked' : ''}>
                            <span>Luxury Spa &amp; Wellness</span>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                            <input type="checkbox" name="amenities" value="Fitness Center & Gym" ${hotel.amenities.contains('Fitness Center & Gym') ? 'checked' : ''}>
                            <span>Fitness Center &amp; Gym</span>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                            <input type="checkbox" name="amenities" value="Fine Dining Restaurant" ${hotel.amenities.contains('Fine Dining Restaurant') || hotel.amenities.contains('Restaurant') ? 'checked' : ''}>
                            <span>Fine Dining Restaurant</span>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                            <input type="checkbox" name="amenities" value="24/7 Room Service" ${hotel.amenities.contains('24/7 Room Service') ? 'checked' : ''}>
                            <span>24/7 Room Service</span>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                            <input type="checkbox" name="amenities" value="Valet Parking" ${hotel.amenities.contains('Valet Parking') || hotel.amenities.contains('Free Parking') ? 'checked' : ''}>
                            <span>Complimentary Valet Parking</span>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                            <input type="checkbox" name="amenities" value="Airport Shuttle" ${hotel.amenities.contains('Airport Shuttle') ? 'checked' : ''}>
                            <span>Airport Shuttle Service</span>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                            <input type="checkbox" name="amenities" value="Beachfront Access" ${hotel.amenities.contains('Beachfront Access') || hotel.amenities.contains('Oceanfront Access') || hotel.amenities.contains('Direct Beachfront Access') ? 'checked' : ''}>
                            <span>Direct Beachfront Access</span>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                            <input type="checkbox" name="amenities" value="Pet-Friendly" ${hotel.amenities.contains('Pet-Friendly') ? 'checked' : ''}>
                            <span>Pet-Friendly Suites</span>
                        </label>
                    </div>

                    <div class="form-group" style="margin-bottom: 2rem;">
                        <label for="customAmenities" style="font-weight: 600;">Additional Custom Amenities (comma-separated)</label>
                        <input type="text" id="customAmenities" name="customAmenities" class="form-control"
                               placeholder="e.g. Helipad, Electric Vehicle Charging, Rooftop Bar, Tennis Court">
                    </div>

                    <!-- Submit Buttons -->
                    <div style="display: flex; gap: 1rem; align-items: center; border-top: 1.5px solid var(--color-border); padding-top: 1.5rem;">
                        <button type="submit" class="btn btn-gold btn-lg" style="padding: 0.75rem 2rem;">
                            &#128190; Save Hotel Details
                        </button>
                        <a href="${pageContext.request.contextPath}/hotel/dashboard" class="btn btn-outline-primary">
                            Cancel
                        </a>
                    </div>
                </form>
            </div>

            <!-- Side Card: Preview & Quick Stats -->
            <div>
                <div class="admin-card" style="margin-bottom: 1.5rem;">
                    <h3 style="margin-bottom: 1rem; color: var(--color-primary); font-size: 1.15rem;">
                        &#128065; Public Listing Preview
                    </h3>

                    <div style="border: 1px solid var(--color-border); border-radius: 8px; overflow: hidden; background: #fff; box-shadow: var(--shadow-sm);">
                        <div style="height: 140px; background-color: #0b1a30; display:flex; align-items:center; justify-content:center; overflow:hidden;">
                            <img src="${pageContext.request.contextPath}/${hotel.imageUrl != null ? hotel.imageUrl : 'assets/images/hero-hotel.svg'}"
                                 alt="Hotel Image" style="width: 100%; height: 100%; object-fit: cover;">
                        </div>
                        <div style="padding: 1.25rem;">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                                <span style="color: #f59e0b; font-size: 1.1rem; letter-spacing: 2px;">
                                    <c:forEach begin="1" end="${hotel.starRating}">&#9733;</c:forEach>
                                </span>
                                <span class="badge" style="background-color: #e0f2fe; color: #0369a1; font-weight: 600; font-size: 0.75rem; padding: 0.2rem 0.5rem; border-radius: 4px;">
                                    ${hotel.active ? 'VERIFIED PARTNER' : 'INACTIVE'}
                                </span>
                            </div>
                            <h4 style="margin: 0 0 0.5rem; color: var(--color-primary); font-size: 1.2rem;">
                                <c:out value="${hotel.name}" />
                            </h4>
                            <p style="color: #64748b; font-size: 0.85rem; margin-bottom: 0.75rem;">
                                &#128205; <c:out value="${hotel.city}" />, <c:out value="${hotel.state}" />
                            </p>
                            <p style="color: #475569; font-size: 0.85rem; line-height: 1.4; margin-bottom: 1rem;">
                                <c:out value="${hotel.description}" />
                            </p>
                            <div style="border-top: 1px solid #e2e8f0; padding-top: 0.75rem; font-size: 0.8rem; color: #64748b;">
                                <div><strong>Check-in:</strong> ${hotel.checkInTime} &bull; <strong>Check-out:</strong> ${hotel.checkOutTime}</div>
                                <div><strong>Contact:</strong> ${hotel.phone}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="admin-card">
                    <h4 style="margin-bottom: 0.75rem; color: var(--color-primary); font-size: 1rem;">
                        &#128161; Partner Tips
                    </h4>
                    <ul style="padding-left: 1.25rem; font-size: 0.85rem; color: #64748b; line-height: 1.6;">
                        <li>Detailed descriptions increase guest reservation conversion by 30%.</li>
                        <li>Ensure front-desk contact phone and check-in times are kept up-to-date.</li>
                        <li>Any changes made here take effect immediately across all customer browsing views.</li>
                    </ul>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
