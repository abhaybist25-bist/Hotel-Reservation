<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<aside class="admin-sidebar">
    <div class="sidebar-brand">
        <img src="${pageContext.request.contextPath}/assets/images/logo.svg" alt="Hotel Emblem">
        <span>Hotel Partner</span>
    </div>

    <ul class="sidebar-menu">
        <li>
            <a href="${pageContext.request.contextPath}/hotel/dashboard" class="${hotelSection == 'dashboard' ? 'active' : ''}">
                <span>&#128202;</span>
                <span>Dashboard</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/hotel/profile" class="${hotelSection == 'profile' ? 'active' : ''}">
                <span>&#127976;</span>
                <span>Edit Hotel Details</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/hotel/rooms" class="${hotelSection == 'rooms' ? 'active' : ''}">
                <span>&#128719;</span>
                <span>Room Inventory</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/hotel/rooms/add" class="${hotelSection == 'add-room' ? 'active' : ''}">
                <span>&#10133;</span>
                <span>Add New Room</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/hotel/bookings" class="${hotelSection == 'bookings' ? 'active' : ''}">
                <span>&#128221;</span>
                <span>Guest Bookings</span>
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
        <div class="logged-label">Property Manager</div>
        <div class="logged-name" title="${sessionScope.currentUser.fullName}">
            ${sessionScope.currentUser.fullName != null ? sessionScope.currentUser.fullName : "Partner Manager"}
        </div>
        <div class="logged-role">&#127976; <c:out value="${hotel.name != null ? hotel.name : 'Partner Hotel'}" /></div>
    </div>
</aside>
