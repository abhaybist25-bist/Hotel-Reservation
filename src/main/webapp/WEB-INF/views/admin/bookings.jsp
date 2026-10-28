<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Reservation Management" />
<c:set var="isAdminPage" value="true" />
<c:set var="adminSection" value="bookings" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="admin-layout">
    <jsp:include page="/WEB-INF/views/common/admin-sidebar.jsp" />

    <div class="admin-main">
        <div class="admin-header">
            <div>
                <div class="section-subtitle">Front Desk Operations</div>
                <h1>Reservation Directory &amp; Audit</h1>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <div class="admin-card">
            <div class="admin-card-header">
                <h2>All Bookings</h2>

                <!-- Toolbar with Status Filter & Search -->
                <form action="${pageContext.request.contextPath}/admin/bookings" method="get" class="admin-toolbar">
                    <select name="status" class="filter-select" onchange="this.form.submit()">
                        <option value="ALL" ${empty statusFilter || statusFilter == 'ALL' ? 'selected' : ''}>All Statuses</option>
                        <c:forEach var="st" items="${statuses}">
                            <option value="${st.name()}" ${statusFilter == st.name() ? 'selected' : ''}>${st.displayName}</option>
                        </c:forEach>
                    </select>

                    <div class="search-input-group">
                        <input type="text" name="q" class="form-control" style="width: 220px; padding: 0.4rem 0.75rem;" placeholder="Search booking #..." value="<c:out value='${keyword}' />">
                        <button type="submit" class="btn btn-primary btn-sm">Search</button>
                    </div>

                    <c:if test="${not empty statusFilter || not empty keyword}">
                        <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-outline-primary btn-sm">Reset</a>
                    </c:if>
                </form>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Confirmation #</th>
                            <th>Guest Details</th>
                            <th>Room #</th>
                            <th>Stay Period</th>
                            <th>Nights</th>
                            <th>Total (USD)</th>
                            <th>Status</th>
                            <th>Status Action</th>
                            <th>Inspect</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty bookings}">
                                <c:forEach var="b" items="${bookings}">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/admin/bookings/details?id=${b.idString}" style="font-family:monospace; font-weight:700;">
                                                ${b.bookingNumber}
                                            </a>
                                        </td>
                                        <td>
                                            <strong>${b.customerName}</strong><br>
                                            <span style="font-size:0.8rem; color:var(--color-text-muted);">${b.customerEmail} &bull; ${b.customerPhone}</span>
                                        </td>
                                        <td>
                                            <strong>Room #${b.roomNumber}</strong><br>
                                            <span style="font-size:0.75rem; color:var(--color-text-muted);">${b.roomType.displayName}</span>
                                        </td>
                                        <td>
                                            ${b.checkIn} &rarr; ${b.checkOut}
                                        </td>
                                        <td>${b.numberOfNights}</td>
                                        <td>
                                            <strong>$<fmt:formatNumber value="${b.totalAmount}" pattern="#,##0.00" /></strong>
                                        </td>
                                        <td>
                                            <span class="badge badge-${b.status.name().toLowerCase()}">${b.status.displayName}</span>
                                        </td>
                                        <td>
                                            <!-- Fast Status Transition Form -->
                                            <form action="${pageContext.request.contextPath}/admin/bookings/status" method="post" style="display:inline;">
                                                <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                                                <input type="hidden" name="bookingId" value="${b.idString}">

                                                <c:choose>
                                                    <c:when test="${b.status == 'CONFIRMED'}">
                                                        <input type="hidden" name="targetStatus" value="CHECKED_IN">
                                                        <button type="submit" class="btn btn-outline-primary btn-sm" title="Mark guest as checked in">Check In</button>
                                                    </c:when>
                                                    <c:when test="${b.status == 'CHECKED_IN'}">
                                                        <input type="hidden" name="targetStatus" value="CHECKED_OUT">
                                                        <button type="submit" class="btn btn-outline-gold btn-sm" title="Mark stay as completed">Check Out</button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="font-size:0.8rem; color:var(--color-text-muted);">Completed</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </form>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/admin/bookings/details?id=${b.idString}" class="btn btn-outline-primary btn-sm">
                                                Audit &rarr;
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="9" style="text-align:center; padding:3rem; color:var(--color-text-muted);">
                                        No reservations match the specified filter criteria.
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

