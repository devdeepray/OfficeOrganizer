//
//		
//		
//				
//					<form id="fileForm" action="ManageFiles" method="POST" enctype="multipart/form-data">
//					
//
//									<input type="file" name="file" size="30" />
//
//<input id="saveForm" class="button_text" type="submit" name="submit" value="Submit" />
//						<input id="operation" type="hidden" name="operation" value="post"/>
//						<input id="user_id" type="hidden" name="user_id"/>
//						<input id="password" type="hidden" name="password"/>


function submituploadform()
{
	alert("File Upload" + getFileNames());
	var file = document.getElementById("fileuploaddata").files[0];
	var data = new FormData();
	data.append("user_id", sess_uid);
	data.append("password", sess_pass);
	data.append("operation", "post");
	data.append("file_name", getFileNames());
	data.append("file", file);
	$.ajax({
	    url : "ManageFiles",
	    type: "POST",
	    data : data,
	    processData: false,
	    contentType: false,
	    success:function(data, textStatus, jqXHR){
	      alert("File uploading successful");
	    },
	    error: function(jqXHR, textStatus, errorThrown){
	        //if fails     
	    }
	});
//	$.post("ManageFiles", {
//		data: data,
//		processData: false, // Don't process the files
//		contentType: false,
//		}, function(data) {
//			alert("asd");
//		$('#uploadform')[0].reset(); // To reset form fields
//	});
}

function getFileNames(){
    var files = document.getElementById("fileuploaddata").files;
    var names = "";
    for(var i = 0; i < files.length; i++)
        names += files[i].name;
    return names;
}




