<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file = "/WEB-INF/views/header/header.jsp" %>
<!DOCTYPE html>
<html>
<head>
<%@include file = "/WEB-INF/views/header/headerHead.jsp" %>
<!-- 서머노트를 위해 추가해야할 부분 -->
<script
	src="${pageContext.request.contextPath}/board/js/summernote/summernote-lite.js"></script>
<script
	src="${pageContext.request.contextPath}/board/js/summernote/lang/summernote-ko-KR.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/board/js/summernote/summernote-lite.css">
<script defer src="${pageContext.request.contextPath}/board/js/write.js"></script>
<link rel="styleSheet" href="${pageContext.request.contextPath}/board/css/write.css">

<title>Insert title here</title>
</head>
<body>
<%@include file="/WEB-INF/views/header/headerBody.jsp"%>
		
	<div id="side">
		<button type="button" id="btn1"
			onclick="location.href='${pageContext.request.contextPath}/board/write'">글쓰기</button>
		<div>

			<a id="button1" class="button1"><span id="span_hover"><img
					alt="image"
					src="${pageContext.request.contextPath}/board/img/right.png"
					style="width: 16px; height: 12px; transition: 0.5s;" id="img1"></span>
				<span id="span_none"> <img alt="image"
					src="${pageContext.request.contextPath}/board/img/white.png"
					style="width: 16px; height: 12px;" id="img1">
			</span>전사게시판</a>
			<ol id="scroll" style="display: none; list-style: none;">
				<c:forEach var="as" items="${boardListOfAll }">
					<li class="li"><a
						href="${pageContext.request.contextPath}/board/sideboard_list?bd_num=${as.bd_num}">${as.bd_name}</a></li>
				</c:forEach>

			</ol>
			<ol id="scroll" style="display: none; list-style: none;">
				<c:forEach var="bs" items="${boardListOfAll }">
					<li class="li">${bs.bd_name}</li>
				</c:forEach>

			</ol>
		</div>
		<div>
			<a id="button2" class="button1"><span id="span_hover"><img
					alt="image"
					src="${pageContext.request.contextPath}/board/img/right.png"
					style="width: 16px; height: 12px; transition: 0.5s;" id="img2"></span>
				<span id="span_none"><img alt="image"
					src="${pageContext.request.contextPath}/board/img/white.png"
					style="width: 16px; height: 12px;" id="img2"></span>부서게시판</a>
			<ol id="scroll" style="display: none; list-style: none;">
				<c:forEach var="bs" items="${boardListOfDept }">
					<li class="li"><a
						href="${pageContext.request.contextPath}/board/sideboard_list?bd_num=${bs.bd_num}">${bs.bd_name}</a></li>
				</c:forEach>

			</ol>
		</div>
	</div>
	<div id="content">
			<form name="form_insert" action="${pageContext.request.contextPath}/board/insert" method="post" enctype="multipart/form-data">
				<sec:csrfInput/>
				<section id="point">
					<article class="pt pt1">
					<div id="Top">
							<span id="td_title">글쓰기</span>
							</div>
								<div id="write_Select">
						<span style="color: #888; font: bold 18px verdana; margin-right: 20px;">To.</span>
							<select id="write_Selectbox" name="bd_num">
								<c:forEach var="boardListOfAll" items="${boardListOfAll}">
									  <option value="${boardListOfAll.bd_num}">${boardListOfAll.bd_name}</option>
								</c:forEach>
									<c:forEach var="boardListOfDept" items="${boardListOfDept}">
									  <option value="${boardListOfDept.bd_num}">${boardListOfDept.bd_name}</option>
								</c:forEach>
							</select>
					</div>	
								<table class="pt_tb">
								<tr>
							<th style="color: #888;">제목</th>
								<td class="td_subject">
								<input id="subject" type="text" name="p_name" placeholder="글 제목" class="form_box">
								</td>
							</tr>
						<tr>
							<td style="padding-left: -10px;" colspan="100"><textarea id="summernote" class="summernote"
									name="p_content" placeholder="글 내용" rows="15" style="width: 900px;">${view.p_content}</textarea>

								<script type="text/javascript">

								</script>
								</td>
						</tr>
						</table>
					</article>
				</section>
				<input type="hidden" name="p_type" value="2">
				<button type="submit" id="btnUpdete"onclick="">등록</button>		
				<button type="submit" id="btnNotice"onclick="form_insert.p_type.value = 1;">공지로등록</button>
			</form>
		
	</div>
<%@include file = "/WEB-INF/views/header/headerFooter.jsp" %>
</body>
</html>