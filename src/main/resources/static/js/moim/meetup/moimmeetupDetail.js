$('[name="offmoimWithdraw_btn"]').on('click', function () {//가입승인 대기중인사람 승인하기 by choiseongjun 2019-10-07
	 var moimNo = $('#moimNo').attr("data-moimNo");
	 var meetupNo=$(this).val();
    let tempdata = {};
    tempdata.no1= $(this).val();
    tempdata.no2= $('#moimNo').attr("data-moimNo");
   

   $.ajax({
         url : '/moimlistView/moimdetailView/moimmeetupDetailView/offmoimWithdraw/'+moimNo+'/'+meetupNo, 
         type : "delete",   
         contentType: 'application/json; charset=UTF-8',
 		  dataType:'json',
 		  data: JSON.stringify(tempdata),
		  success:function(data){
			if(data.code==1){
				alert(data.message);
					
				location.href='/moimlistView/moimdetailView/moimmeetupDetailView/'+moimNo+'/'+meetupNo;
			}else{
				alert(data.message);
			}
		},
          error:function(e){

          }
      });
});

$('[name="offmoimJoin_btn"]').on('click', function () {//오프라인(meetup)모임 참가하기by choiseongjun 2019-10-12
	 
	var moimNo = $('#moimNo').attr("data-moimNo");
	var meetupNo=$(this).val();
    let tempdata = {};
    tempdata.no1= $(this).val();
    tempdata.no2= $('#moimNo').attr("data-moimNo");
   

   $.ajax({
         url : '/moimmeetupJoin', 
         type : "post",   
         contentType: 'application/json; charset=UTF-8',
 		  dataType:'json',
 		  data: JSON.stringify(tempdata),
		  success:function(data){
			if(data.code==1){
				alert(data.message);
					
				  location.href='/moimlistView/moimdetailView/moimmeetupDetailView/'+moimNo+'/'+meetupNo;
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

$(document).ready(function(){
	var x = $("#locationX").text();
	var y = $("#locationY").text();
	var container = document.getElementById('map');
	var options = {
		center: new kakao.maps.LatLng(y, x),
		level: 3
	};

	var map = new kakao.maps.Map(container, options);
	// 지도 확대 축소를 제어할 수 있는  줌 컨트롤을 생성합니다
	var zoomControl = new kakao.maps.ZoomControl();
	map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);

	// 마커가 표시될 위치입니다 
	var markerPosition  = new kakao.maps.LatLng(y, x); 

	// 마커를 생성합니다
	var marker = new kakao.maps.Marker({
	    position: markerPosition
	});

	// 마커가 지도 위에 표시되도록 설정합니다
	marker.setMap(map);
});

