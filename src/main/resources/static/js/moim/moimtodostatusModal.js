//모달창 띄우기  시작

// Get the modal
var modal = document.getElementById("myModal");

// Get the button that opens the modal
var btn = document.getElementById("myBtn");
var editor = document.getElementById("content-body");

var openBtn = document.getElementById("openBtn");
var modal_content= document.getElementById("modal_content");

// Get the <span> element that closes the modal
var span = document.getElementsByClassName("close")[0];

$(document).ready(function() {
  $('#summernote').summernote({
	  	placeholder: "contents",
        minHeight: 250,
        maxHeight: null,
        focus: true, 
        lang : 'ko-KR',
        callbacks: {
        	onImageUpload: function(files, editor, welEditable) {
        		for (var i = files.length - 1; i >= 0; i--) {
                    sendFile(files[i], this);
                  }
        	}
        }
  });
});

function sendFile(file, el) {
    let form_data = new FormData();
    form_data.append('file', file);
    $.ajax({
      data: form_data,
      type: "POST",
      url: '/moimDetail/moimTodoList/image',
      cache: false,
      contentType: false,
      enctype: 'multipart/form-data',
      processData: false,
      success: function(url) {
	        $(el).summernote('editor.insertImage', url);
	        $('#imageBoard > ul').append('<li><img src="'+url+'" width="480" height="auto"/></li>');
        }
    });
}

// 타임라인
function sendToTimeline(id){
	alert("id : "+ id);
	
	$.ajax({
		url: '/moimDetail/moimTodoList/boardTimeline/'+id,
		contentType: "application/json; charset=utf-8",
		processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
		success:function(data){
			if(data.code==1){
	        	var board = data.boardList;
	        	var timehtml = "";
	        	for(var i=0; i<board.length; i++){
	        		moimBoard = board[i].moimboard;
	        		for(var j=0; j<moimBoard.length; j++){
	        			timehtml+="<div class='card gedf-card'>";
	        			timehtml+='<div class="card-header">'
	        			timehtml+='<div class="d-flex justify-content-between align-items-center">'
        				timehtml+='<div class="d-flex justify-content-between align-items-center">'
        				timehtml+='<div class="mr-2">'
        				timehtml+='<img class="rounded-circle" width="45" src="https://picsum.photos/50/50">'
	        			timehtml+='</div>'
        				timehtml+='<div class="ml-2">'
	        			timehtml+='<div id="peoplename" class="h5 m-0">'+board[i].people.name+'</div>'
	        			timehtml+='<div class="h7 text-muted">Miracles Lee Cross</div>'
        				timehtml+='</div>'
	        			timehtml+='</div>'
        				timehtml+='</div>'
	        			timehtml+='</div>'
	        			timehtml+='<div class="card-body">'
	        			timehtml+='<div class="text-muted h7 mb-2"><i class="fa fa-clock-o"></i>'+moimBoard[j].createDate+'</div>'
	        			timehtml+='<a class="card-link" href="#">'
	        			timehtml+='<h5 class="card-title">'+board[i].toDoWrite.plan_title+'</h5>'
	                    timehtml+='<h6 class="card-title">'+moimBoard[j].title+'</h6></a>'
	                    timehtml+='<p class="card-text">'+moimBoard[j].memo+'</p></div>'
	                    timehtml+='<div class="card-footer"><a href="#" class="card-link"><i class="fa fa-gittip"></i> Like</a><a href="#" class="card-link">'
	                    timehtml+='<i class="fa fa-mail-forward"></i> Share</a></div>'
	                    timehtml+='</div>'
	        		}
	        	}//for
	        	alert("result : " + timehtml);
	        	$("#time_contents").html(timehtml);
	        	location.href='/moim/boardTimeline';
			}else{
				alert(data.code);
        	}
        },error:function(e){
        	alert(data.code);
        	alert(data.message);
        }
	});
}



// When the user clicks the button, open the modal 
function modal_view(plan,writer,id,parent,email){
	modal.style.display = "block";
	modal_content.style.display ="block";
	  $("#title").text(plan);
	  $("#writer").text(writer);
	 
	  $("#toDoWriteListId").val(id);
	  $("#toDoWriteId").val(parent);
	  var moimNo = $('#moimNo').attr("data-moimNo");
	  
	
	$.ajax({
	    	url:'/moimDetail/moimTodoList/modalView/'+id,
	    	contentType: "application/json; charset=utf-8",
	        processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
	        success:function(data){
	        	 if(data.code==1){
	               var m=data.modal;
	               var mfile=data.modalfile;
	               console.log(m);
	               var html="<div class='container'><div class='row'><ul class='cbp_tmtimeline' style='background-color : white; width:1200px'>";
	               for(var i=0;i<m.length;i++){
	            	   html+="<li id='"+m[i].id+"' style='height: 150px'><time class='cbp_tmtime' datetime="+m[i].createdAt+" ><span>"+m[i].createdAt.slice(0,10)+" "+m[i].createdAt.slice(11,20)+"</span></time> "
	            	   html+=' <div class="cbp_tmicon bg-info"><i class="zmdi zmdi-label"></i></div><div class="cbp_tmlabel">'
	            	   html+=' <blockquote><p class="blockquote blockquote-primary">'+m[i].memo+"</p></blockquote></div>"
	            	   html+=' <button type="button" id="del" onclick="del_content('+m[i].id+')" class="btn" style="float: right; margin-right: 1rem;">삭제</button>'
	            	   html+=' <button type="button" onclick="update_content(\''+m[i].id+'\',\''+m[i].memo+'\')\" class="btn" style="float: right; margin-right: 1rem;">수정</button>'
	            	   html+="</li>"
	            	   console.log(i)
	            	   if(typeof  m[i].moimBoardfile[0]!= 'undefined' && m[i].moimBoardfile[0].real_name != 'undefined'){
	            		  for(var j in m[i].moimBoardfile){
	            			  html+="<div class='img' onclick='expand(this)'>"
	            			  html+="<img  src='/getMoimImage/"+m[i].moimBoardfile[j].real_name+"' height='150px' width='250px'>";
	            			  html+="<span>+</span>"
	            			  html+="</div>"
	            		  }
	            	   }
	               }
	               html+='<li><button type="button" class="btn btn-primary moreBtn"'
            	   html+= 'onclick="sendToTimeline(\''+moimNo	+'\')">더보기</button>'
	               html+="</li>"
	               html+="</ul></div></div>";
	            	   
	              /* $(".form-group").html(c.memo);
	               $(".btn-group").toggle();*/
	               $("#modal_content").html(html);
	               if($("#Login").attr("data")!=email){
	         		  $("#modal_write").hide();
	         	  }else
	         		 $("#modal_write").show();
	        	 }else{
	                 alert(data.message);
	             }
	        }, error:function(e){

	        }
	});
}

// When the user clicks on <span> (x), close the modal
span.onclick = function() {
  modal.style.display = "none";
  showBoard();
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
  if (event.target == modal) {
    modal.style.display = "none";
    showBoard();
  }
}

// 에디터 보이기
function showEditor(memo, editFlag){
	var content = memo;
	if(!content){
		alert("param Null: "+content);
		$("#summernote").summernote('code',  '');
	}else{
		alert("param is Not Null: "+content);
		$("#summernote").summernote('code',  content);
	}
	
	editor.style.display = "block";
	openBtn.style.display ="none";
	modal_content.style.display ="none";
	
	// 수정버튼 작동일 경우
	if(editFlag){
		$('#submit').hide();
		$('#editBtn').show();
	}
	
	//이미지 모달창 위치 조절
	$('.modal-dialog').css('transform', 'translate(0, 400px)');
}

// 글목록 보이기
function showBoard(){
	editor.style.display = "none";
	openBtn.style.display ="block";
	modal_content.style.display ="block";
	$("#summernote").val("");
}

//글 작성
function submit(){
	
	var content = $("#summernote").val();
	if(content.trim() == ''){
		alert("내용을 작성하세요");
		return;
	}
	
	var MoimBoard={};
	MoimBoard.title=$("#title")[0].textContent;
	MoimBoard.memo= content;
	alert(content);
	
	let formData = new FormData();
	if(array.length>0){
		for(var i in array){
			formData.append("File", array[i][0]);
		}
	}

	formData.append('MoimBoard', new Blob([JSON.stringify(MoimBoard)], {
		type: "application/json; charset=UTF-8"
	}));
	$.ajax({
	      url:'/moimDetail/moimTodoList/modalWrite/'+$("#toDoWriteListId").val(),
	        type:'post',
	        enctype: 'multipart/form-data',
	        processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
	        contentType: false,
	        dataType:'json',
	        cache: false,
	        mimeType:"multipart/form-data",
	        data: formData,
	        success:function(data){
	        	 if(data.code==1){
	        		 modal.style.display = "none";
	        		 get_detail($("#toDoWriteId").val());
	        		 $("#summernote").val("");
	        		 $("#file").val("");
	        		 $("#imgList").html("");
	        		 array=[];
	             }else{
	                 alert(data.message);
	             }
	        }, error:function(e){

	        }
	    });
	showBoard();
}


$("#file").on("change",handleImgFileSelect);
var count=0;
var array=[];
var span = document.createElement("SPAN");
var txt = document.createTextNode("\u00D7"); 
var span2 = document.createElement("SPAN");
var txt2 = document.createTextNode("+"); 
span.appendChild(txt);
span2.appendChild(txt2)
	let imgList = document.getElementById('imgList'); // 미리보기 div 아이디
function handleImgFileSelect(e){
	let inputId = e.target.id; // 이벤트가 발생된곳 ID
	let imgId = 'img'+inputId; // 추가되는 이미지 ID
	var files = e.target.files; // 추가되는 파일
	var filesArr = Array.prototype.slice.call(files); // ?
	var fileName=e.target.files[0].name;
	array.push(e.target.files);
	/*let newFileButtonId = 'file'+ (count+1);
	let formId = document.getElementById('file');
	count++; // 숫자 추가
*/	filesArr.forEach(function(f) {
	
		var reader = new FileReader();
		reader.onload = function(e){
			
			imgList.appendChild(span);
			imgList.innerHTML += '<img src="'+e.target.result+'"name="'+fileName+'" id="'+imgId+'" style = "width:200px;height:100px; margin=2px;"/>'; // div에 미리보기 추가
		}
		reader.readAsDataURL(f);
	});
}
$("#imgList").on("click","span",function(e) {
	  var target=e.target.nextElementSibling;
	   target.remove();
	   e.target.remove();
	  for(var i=0;i<array.length;i++){
		 if(array[i][0].name==target.name){
			 array.splice(i,1);
		 }
	  }
});

//todo 리스트 내의 게시판글 수정 by suyn 2020-04-04
function update_content(id, memo){
	
	alert("수정됩니다 : " +id+"/ "+memo);
	
	var content = memo;
	showEditor(content, true);
	$('#boardId').val(id);
}

//글수정
function edit(){
	
	let moimBoard = {};
	moimBoard.memo = $("#summernote").val();
	
	var id = $('#boardId').val();
	
	$.ajax({
		url : '/moimDetail/moimTodoList/updateModalBoard/'+id,
        type:'PUT',
        contentType: 'application/json; charset=UTF-8',
        processData: false,
        dataType: 'json',
        data:JSON.stringify(moimBoard),
        success:function(data){
            if(data.code == 1){
                alert(data.message);
                location.reload();
            }else{
                alert(data.message);
            }
        },
        error:function(e){
      	  alert(e);
        }
    });
}

// todo 리스트 내의 게시판글 삭제
function del_content(id){
	
	alert("삭제됩니다 : " +id);
	
	
	 $.ajax({
         url : '/moimDetail/moimTodoList/deleteModalBoard/'+id, 
         type : "DELETE",
         processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
         contentType: false,
//         data: baseResponse,
          success:function(data){
            if(data.code==1){
            	alert(data.message);
            	alert('test00');
            	$("#"+id).remove();
           }else{
              alert(data.message);
           }
          },
          error:function(e){
        	  alert(e);
          }
      });
}


function expand(div){
	e=div.firstElementChild;
	if(e. height=="600"){
		e.height="150"
		e.width="250"
	}else{
		e.height="600"
		e.width= "1000"
	}
	







}
