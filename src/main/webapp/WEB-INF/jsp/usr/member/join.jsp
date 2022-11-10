<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="pageTitle" value="MEMBER JOIN" />
<%@ include file="../common/head.jspf" %>
<script>
let MemberJoin__submitDone = false;
let 중복체크 = false;
function MemberJoin__submit(form){
	if(MemberJoin__submitDone){
		alert('이미 처리중 입니다.');
		return;
	}
	if(중복체크 == false){
		alert('아이디를 다시 입력해주세요.');
		return;
	}
				
	form.loginId.value = form.loginId.value.trim();
	
	if(form.loginId.value.length == 0){
		alert('아이디를 입력해 주세요');
		return;
	}	
	
	form.loginPw.value = form.loginPw.value.trim();
	
	if(form.loginPw.value.length == 0){
		alert('비밀번호를 입력해 주세요');
		return;
	}
	
	form.loginPwConfirm.value = form.loginPwConfirm.value.trim();
	
	if(form.loginPwConfirm.value.length == 0){
		alert('비밀번호 확인을 입력해 주세요');
		return;
	}
	
	form.name.value = form.name.value.trim();
	
	if(form.name.value.length == 0){
		alert('이름을 입력해 주세요');
		return;
	}
	
	form.nickname.value = form.nickname.value.trim();
	
	if(form.nickname.value.length == 0){
		alert('닉네임을 입력해 주세요');
		return;
	}
	
	form.cellphoneNum.value = form.cellphoneNum.value.trim();
	
	if(form.cellphoneNum.value.length == 0){
		alert('전화번호를 입력해 주세요');
		return;
	}
	
	form.email.value = form.email.value.trim();
	if(form.email.value.length == 0){
		alert('이메일을 입력해 주세요');
		return;
	}	
	
	if(form.loginPw.value != form.loginPwConfirm.value ){
		alert('비밀번호를 다시 입력해 주세요');
		return;
	}
	MemberJoin__submitDone = true;
	form.submit();		
}

</script>	
	
	<section class="mt-8 text-xl">
		<div class="container mx-auto px-3">
			<form class="table-box-type-1" method="post" action="doJoin" onsubmit= "MemberJoin__submit(this); return false;">
				<table>
					<colgroup>
						<col width="200" />
					</colgroup>	
					<tbody>																				
						<tr>
							<td>아이디</td>
							<td>
								<input required="required" type="text" class="w-4/6 input input-bordered input-lg input_loginId" name="loginId" placeholder="아이디를 입력해주세요." />							
								<div id="loginIdDupCheck"></div>
							</td>						
						</tr>
						<tr>
							<td>비밀번호</td>
							<td><input required="required" type="text" class="w-4/6 input input-bordered input-lg" name="loginPw" placeholder="비밀번호를 입력해주세요." /></td>						
						</tr>
						<tr>
							<td>비밀번호 확인</td>
							<td><input required="required" type="text" class="w-4/6 input input-bordered input-lg" name="loginPwConfirm" placeholder="비밀번호 확인을 입력해주세요." /></td>						
						</tr>
						<tr>
							<td>이름</td>
							<td><input required="required" type="text" class="w-4/6 input input-bordered input-lg" name="name" placeholder="이름을 입력해주세요." /></td>						
						</tr>
						<tr>
							<td>닉네임</td>
							<td><input required="required" type="text" class="w-4/6 input input-bordered input-lg" name="nickname" placeholder="닉네임을 입력해주세요." /></td>						
						</tr>
						<tr>
							<td>전화번호</td>
							<td><input required="required" type="text" class="w-4/6 input input-bordered input-lg" name="cellphoneNum" placeholder="전화번호를 입력해주세요." /></td>						
						</tr>
						<tr>
							<td>이메일</td>
							<td><input required="required" type="text" class="w-4/6 input input-bordered input-lg" name="email" placeholder="이메일을 입력해주세요." /></td>						
						</tr>																								
					</tbody>								
				</table>
				
				<div class= "btns flex justify-end">
					<button class ="btn-text-link mx-4 btn btn-active btn-ghost" type="submit">회원가입</button>					
					<button class ="btn-text-link btn btn-active btn-ghost" type="button" onclick="history.back()">뒤로가기</button>
				</div>
			</form>
		</div>
	</section>
	
	<script>
	//로그인 아이디 중복체크
	 $('.input_loginId').focusout(function(){
	 	var id = $('input[name=loginId]').val().trim();
		
	 	if(id.length == 0){
	 		alert('아이디를 입력하세요');
	 		id.focus();
	 		return;
	 	}
		
	 	$.get('../member/loginIdDuplicateCheck', {
	 		loginId : id,
	 		ajaxMode : 'Y'
	 	}, function(data) {
	 		if(data.resultCode=='S-1'){
	 			$('#loginIdDupCheck').toggleClass();			
	 			$('#loginIdDupCheck').addClass('text-green-500');
	 			$('#loginIdDupCheck').empty().html(data.msg);
	 			중복체크 = true;
	 		}
			
	 		if(data.resultCode=='F-1'){
	 			$('#loginIdDupCheck').toggleClass();		
	 			$('#loginIdDupCheck').addClass('text-red-500');
	 			$('#loginIdDupCheck').empty().html(data.msg);
	 			중복체크 = false;
	 		}		
	 	}, 'json');			
	})
	</script>
<%@ include file="../common/foot.jspf" %>