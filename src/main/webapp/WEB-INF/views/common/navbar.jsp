<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header class="site-header">
    <div class="container">
        <nav class="navbar">
            <a href="${pageContext.request.contextPath}/home" class="brand">
                <img src="${pageContext.request.contextPath}/assets/images/logo.svg" alt="Hotel Emblem" class="brand-icon">
                <span>The Grand <span class="accent">Azure</span></span>
            </a>

            <button class="nav-toggle" id="navToggle" aria-label="Toggle navigation menu">
                &#9776;
            </button>

            <ul class="nav-links" id="navLinks">
                <li><a href="${pageContext.request.contextPath}/home" class="${currentNav == 'home' ? 'active' : ''}">Home</a></li>
                <li><a href="${pageContext.request.contextPath}/rooms" class="${currentNav == 'rooms' ? 'active' : ''}">Rooms &amp; Suites</a></li>

                <c:choose>
                    <c:when test="${not empty sessionScope.currentUser}">
                        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                            <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-gold btn-sm">Admin Portal</a></li>
                        </c:if>
                        <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}">
                            <li><a href="${pageContext.request.contextPath}/customer/dashboard" class="${currentNav == 'dashboard' ? 'active' : ''}">Dashboard</a></li>
                            <li><a href="${pageContext.request.contextPath}/customer/bookings" class="${currentNav == 'bookings' ? 'active' : ''}">My Reservations</a></li>
                        </c:if>
                        <li>
                            <div class="nav-user">
                                <a href="${pageContext.request.contextPath}/customer/profile" class="btn btn-outline-gold btn-sm">
                                    <span>&#128100; ${sessionScope.currentUser.fullName}</span>
                                </a>
                                <form action="${pageContext.request.contextPath}/logout" method="post" style="display:inline;">
                                    <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                                    <button type="submit" class="btn btn-outline-primary btn-sm">Sign Out</button>
                                </form>
                            </div>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${pageContext.request.contextPath}/login" class="${currentNav == 'login' ? 'active' : ''}">Sign In</a></li>
                        <li><a href="${pageContext.request.contextPath}/register" class="btn btn-gold btn-sm">Register</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </nav>
    </div>
</header>
