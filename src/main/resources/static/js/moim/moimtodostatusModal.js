//모달창 띄우기  시작

// Get the modal
var modal = document.getElementById("myModal");

// Get the button that opens the modal
var btn = document.getElementById("myBtn");

// Get the <span> element that closes the modal
var span = document.getElementsByClassName("close")[0];

// When the user clicks the button, open the modal 
function modal_view(plan,writer,id,parent,email){
	modal.style.display = "block";
	  $("#title").text(plan);
	  $("#writer").text(writer);
	 
	  $("#toDoWriteListId").val(id);
	  $("#toDoWriteId").val(parent);
	
	$.ajax({
	    	url:'/moimDetail/moimTodoList/modalView/'+id,
	    	contentType: "application/json; charset=utf-8",
	        processData: false, //데이터를 쿼리 문자열로 변환하는 jQuery 형식 방지
	        success:function(data){
	        	 if(data.code==1){
	               var m=data.modal;
	               var mfile=data.modalfile;
	               console.log(m)
	               var html="<div class='container'><div class='row'><ul class='cbp_tmtimeline' style='background-color : white; width:1200px'>";
	               for(var i in m){
	            	   html+="<li><time class='cbp_tmtime' datetime="+m[i].moimBoard.create_date+" ><span>"+m[i].moimBoard.create_date.slice(0,16)+"</span></time> "
	            	   html+=' <div class="cbp_tmicon bg-info"><i class="zmdi zmdi-label"></i></div><div class="cbp_tmlabel">'
	            	   html+=' <blockquote><p class="blockquote blockquote-primary">'+m[i].moimBoard.memo+"</p></blockquote></li>"
	            	   if(m[i].file_loacate!=''){
	            		   html+="<img src='/getMoimImage/"+m[i].real_name+"' height='100px' width='100px'>";
	            		   //html+='<span class="float-left mr-3"><img th:src=${\'''/getMoimImage/'+moimDetail.imageName+'.'+moimDetail.imageExtension}"'+m[i].file_locate+'" alt="" class="thumb-lg rounded-circle"></span>'
	            	   }
	               }
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
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
  if (event.target == modal) {
    modal.style.display = "none";
  }
}
//글 작성
function submit(){
	if($("#message").val()==''&&$("#file").val==''){
		alert("내용을 작성하세요");
		return;
	}
	var MoimBoard={};
	MoimBoard.title=$("#title")[0].textContent;
	MoimBoard.memo=$("#message").val();
	
	let formData = new FormData();
	if($("#file").val()){
		var File;
		formData.append("File", $('#file')[0].files[0]);
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
	        		 $("#message").val("");
	        		 $("#file").val("");
	             }else{
	                 alert(data.message);
	             }
	        }, error:function(e){

	        }
	    });

	
}
function getCmaFileInfo(obj,stype) {
    var fileObj, pathHeader , pathMiddle, pathEnd, allFilename, fileName, extName;
    if(obj == "[object HTMLInputElement]") {
        fileObj = obj.value
    } else {
        fileObj = document.getElementById(obj).value;
    }
    if (fileObj != "") {
            pathHeader = fileObj.lastIndexOf("\\");
            pathMiddle = fileObj.lastIndexOf(".");
            pathEnd = fileObj.length;
            fileName = fileObj.substring(pathHeader+1, pathMiddle);
            extName = fileObj.substring(pathMiddle+1, pathEnd);
            allFilename = fileName+"."+extName;
 
            if(stype == "all") {
                    return allFilename; // 확장자 포함 파일명
            } else if(stype == "name") {
                    return fileName; // 순수 파일명만(확장자 제외)
            } else if(stype == "ext") {
                    return extName; // 확장자
            } else {
                    return fileName; // 순수 파일명만(확장자 제외)
            }
            var file = obj.target.file;
            
    } else {
            alert("파일을 선택해주세요");
            return false;
    }
    // getCmaFileView(this,'name');
    // getCmaFileView('upFile','all');
 }
 
function getCmaFileView(obj,stype) {
    var s = getCmaFileInfo(obj,stype);
    
}