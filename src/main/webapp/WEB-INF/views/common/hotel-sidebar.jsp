<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<aside class="admin-sidebar">
    <div class="sidebar-brand">
        <img src="${pageContext.request.contextPath}/assets/images/logo.svg" alt="Hotel Emblem" style="width:28px; height:28px;">
        <span>Hotel Partner</span>
    </div>

    <ul class="sidebar-menu">
        <li>
            <a href="${pageContext.request.contextPath}/hotel/dashboard" class="${hotelSection == 'dashboard' ? 'active' : ''}">
                <span>&#128202;</span> Dashboard
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/hotel/profile" class="${hotelSection == 'profile' ? 'active' : ''}">
                <span>&#127976;</span> Edit Hotel Details
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/hotel/rooms" class="${hotelSection == 'rooms' ? 'active' : ''}">
                <span>&#128719;</span> Room Inventory
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/hotel/rooms/add" class="${hotelSection == 'add-room' ? 'active' : ''}">
                <span>&#10133;</span> Add New Room
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/hotel/bookings" class="${hotelSection == 'bookings' ? 'active' : ''}">
                <span>&#128221;</span> Guest Bookings
            </a>
        </li>
        <li style="margin-top: 1.5rem; border-top: 1px solid var(--color-border); padding-top: 1rem;">
            <a href="${pageContext.request.contextPath}/home" target="_blank">
                <span>&#127760;</span> View Public Website &rarr;
            </a>
        </li>
    </ul>

    <div class="sidebar-footer">
        <div>Logged in as:</div>
        <strong style="color:var(--color-primary);">${sessionScope.currentUser.fullName}</strong>
        <div style="color:var(--color-gold); font-size:0.75rem; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">
            <c:out value="${hotel.name != null ? hotel.name : 'Hotel Partner'}" />
        </div>
    </div>
</aside>
