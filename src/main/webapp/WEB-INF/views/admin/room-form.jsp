<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="${isEdit ? 'Edit Room' : 'Add New Room'}" />
<c:set var="isAdminPage" value="true" />
<c:set var="adminSection" value="${isEdit ? 'rooms' : 'add-room'}" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="admin-layout">
    <jsp:include page="/WEB-INF/views/common/admin-sidebar.jsp" />

    <div class="admin-main">
        <div class="admin-header">
            <div>
                <div class="section-subtitle">Room Configuration</div>
                <h1>${isEdit ? 'Edit Room Details' : 'Add New Room to Inventory'}</h1>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin/rooms" class="btn btn-outline-primary">
                    &larr; Back to Room Inventory
                </a>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <div class="admin-card" style="max-width: 800px; margin: 0 auto; padding: 2rem;">
            <form action="${pageContext.request.contextPath}${isEdit ? '/admin/rooms/edit' : '/admin/rooms/add'}" method="post">
                <!-- CSRF Protection Token -->
                <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                <c:if test="${isEdit}">
                    <input type="hidden" name="id" value="${room.idString}">
                </c:if>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 1.25rem;">
                    <div class="form-group">
                        <label for="roomNumber">Room Number *</label>
                        <input type="text" id="roomNumber" name="roomNumber" class="form-control" value="<c:out value='${room.roomNumber}' />" ${isEdit ? 'readonly style="background:#F1F5F9;"' : 'required placeholder="e.g. 501"'} >
                        <c:if test="${isEdit}">
                            <span style="font-size:0.75rem; color:var(--color-text-muted);">Room number cannot be changed once established.</span>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="roomType">Category / Type *</label>
                        <select id="roomType" name="roomType" class="form-control" required>
                            <c:forEach var="type" items="${roomTypes}">
                                <option value="${type.name()}" ${room.roomType == type ? 'selected' : ''}>${type.displayName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="form-group" style="margin-bottom: 1.25rem;">
                    <label for="roomTitle">Public Room Title *</label>
                    <input type="text" id="roomTitle" name="title" class="form-control" value="<c:out value='${room.title}' />" required placeholder="e.g. Royal Oceanfront Penthouse">
                </div>

                <div class="form-group" style="margin-bottom: 1.25rem;">
                    <label for="roomDescription">Full Description *</label>
                    <textarea id="roomDescription" name="description" class="form-control" rows="4" required placeholder="Detailed room features, architectural views, furnishings..."><c:out value='${room.description}' /></textarea>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 1.25rem;">
                    <div class="form-group">
                        <label for="roomPrice">Nightly Rate (USD) *</label>
                        <input type="number" id="roomPrice" name="pricePerNight" class="form-control" step="0.01" min="1" value="${room.pricePerNight}" required placeholder="250.00">
                    </div>

                    <div class="form-group">
                        <label for="roomCapacity">Max Guest Capacity *</label>
                        <input type="number" id="roomCapacity" name="capacity" class="form-control" min="1" max="10" value="${room.capacity != null ? room.capacity : 2}" required>
                    </div>
                </div>

                <div class="form-group" style="margin-bottom: 1.25rem;">
                    <label for="roomAmenities">Amenities (Comma-separated)</label>
                    <input type="text" id="roomAmenities" name="amenities" class="form-control" value="<c:out value='${room.amenitiesCsv}' />" placeholder="King Bed, Ocean View, Wi-Fi, Balcony, Jacuzzi, Espresso Machine">
                    <span style="font-size:0.75rem; color:var(--color-text-muted);">Stored as a MongoDB BSON array of strings.</span>
                </div>

                <div class="form-group" style="margin-bottom: 1.5rem;">
                    <label for="roomImageUrl">Image Path / URL</label>
                    <input type="text" id="roomImageUrl" name="imageUrl" class="form-control" value="<c:out value='${room.imageUrl}' />" placeholder="assets/images/room-suite-1.svg">
                    <span style="font-size:0.75rem; color:var(--color-text-muted);">Relative asset path (e.g. assets/images/room-suite-1.svg) or web URL.</span>
                </div>

                <c:if test="${isEdit}">
                    <div style="margin-bottom: 1.5rem; display: flex; align-items: center; gap: 0.5rem;">
                        <input type="checkbox" id="roomActive" name="active" ${room.active ? 'checked' : ''} style="width: 18px; height: 18px;">
                        <label for="roomActive" style="font-weight: 600; cursor: pointer;">Active for guest booking</label>
                    </div>
                </c:if>

                <div style="display: flex; gap: 1rem; border-top: 1px solid var(--color-border); padding-top: 1.5rem;">
                    <button type="submit" class="btn btn-gold btn-lg">
                        ${isEdit ? 'Save Changes' : 'Create Room'} &rarr;
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/rooms" class="btn btn-outline-primary">
                        Cancel
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

