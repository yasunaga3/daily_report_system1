<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/WEB-INF/views/layout/app.jsp">

	<c:param name="content">
		<%-------------------- フラッシュの表示 -------------------%>
		<%-- エラー表示(パスワード) --%>
		<c:if test="${hasError}">
			<div id="flush_error">社員番号かパスワードが間違っています。</div>
		</c:if>
		<%-- 成功表示(ログアウト) --%>
		<c:if test="${flush != null}">
			<div id="flush_success"><c:out value="${flush}" /></div>
		</c:if>

		<%-------------------- ログインフォームの表示 -------------------%>
		<h2>ログイン</h2>
		<form method="post" action="<c:url value='/login' />">
			<label for="code">社員番号</label><br />
			<input type="text" name="code" value="${employee.code}" /><br /><br />

			<label for="name">パスワード</label><br />
			<input type="password" name="password" /><br /><br />

			<input type="hidden" name="_token" value="${_token}" />
			<button type="submit">ログイン</button>
		</form>
	</c:param>

</c:import>