<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<aside class="admin-sidebar">
    <div class="sidebar-brand">
        <img src="${pageContext.request.contextPath}/assets/images/logo.svg" alt="Admin Emblem">
        <span>Staff Portal</span>
    </div>

    <ul class="sidebar-menu">
        <li>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="${adminSection == 'dashboard' ? 'active' : ''}">
                <span>&#128202;</span>
                <span>Dashboard</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/rooms" class="${adminSection == 'rooms' ? 'active' : ''}">
                <span>&#128719;</span>
                <span>Room Inventory</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/rooms/add" class="${adminSection == 'add-room' ? 'active' : ''}">
                <span>&#10133;</span>
                <span>Add New Room</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/bookings" class="${adminSection == 'bookings' ? 'active' : ''}">
                <span>&#128221;</span>
                <span>Reservations</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/customers" class="${adminSection == 'customers' ? 'active' : ''}">
                <span>&#128101;</span>
                <span>Customer Directory</span>
            </a>
        </li>
        <li style="margin-top: 1rem; border-top: 1px solid var(--admin-border-light); padding-top: 0.75rem;">
            <a href="${pageContext.request.contextPath}/home" target="_blank">
                <span>&#127760;</span>
                <span>View Public Site &rarr;</span>
            </a>
        </li>
    </ul>

    <div class="sidebar-footer">
        <div class="logged-label">Logged In As</div>
        <div class="logged-name" title="${sessionScope.currentUser.fullName}">
            ${sessionScope.currentUser.fullName != null ? sessionScope.currentUser.fullName : "Administrator"}
        </div>
        <div class="logged-role">&#128737; System Administrator</div>
    </div>
</aside>
