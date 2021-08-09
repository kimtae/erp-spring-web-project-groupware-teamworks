<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file = "/WEB-INF/views/header/header.jsp" %>
<!DOCTYPE html>
<html>
<head>
<%@include file = "/WEB-INF/views/header/headerHead.jsp" %>
<link rel="styleSheet" href="${pageContext.request.contextPath}/community/css/community_main.css">
<script src="${pageContext.request.contextPath}/community/js/community_main.js"></script>
<title>Insert title here</title>
</head>
<script type="text/javascript">
$(document).ready(function() {
var count = 0;
//스크롤 바닥 감지
window.onscroll = function(e) {
  //추가되는 임시 콘텐츠
  //window height + window scrollY 값이 document height보다 클 경우,
  if((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
  	//실행할 로직 (콘텐츠 추가)
      count++;
      var addContent = '<div class="block"><p>'+ count +'번째로 추가된 콘텐츠</p></div>';
      //article에 추가되는 콘텐츠를 append
      $('article').append(addContent);
  }
}
});
</script>
<body>
<%@include file = "/WEB-INF/views/header/headerBody.jsp" %>
		<div id="side">
		<button type="button" id="btn1"
			onclick="location.href='${pageContext.request.contextPath}/community/write_Community'">커뮤니티 만들기</button>
		<div id="side_text">
			<a id="button1" class="button1"><span id="span_hover"><img
					alt="image"
					src="${pageContext.request.contextPath}/board/img/right.png"
					style="width: 16px; height: 12px; transition: 0.5s;" id="img1"></span>
				<span id="span_none"> <img alt="image"
					src="${pageContext.request.contextPath}/board/img/white.png"
					style="width: 16px; height: 12px;" id="img1">
			</span>커뮤니티게시판</a>
			<ol id="scroll" style="display: none; list-style: none;">
				<c:forEach var="as" items="${boardListOfCommunity }">
					<li class="li">
						<c:if test="${as.is_joined != '0'}">
							<a href="${pageContext.request.contextPath}/community/sideboard_list?bd_num=${as.bd_num}">${as.bd_name}</a>
						</c:if>
						
						<c:if test="${as.is_joined == '0'}">
						    <a href="${pageContext.request.contextPath}/community/sideboard_list?bd_num=${as.bd_num}" style="color: #BDBDBD;">${as.bd_name}</a>     
							<button  style="padding: 4px 4px 4px;color: #fff; font-weight: normal; border:none; font-size: 11px; line-height: 12px; background: #aeb2ba; letter-spacing: -1px;" 
							           id="boardSet2_${as.bd_num}">가입하기</button>
						</c:if>
					</li>
				</c:forEach>

			</ol>
		</div>
	</div>
<div id="content">
<div id="main_Text">
<h1 class="pageTitle" style="margin-left: 60px; margin-top:30px; font-weight: normal;font-size: 20px; position: fixed;">커뮤니티 홈</h1>
</div>
<div class="block">

			<div id="board_table">	
			
			<input type="hidden" formmethod="post" formaction="${pageContext.request.contextPath}/community/update?p_num=${view.p_num}&m_id=${view.m_id}&loginId=${view.loginId}">
						<sec:csrfInput />
					<c:forEach var="ps" items="${listPost}">
					<div id="community_list">
				<ul>
				<li>${community.bd_name} ${ps.p_view} </li>
				<li ><a style="font-size: 20px;" href='${pageContext.request.contextPath}/community/view?p_num=${ps.p_num}'>${ps.p_name}</a></li>
			
				<li>${ps.m_id} &nbsp; &nbsp; &nbsp;<fmt:formatDate value="${ps.p_regdate}" pattern="yyyy-MM-dd HH:mm:ss"/><button style="background-color: transparent; border: none;" type="button" id="buttonRecommand" onclick="location.href='${pageContext.request.contextPath}/board/recommend?p_num=${view.p_num}'">
			<img alt="image" src="${pageContext.request.contextPath}/board/img/heart.png"
											style="width: 30px; height: 30px; margin-left: 800px;"></button>
			<c:if test="${statusOfLike == 0}">
										<img alt="image"
										src="${pageContext.request.contextPath}/board/img/heart.png"
											style="width: 30px; height: 30px;">
									</c:if>
									<c:if test="${statusOfLike == 1}">
										<img alt="image"
											src="${pageContext.request.contextPath}/board/img/checkedheart.png"
											style="width: 30px; height: 30px;">
									</c:if></li>
		
								
				</ul>	
				</div>
					</c:forEach>	
			</div>
			</div>
 </div> <!--무한스크롤끝 -->
<!-- 	</div> -->
<div id="myModal" class="modal">

		<!-- Modal content -->
		<div class="modal-content">
			<p>
				<span>게시판 관리 <img
					src="https://img.icons8.com/fluent-systems-regular/48/000000/x.png"
					style="width: 35px; height: 25px; float: right; cursor: pointer;"
					onclick="close_pop2();" id="x_icon" />
				</span>
			</p>
			<br>
			<p style="text-align: center; line-height: 1.5;"></p>
			<form id="modal_form" name="modal_form">
			<sec:csrfInput/>
				<table>
					<tr>
						<td>게시판선택</td>
						<td colspan="3">
							<select name="bd_num">
								<c:forEach var="boardListOfCommunity" items="${boardListOfCommunity}">
									  <option value="${boardListOfCommunity.bd_num}">${boardListOfCommunity.bd_name}</option>
								</c:forEach>							
							</select>
						</td>
					<tr>
						<td></td>
					    <td><input type="button" value="그룹지정" onclick="javascript:boardGroup()"></td>
						<td><input type="submit" value="그룹삭제" formaction="${pageContext.request.contextPath}/board/groupDelete"></td>
						<td><input type="button" value="취소" id="close_btn"></td>
				</table>
			</form>
		</div>
	</div>
	<c:forEach var="as" items="${boardListOfCommunity }">
	<div id="myModal2_${as.bd_num}" class="modal">
	
		<!-- Modal content -->
		<div class="modal-content">
		
			<p>
				<span style="font-size: 20px; font-weight: bold;">가입을 하시겠습니까? <img
					src="https://img.icons8.com/fluent-systems-regular/48/000000/x.png"
					style="width: 35px; height: 25px; float: right; cursor: pointer;"
					onclick="close_pop2();" id="x_icon" />
				</span>
			</p>
			<br>
			<p style="text-align: center; line-height: 1.5;"></p>
			<form id="modal_form" name="modal_form" name="form_insert" action="${pageContext.request.contextPath}/community/CommunitySign?bd_num=${as.bd_num}" method="post" enctype="multipart/form-data">
	
			<sec:csrfInput/>
				<table>
		          <tr> 
					<td id="btnNotice"><button type="submit" onclick="">가입</button>	</td></tr>	
				</table>
			</form>
		</div>
	</div>
	<script>
	//커뮤니티 가입 모달
		$(document).ready(function() {
		// 가입 클릭 시 모달창 열기
		$("#boardSet2_${as.bd_num}").on("click", function () {
			$('#myModal2_${as.bd_num}').show();
		});
		//모달창 Close 기능
			$("#close_btn2").unbind('click').on('click', function() {
				$('#myModal2_${as.bd_num}').hide();
			});
		//모달창 Close 기능
			$("#x_icon").unbind('click').on('click', function() {
				$('#myModal2_${as.bd_num}').hide();
			});
		});
	</script>
	</c:forEach>
	
<%@include file = "/WEB-INF/views/header/headerFooter.jsp" %>
</body>
</html>