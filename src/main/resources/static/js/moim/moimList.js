
// [Ajax] 카테고리 별 모임 리스트 화면에 추가
function makeMoimList(item, itemCount) {
    // 모임 리스트 화면 추가

}

// [hyozkim] makeCategoryTab 카테고리 탭 화면에 추가
//function makeCategoryTab(item,commonCode) {
//    // console.log(item);
//    //console.log(commonCode);
//    //console.log(typeof commonCode);
//    var resJson = jQuery.parseJSON(JSON.stringify(item));
//    var categoryhtml = $('#categoryItem').html();
//     console.log(categoryhtml);
//
//    var resultHtml = "";
//    for(var i=0; i<resJson.length; i++) {
//        resultHtml += categoryhtml.replace("{commonCode}", resJson[i].commCode)
//                                    .replace("{className}","anchor")
//                                    .replace("{categoryName}", resJson[i].commName);
//    }
//    $(".category_tab_lst").html(resultHtml);
//
//    // 클릭한 카테고리 탭 active 활성화
//    Array.from(document.querySelector(".category_tab_lst").querySelectorAll("li")).forEach( a => {
//    	a.firstElementChild.className = (a.dataset.category == commonCode) ? "anchor active" : "anchor";
//    });
//}

// 카테고리 탭 Click Event
//$(".category_tab_lst").off().on('click', function(evt) {
//    var commonCode = "";
//	if( evt.target.tagName === "LI" ) {
//		//console.log(evt.target.dataset.category);
//		commonCode = evt.target.dataset.category;
//
//	} else if ( evt.target.tagName === "UL" ) {
//		//console.log(evt.target.firstChild.dataset.category);
//		commonCode = evt.target.firstChild.dataset.category;
//
//
//	} else if ( evt.target.tagName === "A" ) {
//		//console.log(evt.target.parentElement.dataset.category);
//		commonCode = evt.target.parentElement.dataset.category;
//
//
//	} else if( evt.target.tagName === "SPAN" ) {
//		//console.log(evt.target.parentElement.parentElement.dataset.category);
//		commonCode = evt.target.parentElement.parentElement.dataset.category;
//
//	}
//
//    $.ajax({
//      url:'/moimlist?commonCode='+commonCode,
//        type:'get',
//        enctype: 'multipart/form-data',
//        processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
//        contentType: false,
//        dataType:'json',
//        cache: false,
//        mimeType:"multipart/form-data",
//        success:function(data) {
//            //console.log(data.categories);
//            makeCategoryTab(data.categories,commonCode);
//
//            //console.log(data.moimList);
//            // makeMoimList(data.moimList, data.moimListCount);
//        }
//    });
//});






// [hyozkim] makeSelectElement 셀렉트 탭에 데이터 추가
function makeSelectElement(item, id) {
    $('#'+id).empty(); // 초기화

    for (var n=0; n<item.length; n++) {
        var code  = item[n].commCode;
        var name = item[n].commName;

        $('#'+id).append('<option value="' + code + '">' + name + '</option>');
    }
}

// [hyozkim] 모임 상태, 모임 주제, 모임 비공개 데이터 모두 가져오도록 수정.
function initMoimElement(opt) {

   $.ajax({
      url:'/moimelemtent',
        type:'get',
        enctype: 'multipart/form-data',
        processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
        contentType: false,
        dataType:'json',
        cache: false,
        mimeType:"multipart/form-data",
        //data: formData,
        success:function(data) {
        	
             makeSelectElement(data.element_CA1,'categorybox');
             makeSelectElement(data.element_CA2,'status');

             // 카테고리 탭 그리기
             // makeCategoryTab(data.element_CA1);

         /* ASIS
         $('#categorybox').empty(); // 초기화
         var item = data._category;
         for (var n=0; n<item.length; n++){
        	 var code  = item[n].commCode;
            var name = item[n].commName;
            $('#categorybox').append('<option value="' + code + '">' + name + '</option>');
         }
        */
        // $('#categorybox').selectmenu('refresh');

      }
   });
   //로딩화면 스탑i
   // isLoading.stop();
}




/**
 * 
 * @시/도/구 카테고리 조회 API
 */
$(function(){
	$.ajax({
		type: "get",
		url: "http://openapi.nsdi.go.kr/nsdi/eios/service/rest/AdmService/admCodeList.json",
		data : {authkey : $('#sido_key').val()},
		async: false,
		dataType: 'json',
		success: function(data) {
			var html =  "<option value=''>전체</option>";
			
			for(var i=0;i<data.admVOList.admVOList.length;i++){ 
				html +="<option value='"+data.admVOList.admVOList[i].admCode+"'>"+data.admVOList.admVOList[i].lowestAdmCodeNm+"</option>"
			}
            $('#sido_code').html(html);

			// Get initial value
            //$('#sido_code').selectric(html);
		},
		error: function(xhr, stat, err) {}
	});
	
	
	$(document).on("change","#sido_code",function(){
		var thisVal = $(this).val();		
		
		$.ajax({
			type: "get",
			url: "http://openapi.nsdi.go.kr/nsdi/eios/service/rest/AdmService/admSiList.json",
			data : {admCode : thisVal, authkey : $('#sigoon_key').val()},
			async: false,
			dataType: 'json',
			success: function(data) {
				var html = "<option value=''>선택</option>";
				var html = "";
				for(var i=0;i<data.admVOList.admVOList.length;i++){ 
					html +="<option value='"+data.admVOList.admVOList[i].admCode+"'>"+data.admVOList.admVOList[i].lowestAdmCodeNm+"</option>"
				}
				
	            $('#sigoon_code').html(html);
				
			},
			error: function(xhr, stat, err) {}
		});
	});
})