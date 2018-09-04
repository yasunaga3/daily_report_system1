<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="../layout/app.jsp">
    <c:param name="content">
        <h2>日報管理システムへようこそ</h2>
    </c:param>
</c:import>

   	<%---------- テスト用リンク(要削除) ----------%>
   	<%---------- 一覧画面へのリンク ----------%>
   	<p><a href="<c:url value='/employees/index' />">一覧画面へ</a></p>