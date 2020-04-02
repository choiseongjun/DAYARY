$(function(){
	$(".find_crew .top_box .crew_util_box .control_box a").on("click", function(){
		var obj_left_first = $(".find_crew .top_box .crew_util_box .tabbtn_box ul li:first-child").offset().left;
		var obj_left_last = $(".find_crew .top_box .crew_util_box .tabbtn_box ul li:last-child").offset().left;
		if($(this).hasClass("prev")){
			$(".find_crew .top_box .crew_util_box .tabbtn_box").stop().animate({
				scrollLeft: obj_left_first
			},600);
		}else{
			$(".find_crew .top_box .crew_util_box .tabbtn_box").stop().animate({
				scrollLeft: obj_left_last
			},600);
		}
	});
	
	$(".find_crew .top_box .detail_search .search_open_btn").on("click", function(){
		if($(this).hasClass("open")){
			$(this).removeClass("open").addClass("close").find(".ment").text("닫기");
			$(".find_crew .top_box .detail_search .detail_search_inner").stop().slideDown(500);
		}else{
			$(this).removeClass("close").addClass("open").find(".ment").text("열기");
			$(".find_crew .top_box .detail_search .detail_search_inner").stop().slideUp(500);
		}
	});
	
	$(".custom_select").selectric({
		onInit: function(e){
			$(".selectric-input").remove();
			$(this).parent('.selectric-hide-select').siblings(".selectric-items").find("li").wrapInner('<a href="#none"></a>');
			$(".selectric-button").attr("type","button");
			
			$(".find_crew .top_box .detail_search .detail_search_inner .top_search_box > .item").each(function(){
				$(this).find(".selectric-items .selectric-scroll").niceScroll({
					cursorborder: "0",
					cursoropacitymin: 1,
					cursorwidth: "5px",
					cursorcolor: "#c4c4c4",
					background: "#e6e6e6",
					zindex: 5,
					scrollspeed: 30,
					railpadding: { top: 10, right: 2, left: 2, bottom: 10 },
				});
			});
		},
		onOpen: function(e){
			$(".find_crew .top_box .detail_search .detail_search_inner .top_search_box > .item").each(function(){
				var scroll_obj = $(this).find(".selectric-scroll ul");
				var scroll_obj_height = scroll_obj.height();
				if(scroll_obj_height > 160){
					scroll_obj.parent().addClass("over_scroll");
				}
			});
			$(".find_crew .top_box .detail_search .detail_search_inner .top_search_box > .item .selectric-items .selectric-scroll").getNiceScroll().resize();
		},
	});
	
	$(".selectric .selectric-button").on("click", function(){
		$(this).parent().siblings(".selectric-items").find('li[data-index="0"] a').focus();
	});
	$(".selectric .selectric-button, input#search_input").on("focus", function(){
		$(".custom_select").selectric('close');
	});
	
	$(".find_crew .bottom_box .grid_item_box .add_team").height($(".find_crew .bottom_box .grid_item_box .card").height()-0.5);
	
	$(".find_crew .bottom_box .grid_item_box .card").each(function(){
		var obj = $(this);
		obj.find(".percentage .bar .active").css("transition","ease-in-out 1s");
		setTimeout(function(){
			obj.find(".percentage .bar .active").css("width",""+obj.find(".percentage .txt span").text()+"%");
		},200);
	});
	
	$(".find_crew .bottom_box .grid_item_box .card").each(function(){
		
		var obj = $(this);
		var percent = Number($(this).find(".percentage .txt span").text());
		
		obj.find(".percentage .bar .active").css("width",""+obj.find(".percentage .txt span").text()+"%");
		if(percent > 60){
			$(this).find(".percentage").addClass("high");
		}else if(percent > 30 && percent < 60){
			$(this).find(".percentage").addClass("mid");
		}else if(percent > 0 && percent < 30){
			$(this).find(".percentage").addClass("low");
		}else if(percent == 0){
			$(this).find(".percentage").addClass("zero");
		}
	});
	
	$(".custom_scroll").niceScroll({
		cursorborder: "0",
		cursorwidth: "5px",
		cursorcolor: "#c4c4c4",
		cursorborderradius: "5px",
		background: "#e6e6e6",
		zindex: 5,
		scrollspeed: 30,
		railpadding: { top: 0, right: 0, left: 0, bottom: -6 },
	});
	
	$(window).on("resize", function(){
		$(".find_crew .bottom_box .grid_item_box .add_team").height($(".find_crew .bottom_box .grid_item_box .card").height()-0.5);
	});
	
});