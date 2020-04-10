$(document).ready(function(){
	//로딩화면 스타트
	

});
$('#signup_btn').off().on('click',function(){
	
		isLoading.start();
		
        let people = {};
    	people.email=$('#email').val();
    	people.name=$('#name').val();
    	people.password=$('#password').val();
    	people.sex=$('#sex').val();
    	people.birth=$('#birth').val();

    	let formData = new FormData();
    	formData.append("file", $('#people_image_file')[0].files[0]);
    	formData.append('signUpRequest', new Blob([JSON.stringify(people)], {
    	        type: "application/json; charset=UTF-8"
    	    }));
    	$.ajax({
    		url:'/signup',
    		type:'POST',
    		 enctype: 'multipart/form-data',
    	        processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
    	        contentType: false,
    	        dataType:'json',
    		mimeType:"multipart/form-data",
    	    data: formData,
    		success:function(data){
    			if(data.code==1){
    				alert(data.message);
    				location.href='/signinView';
    			}else{
    				alert(data.message);
    			}
    		},
    		error:function(){

    		},
    		complete : function() {
    			isLoading.stop();
    	    }


    	});
    	  //isLoading.stop();
	
});