<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Customer Directory" />
<c:set var="isAdminPage" value="true" />
<c:set var="adminSection" value="customers" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="admin-layout">
    <jsp:include page="/WEB-INF/views/common/admin-sidebar.jsp" />

    <div class="admin-main">
        <div class="admin-header">
            <div>
                <div class="section-subtitle">Guest Records</div>
                <h1>Customer Directory</h1>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/common/messages.jsp" />

        <div class="admin-card">
            <div class="admin-card-header">
                <h2>Registered Customers (${customers != null ? customers.size() : 0})</h2>

                <form action="${pageContext.request.contextPath}/admin/customers" method="get" class="admin-toolbar">
                    <div class="search-input-group">
                        <input type="text" name="q" class="form-control" style="width: 260px; padding: 0.4rem 0.75rem;" placeholder="Search by name, email or phone..." value="<c:out value='${keyword}' />">
                        <button type="submit" class="btn btn-primary btn-sm">Search</button>
                    </div>
                    <c:if test="${not empty keyword}">
                        <a href="${pageContext.request.contextPath}/admin/customers" class="btn btn-outline-primary btn-sm">Reset</a>
                    </c:if>
                </form>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Guest Name</th>
                            <th>Email Address</th>
                            <th>Phone</th>
                            <th>Role</th>
                            <th>Registered Date</th>
                            <th>Account Security</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty customers}">
                                <c:forEach var="c" items="${customers}">
                                    <tr>
                                        <td>
                                            <div style="display: flex; align-items: center; gap: 0.75rem;">
                                                <div style="width: 34px; height: 34px; background: var(--color-primary-light); color: #FFF; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 0.85rem; font-weight: 700;">
                                                    ${c.fullName.substring(0, 1).toUpperCase()}
                                                </div>
                                                <div>
                                                    <strong>${c.fullName}</strong>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <a href="mailto:${c.email}" style="color: var(--color-primary);">${c.email}</a>
                                        </td>
                                        <td>${c.phone}</td>
                                        <td>
                                            <span class="badge badge-confirmed">${c.role.displayName}</span>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${c.createdAt}" pattern="MMM dd, yyyy" />
                                        </td>
                                        <td>
                                            <span style="font-size: 0.8rem; color: var(--color-success);">&#128274; BCrypt Encrypted</span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6" style="text-align: center; padding: 3rem; color: var(--color-text-muted);">
                                        No customer records match your search criteria.
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

