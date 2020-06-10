// Create a "close" button and append it to each list item

var myNodelist = document.getElementById("myUL").getElementsByTagName("LI");
var i;
for (i = 0; i < myNodelist.length; i++) {
  var span = document.createElement("SPAN");
  var txt = document.createTextNode("\u00D7");
  span.className = "close";
  span.appendChild(txt);
  myNodelist[i].appendChild(span);
}

// Click on a close button to hide the current list item
var close = document.getElementsByClassName("close");
var i;
for (i = 0; i < close.length; i++) {
  close[i].onclick = function() {
    var div = this.parentElement;
    div.style.display = "none";
  }
}

// Create a new list item when clicking on the "Add" button
function newElement() {
  var li = document.createElement("li");
  var inputValue = document.getElementById("myInput1").value;
  var t = document.createTextNode(inputValue);
  li.setAttribute("class","ui-state-default")
  li.appendChild(t);
  if (inputValue === '') {
    alert("You must write something!");
  } else {
    document.getElementById("myUL").appendChild(li);
  }
  document.getElementById("myInput1").value = "";

  var span = document.createElement("SPAN");
  var txt = document.createTextNode("\u00D7");
  span.className = "close";
  span.appendChild(txt);
  li.appendChild(span);

  for (i = 0; i < close.length; i++) {
    close[i].onclick = function() {
      var div = this.parentElement;
      div.style.display = "none";
    }
  }
}



     $("#from_date, #to_date ").datepicker({
     });

//날짜범위체크 종료일이 시작일보다 늦을경우 
var d=new Date();
function check2(){
	var from =new Date($( "#from_date" ).val());
	var to=new Date($( "#to_date" ).val());
 if(from>to){
	 alert("잘못된 날짜 범위입니다.");
	 $( "#to_date" ).val("");
 }
}
//시작일이 지난날짜인경우
function check1(){
	
	var from =new Date($( "#from_date" ).val());
	var to=new Date($( "#to_date" ).val());
	
	if(from.getDate()<d.getDate() ){
		alert("이미 지난 날짜입니다.");
		$( "#from_date" ).val("");
	}
	if(to!=''){
		check2();
	}
}
//엔터시 리스트에 추가
function onKeyDown(){
     if(event.keyCode == 13)
     {
    	 newElement();
     }
}

 function post_send() {
	 if($("#title").val()==""){
		 alert("제목을 작성해주세요.")
		 return;
	 }
	var temp = document.getElementById("myUL").getElementsByTagName("LI");
	var list="";
	for (var i in temp) {
		var x=temp[i].innerText;
		if(x!=null&&temp[i].style[0]==null){
			list+=temp[i].innerText.slice(0,-1)+",";
		}
	}
	if(list.length==0){
		alert("일정을 작성해주세요.")
		return;
	}
	var moimNo= $('#moimNo').attr("data-moimNo");
	let toDoWrite={};
	toDoWrite.plan_title=$("#title").val();
	toDoWrite.from_date=new Date($("#from_date").val());
	toDoWrite.to_date=new Date($("#to_date").val());
	let toDoWriteList={};
	toDoWriteList.toDoWrite=toDoWrite;
	toDoWriteList.plan_list=list;
	   $.ajax({
	      url:'/moimDetail/moimTodoList/moimTodowrite/'+moimNo,
	        type:'post',
	        data: JSON.stringify(toDoWriteList) ,
	        contentType: "application/json; charset=utf-8",
	        success:function(data){
	        	 if(data.code==1){
	        			socket.send('/noti/writeNoti',{},JSON.stringify({moimNo:moimNo, userName: $("#userName").attr("data")}));
	 					socket.send('/moim/writeNoti',{},JSON.stringify({moimNo:moimNo, userName:  $("#userName").attr("data") }));
	                 location.href='/moimDetail/moimTodoList/'+ $('#moimNo').attr("data-moimNo");
	             }else{
	                 alert(data.message);
	             }
	        }, error:function(e){

	        }
	    });

 }
 