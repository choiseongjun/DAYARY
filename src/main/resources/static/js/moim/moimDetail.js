$(document).ready(function(){
	connectStompforMoim(); 
});
function meetupPeoplejoinCheck() {
  console.log("페이지 로딩 완료");

}
function connectStompforMoim() {
	socket.connect({}, function () {
        console.log("Connected MoimNoti");
        socket.subscribe('/topic/moim', onMessageReceivedMoim);
        
    });

}
function onMessageReceivedMoim(payload) {
	var message = JSON.parse(payload.body);
	$("#noti").append('<span>'+message.memo+'</span><span>'+message.createDate+'</span><br>');
}
$('[name="grantpeople_btn"]').on('click', function () {//가입승인 대기중인사람 승인하기 by choiseongjun 2019-10-07
		 var moimNo = $('#moimNo').attr("data-moimNo");
	     let tempdata = {};
	     tempdata.no1= $(this).val();
	     tempdata.no2= $('#moimNo').attr("data-moimNo");
	    
	     console.log($(this).val())
	     

	    $.ajax({
	          url : '/moimParticipant/moimgrantjoinPeople', 
	          type : "post",   
	          contentType: 'application/json; charset=UTF-8',
	  		  dataType:'json',
	  		  data: JSON.stringify(tempdata),
	 		  success:function(data){
	 			if(data.code==1){
	 				var moimPeopleList=moimPeopleList.join(',');
	 				if(!isStomp && socket.readyState!==1) return;
             		let peopleId=$('#peopleId').attr("data");
	 				let moimNo=$('#moimNo').attr("data-moimNo");
	 				let peopleEmail=$('#email').attr("data-email");
	 				if(isStomp){
	 					socket.send('/noti/joinNoti',{},JSON.stringify({moimNo:moimNo, userName: userName, moimTitle: moimTitle,moimPeopleList:moimPeopleList}));
	          			socket.send('/moim/joinNoti',{},JSON.stringify({moimNo:moimNo, userName: userName, moimTitle: moimTitle}));
	 					alert(data.message);
	 				  location.href='/moimlistView/moimdetailView/'+moimNo;
	 	       		}
	 			}else{
	 				alert(data.message);
	 			}
	 		},
	           error:function(e){
	        	   signup_btn
	           }
	       });
	});
$('[name="banpeople_btn"]').on('click', function () {//회원 강퇴하기 by choiseongjun 2019-10-01
   var moimNo = $('#moimNo').attr("data-moimNo");

     let tempdata = {};
     tempdata.no1= $(this).val();
     tempdata.no2= $('#moimNo').attr("data-moimNo");
     

    $.ajax({
          url : '/moimParticipant/banjoinedPeople', 
          type : "post",   
          contentType: 'application/json; charset=UTF-8',
  		  dataType:'json',
  		  data: JSON.stringify(tempdata),
 		  success:function(data){
 			if(data.code==1){
 				
 				alert(data.message);
 				  location.href='/moimlistView/moimdetailView/'+moimNo;
 					socket.send('/noti/banNoti',{},JSON.stringify({moimNo:moimNo,  moimTitle: moimTitle,moimPeopleList:moimPeopleList,userName: $(this).val()}));
         			socket.send('/moim/banNoti',{},JSON.stringify({moimNo:moimNo,  userName: $(this).val()}));
     		
 				  
 			}else{
 				alert(data.message);
 			}
 		},
           error:function(e){

           }
       });
});


$('#signup_btn').off().on('click', function () {//스터디 가입하기 by choiseongjun 2019-09-20
	
    var moimNo = $('#moimNo').attr("data-moimNo");
    var joinCondition = $('#joinCondition').attr("data-joinCondition");
    $.ajax({
       url : '/moimParticipant/'+moimNo, 
       type : "post",   
       processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
        contentType: false,
       contentType: 'application/json; charset=UTF-8',
       dataType   : 'json',  
       data      : JSON.stringify(moimNo),
        success:function(data){
           if(data.code==1){
             //  console.log("success callback data");
             //  sendEcho(moimNo);
        	   
        	   var userName = $("#userName").attr("data");
           	var moimTitle = $('#moimTitle').attr("data-moimTitle");
           	
       	  
      		if(joinCondition=='N'){
      			if(moimPeopleList.length!=1)
      				 moimPeopleList=moimPeopleList.join(',');
          		if(!isStomp && socket.readyState!==1) return;
          		let moimNo=$('#moimNo').attr("data-moimNo");
          			socket.send('/noti/joinNoti',{},JSON.stringify({moimNo:moimNo, userName: userName, moimTitle: moimTitle,moimPeopleList:moimPeopleList}));
          			socket.send('/moim/joinNoti',{},JSON.stringify({moimNo:moimNo, userName: userName, moimTitle: moimTitle}));
      		}else{
      			socket.send('/noti/apprNoti',{},JSON.stringify({moimNo:moimNo, moimTitle: moimTitle,userName:userName}));
      			
      		}
        	   
               alert(data.message);
               location.href='/moimlistView/moimdetailView/'+moimNo;
        	   
        		
         }else if(data.code==2){
        	 alert(data.message);
         }else{
            alert(data.message);
         }
        },
        error:function(e){

        }
    });
});
$('#signup_btnY').off().on('click', function () {//스터디 가입하기 by choiseongjun 2019-09-20

    var moimNo = $('#moimNo').attr("data-moimNo");
    $.ajax({
       url : '/moimParticipantY/'+moimNo, 
       type : "post",   
       processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
        contentType: false,
       contentType: 'application/json; charset=UTF-8',
       dataType   : 'json',  
       data      : JSON.stringify(moimNo),
        success:function(data){
           if(data.code==1){
               console.log("success callback data");
               alert(data.message);
               location.href='/moimlistView/moimdetailView/'+moimNo;
               sendEcho(moimNo);
         }else{
            alert(data.message);
         }
        },
        error:function(e){

        }
    });
});

// 가입 알림 보내기 by yn 2019/11/15
//function sendEcho(moimId){
//    	console.log("reply.js::socket>>", socket);
//    	console.log('여기타나??');
//    	var sessionEmail = $('#sessionUserEmail').attr("data-sessionUserEmail");
//    	var moimTitle = $('#moimTitle').attr("data-moimTitle");
//		if (socket) {
//			// websocket에 보내기 (regist,모임id,모임명,가입유저명,모임가입자들)
//			console.log("moimPeopleList>>", moimPeopleList);
//			let socketMsg = "regist," + moimId + "," + moimTitle + "," + sessionEmail + "," + moimPeopleList; 
//			console.log("sssssssmsg>>", socketMsg);
//			socket.send(socketMsg);
//		}
//}

$('#withdraw_btn').off().on('click', function () {//스터디 탈퇴하기 by choiseongjun 2019-09-20
	 
	 var moimNo = $('#moimNo').attr("data-moimNo");
     var moimPeopleNo = $('#moimPeopleNo').attr("data-moimPeopleNo");
     moimPeopleNo*=1;
     $.ajax({
          url : '/moimParticipant/deletejoinedPeople/'+moimPeopleNo, 
          type : "DELETE",   
          processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
          contentType: false,
          contentType: 'application/json; charset=UTF-8',
          dataType   : 'json',  
          data      : JSON.stringify(moimPeopleNo),
          success:function(data){
              if(data.code==1){
            	   
            	var userName = $("#userName").attr("data");
              	var moimTitle = $('#moimTitle').attr("data-moimTitle");
              	
          	
         			if(moimPeopleList.length!=1)
         				 moimPeopleList=moimPeopleList.join(',');
             		if(!isStomp && socket.readyState!==1) return;
             		let peopleId=$('#peopleId').attr("data");
             		let moimNo=$('#moimNo').attr("data-moimNo");
             			socket.send('/noti/exitNoti',{},JSON.stringify({moimNo:moimNo, userName: peopleId, moimTitle: moimTitle,moimPeopleList:moimPeopleList}));
             			socket.send('/moim/exitNoti',{},JSON.stringify({moimNo:moimNo, userName: userName, moimTitle: moimTitle}));
         		
           	   
            	  
            	  alert(data.message);
                  location.href='/moimlistView/moimdetailView/'+moimNo;
              }else{
                  alert(data.message);
               }
           },
           error:function(e){

           }
       });
   
});
//스터디 가입취소  by choiseongjun 2020-04-24
$('#registerCancle_btn').off().on('click', function () {
	 
	 var moimNo = $('#moimNo').attr("data-moimNo");
    var moimPeopleNo = $('#moimPeopleNo').attr("data-moimPeopleNo");
    moimPeopleNo*=1;
    $.ajax({
         url : '/moimParticipant/registerCancle_btn/'+moimPeopleNo, 
         type : "DELETE",   
         processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
         contentType: false,
         contentType: 'application/json; charset=UTF-8',
         dataType   : 'json',  
         data      : JSON.stringify(moimPeopleNo),
         success:function(data){
             if(data.code==1){
           	   
//           	var userName = $("#userName").attr("data");
//             	var moimTitle = $('#moimTitle').attr("data-moimTitle");
//             	
//         	
//        			if(moimPeopleList.length!=1)
//        				 moimPeopleList=moimPeopleList.join(',');
//            		if(!isStomp && socket.readyState!==1) return;
//            		let peopleId=$('#peopleId').attr("data");
//            		let moimNo=$('#moimNo').attr("data-moimNo");
//            			socket.send('/noti/exitNoti',{},JSON.stringify({moimNo:moimNo, userName: peopleId, moimTitle: moimTitle,moimPeopleList:moimPeopleList}));
//            			socket.send('/moim/exitNoti',{},JSON.stringify({moimNo:moimNo, userName: userName, moimTitle: moimTitle}));
        		
          	   
           	  
           	  alert(data.message);
                 location.href='/moimlistView/moimdetailView/'+moimNo;
             }else{
                 alert(data.message);
              }
          },
          error:function(e){

          }
      });
  
});
//스터디 삭제 by choiseongjun 2019-09-20
$("#moim_delete_btn").click(function(){
	var moimNo = $('#moimNo').attr("data-moimNo");
	
	$.ajax({
		url:'/moimDetail/moimDeleteOne/'+moimNo,
		type:'DELETE',
		contentType: 'application/json; charset=UTF-8',
		dataType:'json',
		success:function(data){
			if(data.code==1){
				alert(data.message);
				location.href='/moimlistView';
			}else{
				alert(data.message)
			}
		},
		error:function(xhr,error){
			
		}
	});
});


// 모임 비공개 전환 by hyozkim 2020-01-14
$("#moimClosedBtn").click(function() {
    var moimNo = $('#moimNo').attr("data-moimNo");

    $.ajax({
        url:'/moimUpdateClosed/'+moimNo,
        type:'PUT',
        contentType: 'application/json; charset=UTF-8',
        dataType:'json',
        success:function(data){
            if(data.code == 1){
                alert(data.message);
                location.href='/moimlistView';

            }else{
                alert(data.message);
            }
        },
        error:function(xhr,error) {

        }
    });

});

var moimPeopleList;
$(document).ready(function(){
	
	
	var moimNo = $('#moimNo').attr("data-moimNo");
	$.ajax({
		type : 'GET',
		headers : {
			Accept : "application/json; charset=utf-8",
			"Content-Type" : "application/json; charset=utf-8"
		},
		url : '/TodoStatusChart/'+moimNo,
		success : function(result) {
			google.charts.load('current', {
				'packages' : [ 'corechart' ]
			});
			google.charts.setOnLoadCallback(function() {
				drawChart(result);
			});
		}
	});
	$.ajax({
		type : 'GET',
		headers : {
			Accept : "application/json; charset=utf-8",
			"Content-Type" : "application/json; charset=utf-8"
		},
		url : '/TodoCompltLankChart/'+moimNo,
		success : function(result) {
			google.charts.load('current', {
				'packages' : [ 'corechart' ]
			});
			google.charts.setOnLoadCallback(function() {
				drawChartEnd(result);
			});
		}
	});
	$.ajax({
		type : 'GET',
		headers : {
			Accept : "application/json; charset=utf-8",
			"Content-Type" : "application/json; charset=utf-8"
		},
		url : '/TodoTimeline/'+moimNo,
		success : function(result) {
			google.charts.load('current', {
				'packages' : [ 'corechart' ]
			});
			google.charts.setOnLoadCallback(function() {
				drawChartTimeLine(result);
			});
		}
	});
	$.ajax({
			url : '/moimParticipant/searchJoinedPeople/'+moimNo, 
			type:'get',
			contentType: 'application/json; charset=UTF-8',
	        enctype: 'multipart/form-data',
	        processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
	        contentType: false,
	        dataType:'json',
	        cache: false,
	        mimeType:"multipart/form-data",
	        success:function(data){
	         if(data.code==1){
	        	 moimPeopleList = data.moimPeople;
	            console.log(moimPeopleList);
	         }else{
	            alert(data.message)
	         }
      },
      error:function(xhr,error){
         
      }
    });
});
function drawChart(result) {
	console.log('chart')
	var chartData=result.StachartList;
	
	
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'status');
	data.addColumn('number', 'cnt');
	
	var dataArray = [];
	$.each(result, function(i, obj) {
		dataArray.push([ obj.name, obj.quantity ]);
	});
	for(var i=0;i<chartData.length;i++){
		console.log(chartData[i]);
		dataArray.push([ chartData[i].status, chartData[i].cnt ]);
	}
	data.addRows(dataArray);

	var piechart_options = {
		title : '현재 계획상태리스트',
		width : 300,
		height : 300
	};
	var piechart = new google.visualization.PieChart(document
			.getElementById('piechart_div'));
	piechart.draw(data, piechart_options);

//	var barchart_options = {
//		title : 'Barchart: How Much Products Sold By Last Night',
//		width : 400,
//		height : 300,
//		legend : 'none'
//	};
//	var barchart = new google.visualization.BarChart(document
//			.getElementById('barchart_div'));
//	barchart.draw(data, barchart_options);
}
function drawChartEnd(result) {
	var chartData=result.StachartList;
	
	
	var data = new google.visualization.DataTable();
	data.addColumn('string', '이름');
	data.addColumn('number', '완료한 갯수');
	
	var dataArray = [];
	$.each(result, function(i, obj) {
		dataArray.push([ obj.name, obj.cnt ]);
	});
	for(var i=0;i<chartData.length;i++){
		dataArray.push([ chartData[i].name, chartData[i].cnt ]);
	}
	data.addRows(dataArray);

//	var piechart_options = {
//		title : '현재 계획상태리스트',
//		width : 300,
//		height : 300
//	};
//	var piechart = new google.visualization.PieChart(document
//			.getElementById('piechart_div'));
//	piechart.draw(data, piechart_options);

	var barchart_options = {
		title : '현재 계획완료한 사람 랭킹',
		width : 400,
		height : 300,
		legend : 'none'
	};
	var barchart = new google.visualization.BarChart(document
			.getElementById('barchart_div'));
	barchart.draw(data, barchart_options);
}
google.charts.load('current', {'packages':['timeline']});
//function drawChart() {
//  var container = document.getElementById('timeline');
//  var chart = new google.visualization.Timeline(container);
//  var dataTable = new google.visualization.DataTable();
//
//  dataTable.addColumn({ type: 'string', id: 'President' });
//  dataTable.addColumn({ type: 'date', id: 'Start' });
//  dataTable.addColumn({ type: 'date', id: 'End' });
//  dataTable.addRows([
//    [ 'Washington', new Date(1789, 3, 30), new Date(1797, 2, 4) ],
//    [ 'Adams',      new Date(1797, 2, 4),  new Date(1801, 2, 4) ],
//    [ 'Jefferson',  new Date(1801, 2, 4),  new Date(1809, 2, 4) ]]);
//
//  chart.draw(dataTable);
//}
function drawChartTimeLine(result){
	
	var chartData=result.timeLinelist;
	
	 var container = document.getElementById('timeline');
	  var chart = new google.visualization.Timeline(container);
	  var dataTable = new google.visualization.DataTable();

	  dataTable.addColumn({ type: 'string', id: 'President' });
	  dataTable.addColumn({ type: 'date', id: 'Start' });
	  dataTable.addColumn({ type: 'date', id: 'End' });
	  
	  for(var i=0;i<chartData.length;i++){
		  dataTable.addRows([
			    [ chartData[i].title, new Date(chartData[i].a), new Date(chartData[i].b) ],
			    ]);
	  }
//	  dataTable.addRows([
//	    [ 'Washington', new Date(1789, 3, 30), new Date(1797, 2, 4) ],
//	    [ 'Adams',      new Date(1797, 2, 4),  new Date(1801, 2, 4) ],
//	    [ 'Jefferson',  new Date(1801, 2, 4),  new Date(1809, 2, 4) ]]);

	  chart.draw(dataTable);
	
}