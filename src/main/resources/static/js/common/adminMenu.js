$(function(){
	$.ajax({
	      url:'/Menulist',
	        type:'get',
	        processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
	        contentType: false,
	        dataType:'json',
	        cache: false,
	        //data: formData,
	        success:function(data) {
	        	var m=data;
	        	console.log(m)
	        	var html1="<th>메뉴명</th><th>메뉴URL</th>"
	        	$("#thead").html(html1);
        		var html=""
	        	for(var i=0;i<m.length;i++){
	        		html+="<td>"+m[i].menu_name+"</td>";
	        		html+="<td>"+m[i].menu_url+"</td>";
	        	}
	        		console.log(html)
	        	$("#tbody").html(html);
	       }
	   });
})