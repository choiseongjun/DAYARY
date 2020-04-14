$(function(){
	$.ajax({
	      url:'/adminPeoplelist',
	        type:'get',
	        processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
	        contentType: false,
	        dataType:'json',
	        cache: false,
	        //data: formData,
	        success:function(data) {
	        	var m=data;
	        	console.log(m)
	        	var html1="<th>메뉴명</th><th>메뉴URL</th><th>메뉴내용</th><th>메뉴순서</th>"
	        	$("#thead").html(html1);
        		var html=""
	        	for(var i=0;i<m.length;i++){
	        		html+="<tr>"
	        		html+="<td>"+m[i].menu_name+"</td>";
	        		html+="<td>"+m[i].menu_url+"</td>";
	        		html+="<td>"+m[i].menu_memo+"</td>";
	        		html+="<td>"+m[i].menu_order+"</td>";
	        		html+="</tr>"
	        	}
	        		console.log(html)
	        	$("#tbody").html(html);
	       }
	   });
})