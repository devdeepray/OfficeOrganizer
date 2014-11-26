
function deleteMemo(memo_id)
{;
	$.ajax({
	async: false,
    url     : "ManageMemos",
    type    : "POST",
    data    : "user_id="+sess_uid+"&password="+sess_pass+"&operation=deletememo&memo_id="+memo_id,
    success : function(jsondata){

    	fetchMemos();
    } 
	});
}

function fetchMemos()
{
	var memoData = null;
	$.ajax({
		async: false,
	    url     : "ManageMemos",
	    type    : "POST",
	    data    : "user_id="+sess_uid+"&password="+sess_pass+"&operation=fetchmemo",
	    success : function(jsondata){

	    	memoData = jsondata;
	    } 
	});
	data = '';
	var jsonObj = JSON.parse(memoData);
	for(var i=0; i<jsonObj.length; i++)
		{
			data+='<div id="singlememo'+(i%2)+'">\
				<table id="memotable">\
					<tr><td>'+ jsonObj[i].title +'</td><td><a href="#" onclick=deleteMemo('+jsonObj[i].memo_id+')>Del</a></td></tr>\
					<tr><td colspan=3>'+jsonObj[i].text+'</td><tr>\
				</table>\
				</div>';
		}
	
	document.getElementById("createdmemos").innerHTML = data;
}


function createMemo()
{

	var form = document.getElementById("memocreateform");
	var title = form.elements[0].value;
	var message = encodeURIComponent(form.elements[1].value);
	$.ajax({
		async: false,
	    url     : "ManageMemos",
	    type    : "POST",
	    data    : "user_id="+sess_uid+"&password="+sess_pass+"&operation=postmemo"+"&title="+title+"&text="+message,
	    success : function(jsondata){
	    	alert("Memo created");
	    } 
	});
	fetchMemos();
}