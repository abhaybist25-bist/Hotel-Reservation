<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Room Inventory - ${hotel.name}" />
<c:set var="isAdminPage" value="true" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="admin-layout">
    <jsp:include page="/WEB-INF/views/common/hotel-sidebar.jsp" />

    <div class="admin-main">
        <div class="admin-header">
            <div>
                <div class="section-subtitle">${hotel.name}</div>
                <h1>Room Inventory Management</h1>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/hotel/rooms/add" class="btn btn-gold">
                    &#10133; Add New Room
                </a>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <div class="admin-card">
            <div class="admin-card-header">
                <h2>Hotel Rooms (${rooms != null ? rooms.size() : 0})</h2>
                <div style="font-size: 0.85rem; color: var(--color-text-muted);">
                    Managed exclusively for ${hotel.name}
                </div>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Room #</th>
                            <th>Image</th>
                            <th>Category</th>
                            <th>Title</th>
                            <th>Capacity</th>
                            <th>Price / Night</th>
                            <th>Status</th>
                            <th>Operations</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty rooms}">
                                <c:forEach var="room" items="${rooms}">
                                    <tr>
                                        <td><strong>#${room.roomNumber}</strong></td>
                                        <td>
                                            <img src="${pageContext.request.contextPath}/${room.imageUrl}" alt="${room.title}" style="width: 50px; height: 35px; object-fit: cover; border-radius: var(--radius-sm);">
                                        </td>
                                        <td><span class="badge badge-confirmed">${room.roomType.displayName}</span></td>
                                        <td>
                                            <strong>${room.title}</strong><br>
                                            <span style="font-size:0.8rem; color:var(--color-text-muted);">${room.amenitiesCsv}</span>
                                        </td>
                                        <td>${room.capacity} Guests</td>
                                        <td>
                                            <strong>$<fmt:formatNumber value="${room.pricePerNight}" pattern="#,##0.00" /></strong>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${room.active}">
                                                    <span class="badge badge-active">&#10004; Active</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-inactive">&#10006; Inactive</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div style="display:flex; gap:0.4rem; align-items:center;">
                                                <!-- Edit -->
                                                <a href="${pageContext.request.contextPath}/hotel/rooms/edit?id=${room.idString}" class="btn btn-outline-primary btn-sm">
                                                    Edit
                                                </a>

                                                <!-- Toggle Active -->
                                                <form action="${pageContext.request.contextPath}/hotel/rooms/toggle" method="post" style="display:inline;">
                                                    <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                                                    <input type="hidden" name="id" value="${room.idString}">
                                                    <button type="submit" class="btn btn-outline-gold btn-sm">
                                                        ${room.active ? 'Deactivate' : 'Activate'}
                                                    </button>
                                                </form>

                                                <!-- Delete Safely -->
                                                <form action="${pageContext.request.contextPath}/hotel/rooms/delete" method="post" onsubmit="return confirm('Permanently delete Room ${room.roomNumber}? This cannot be undone.');" style="display:inline;">
                                                    <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                                                    <input type="hidden" name="id" value="${room.idString}">
                                                    <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="8" style="text-align:center; padding:3rem; color:var(--color-text-muted);">
                                        No rooms configured yet for ${hotel.name}. Use the "Add New Room" button to create your first room.
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
