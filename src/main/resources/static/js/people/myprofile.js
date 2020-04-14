
function getNoti(){
	var notiList =document.getElementById("notiList");
	var pageNum=parseInt(notiList.childElementCount/9)+1;
	
	
	$.ajax({
	      url:'/getMyNotiList/'+pageNum,
	        contentType: "application/json; charset=utf-8",
	        processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
	        success:function(data){
		        	var notilist=data.notiList;
		        	console.log(notilist);
		        	if(notilist.length>0){
		        		$("#more").remove();
		        		var html=""
			        		for(var i in notilist){
			    				html += '<tr>'
		    					html += 	'<td>'
			    				html += 	'<span class="float-right font-weight-bold">'+notilist[i].createDate+'</span>'
								html += 	'</td>'
			    				html += 	'<td>'
			    				html += 	'<a href="moimlistView/moimdetailView/'+notilist[i].moim.id+'">'+notilist[i].memo+'</a>'
								html += 	'</td>'
				   				html += '</tr>'
				   					if(i==notilist.length-1){
				   						html+="<tr id='more'><a onclick='getNoti()'>더보기</a></tr>";
				   					}
			        		}
		        		
		        		$("#notiList").append(html);
		        	}
		        
		        	
	        	
	        }, error:function(e){
				alert(e)
	        }
	    });
}
function myMoimList(){
	$.ajax({
	      url:'/myprofilemoimView',
	        contentType: "application/json; charset=utf-8",
	        processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
	        success:function(data){
	        	console.log(data);
	        	if(data.code==1){
	        		var joinedMoimListNo=data.joinedMoimListNo;
	        		var joinedMoimList=data.joinedMoimList;
	        		var html=""
        			for(var i in joinedMoimList){
        				html += '<tr>'
        				html += 	'<td>'
        				html += 	'<a href="moimlistView/moimdetailView/'+joinedMoimListNo[i]+'">'+joinedMoimList[i]+""+'</a>'
    					html += 	'</td>'
		   				html += '</tr>'
        			}
	        		$("#list").html(html);
	        	}
	        }, error:function(e){
				alert(e)
	        }
	    });
	$.ajax({
	      url:'/myprofilemademoimView',
	        contentType: "application/json; charset=utf-8",
	        processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
	        success:function(data){
	        	if(data.code==1){
	        		var madeMoimListNo=data.madeMoimListNo;
	        		var madeMoimListtitle=data.madeMoimListtitle;
	        		var html=""
      			for(var i in madeMoimListNo){
      				html += '<tbody>'
      				html += '<tr>'
      				html += 	'<td>'
      				html += 	'<a href="moimlistView/moimdetailView/'+madeMoimListNo[i]+'">'+madeMoimListtitle[i]+""+'</a>'
  					html += 	'</td>'
		   				html += '</tr>'
      				html += '</tbody>'
      			}
	        		$("#mademoimlist").html(html);
	        	}
	        }, error:function(e){
				alert(e)
	        }
	    });
}


$(document).ready(function(){
	console.log("여기")
	var peopleId = $('#peopleId').attr("data-peopleId");
	
	$.ajax({
		type : 'GET',
		headers : {
			Accept : "application/json; charset=utf-8",
			"Content-Type" : "application/json; charset=utf-8"
		},
		url : '/selectMyTodoProgress/'+peopleId,
		success : function(result) {
			google.charts.load('current', {
				'packages' : [ 'corechart' ]
			});
			google.charts.setOnLoadCallback(function() {
				drawChartEnd(result);
			});
		}
	});
	getNotiCount();
	
	$("#people_delete_btn").click(function(){
		  var peopleId = $('#peopleId').attr("data-peopleId");
		  
			$.ajax({
		        url:'/myprofileView/deletePeople/'+peopleId,
		        type:'DELETE',
				contentType: 'application/json; charset=UTF-8',
		        dataType:'json',
		        success:function(data){
					if(data.code==1){
						alert(data.message);
						location.href='/logout';
					}else{
						alert(data.message)
					}
				},
				error:function(xhr,error){
					
				}
		    });
	  });
	});


function toMoim(){
	 var joinedMoimId = $('#joinedMoimId').attr("data-joinedMoimId");
	console.log('onclick'+joinedMoimId);
}
$('[name="joinedMoimId"]').on('click', function () {
	
//	console.log($(this).val());
	
});
function drawChartEnd(result) {
	var chartData=result.StachartList;
	
	console.log(result)
	
	var data = new google.visualization.DataTable();
	data.addColumn('string', '이름');
	data.addColumn('number', '완료한 갯수');
	data.addColumn('number', '해야할 갯수');
	var dataArray = [];
	$.each(result, function(i, obj) {
		dataArray.push([ obj.title, obj.no, obj.no2 ]);
	});
	for(var i=0;i<chartData.length;i++){
		dataArray.push([ chartData[i].title, chartData[i].no1, chartData[i].no2 ]);
	}
	data.addRows(dataArray);

	var barchart_options = {
		title : '나의 계획 진척도',
		width : 400,
		height : 300,
		legend : 'none'
	};
	var barchart = new google.visualization.ColumnChart(document
			.getElementById('barchart_div'));
	barchart.draw(data, barchart_options);
}