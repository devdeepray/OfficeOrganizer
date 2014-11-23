var day_of_week = new Array('Sun','Mon','Tue','Wed','Thu','Fri','Sat');
var month_of_year = new Array('January','February','March','April','May','June','July','August','September','October','November','December');

var time_slots = new Array('00:00', '00:30','01:00', '01:30','02:00', '02:30',
		'03:00', '03:30','04:00', '04:30','05:00', '05:30','06:00', '06:30','07:00', '07:30',
		'08:00', '08:30','09:00', '09:30','10:00', '10:30','11:00', '11:30','12:00', '12:30',
		'13:00', '13:30','14:00', '14:30','15:00', '15:30','16:00', '16:30','17:00', '17:30',
		'18:00', '18:30','19:00', '19:30','20:00', '20:30','21:00', '21:30','22:00', '22:30',
		'23:00', '23:30');

//var bc_calendar = new Date();
//var bc_viewyear = bc_calendar.getFullYear();     // Returns year
//var bc_trueyear = bc_viewyear;
//var bc_viewmonth = bc_calendar.getMonth();    // Returns month (0-11)
//var bc_truemonth = bc_viewmonth;
//var bc_viewday = bc_calendar.getDate();    // Returns day (1-31)
//var bc_trueday = bc_viewday;
//var bc_trueweekday = bc_calendar.getDay();
var bc_DAYS_OF_WEEK = 7;    // "constant" for number of days in a week
var bc_DAYS_OF_MONTH = 31;    // "constant" for number of days in a month
var bc_CURRENT_USER = sess_uid;
var bc_viewdate = new Date();

function bc_render(startDate)
{
	bc_viewdate = startDate;
	var bc_CURRENT_USER_NAME = null;
	$.ajax({
		async : false,
		url : "ManageContacts",
		type : "POST",
		data : 'operation=getName&user_id=' + bc_CURRENT_USER,
		success : function(data) {
			bc_CURRENT_USER_NAME = data;

		}
	});
	
	var tday = startDate.getDate();
	var tmonth = startDate.getMonth();
	var tyear = startDate.getFullYear();
	var jsontxt = null;
	$.ajax({
		async: false,
	    url     : "DisplayCalendar",
	    type    : "POST",
	    data    : "user_id="+bc_CURRENT_USER+"&date="+tday+"/"+(tmonth+1)+"/"+tyear,
	    success : function(jsondata){
	    	jsontxt = jsondata;
	    } 
	});

	var navBarData = '<DIV class="bigCalendar" id="navbar" style="height:100%">\
						<TABLE style="width:100%; height:100%; "><TR>\
					<TD ><a href="#" onclick="bc_renderPrev()">Prev week</a></TD>\
					<TD ><a href="#" onclick="setViewUidAs('+sess_uid+')">My calendar</A></TD>\
					<TD >Current calender: '+bc_CURRENT_USER_NAME+'</TD>\
					<TD style="text-align:right;"><a href="#" onclick="bc_renderNext()">Next week</a></TD>\
				</DIV>';
	document.getElementById("bigCalendarNav").innerHTML = navBarData;
	
	var date_iter = new Date(startDate);
	
	var tablabdata = '<DIV class="bigCalendar" id="daycolumntop" >Time</DIV>';
	for(var i = 0; i < 7; ++i)
	{
		var date_str = date_iter.toDateString();
		date_str = date_str.substring(0, date_str.length - 4);
		tablabdata = tablabdata + '<DIV class="bigCalendar" id="daycolumntop" ><span>' + date_str + '</span></DIV>';
		date_iter.setDate(date_iter.getDate() + 1);
	}
	document.getElementById("bigCalendarTabLab").innerHTML = tablabdata;
	var jsonobj = JSON.parse(jsontxt);

	var data ='';
	var sp_arr = [0,0,0,0,0,0,0];
	data = '<DIV class="bigCalendar" id="fullcalendar"><TABLE class="bigCalendar" id="calendartable">';
	for(var tsid=0; tsid<48; ++tsid)
	{
		data += '<TR class="bigCalendar"><TD class="bigCalendar" id="timeslot">' + time_slots[tsid] + '</TD class="bigCalendar">';
		var date = new Date(startDate);
		for(var i=0; i < 7; ++i)
		{
			var daynum = date.getDay();
			var todate = date.getDate();
			var endate = "";
			if(todate < 10) endate = "0" + todate;
			else endate = todate;
			var tomon = date.getMonth() + 1;
			var enmon = "";
			if(tomon < 10) enmon = "0" + tomon;
			else enmon = tomon;
			var dateString = date.getFullYear() + "-" + enmon + "-" + endate;
			var start_times = jsonobj[dateString].start;
			var end_times = jsonobj[dateString].end;
			var content = jsonobj[dateString].agenda;
			if(sp_arr[i] == 0)
			{
				index = start_times.indexOf(tsid);
				if(index != -1)
				{
					sp_arr[i] = end_times[index] - start_times[index] ;
					data += '<TD class="bigCalendar" id="appointment" rowspan='+(sp_arr[i]+1)+'>'+content[index]+'</TD>';
				}
				else
				{
					var mode = 'lt';
					if(tsid%4<2) mode = 'dk';
					data += '<TD class="bigCalendar" id="'+mode+'blankcell"></TD>';
				}
			}
			else
			{
				sp_arr[i]--;
			}
			date.setDate(date.getDate()+1);
		}
		data+='</TR>';
	}
	data+= '</TABLE></DIV>';
	
	

	document.getElementById("bigCalendarBox").innerHTML = data;
}

function bc_renderNext()
{
	var tmp = bc_viewdate;
	tmp.setDate(bc_viewdate.getDate() + 1);
	bc_render(tmp);
}

function bc_renderPrev()
{
	var tmp = bc_viewdate;
	tmp.setDate(bc_viewdate.getDate() - 1);
	bc_render(tmp);
}

function bc_renderNow()
{
	bc_render(new Date());
}