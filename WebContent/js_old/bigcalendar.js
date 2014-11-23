var day_of_week = new Array('Sun','Mon','Tue','Wed','Thu','Fri','Sat');
var month_of_year = new Array('January','February','March','April','May','June','July','August','September','October','November','December');

var time_slots = new Array('00:00', '00:30','01:00', '01:30','02:00', '02:30',
		'03:00', '03:30','04:00', '04:30','05:00', '05:30','06:00', '06:30','07:00', '07:30',
		'08:00', '08:30','09:00', '09:30','10:00', '10:30','11:00', '11:30','12:00', '12:30',
		'13:00', '13:30','14:00', '14:30','15:00', '15:30','16:00', '16:30','17:00', '17:30',
		'18:00', '18:30','19:00', '19:30','20:00', '20:30','21:00', '21:30','22:00', '22:30',
		'23:00', '23:30');

var bc_calendar = new Date();
var bc_viewyear = bc_calendar.getFullYear();     // Returns year
var bc_trueyear = bc_viewyear;
var bc_viewmonth = bc_calendar.getMonth();    // Returns month (0-11)
var bc_truemonth = bc_viewmonth;
var bc_viewday = bc_calendar.getDate();    // Returns day (1-31)
var bc_trueday = bc_viewday;
var bc_trueweekday = bc_calendar.getDay();
var bc_DAYS_OF_WEEK = 7;    // "constant" for number of days in a week
var bc_DAYS_OF_MONTH = 31;    // "constant" for number of days in a month
var bc_CURRENT_USER = sess_uid;

function bc_render(startDate)
{
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
	var TR_start = '<TR>';
	var TR_end = '</TR>';
	var TD_start = '<TD>';
	var TDid_start = '<TD id="';
	var TDid_end = '">';
	var TD_end = '</TD>';

	var date_iter = new Date(startDate);
	
	var tablabdata = '<DIV id="daycolumn" style="border:1px solid black; -webkit-box-sizing: border-box; -moz-box-sizing: border-box; box-sizing: border-box;">Time</DIV>';
	for(var i = 0; i < 7; ++i)
	{
		var date_str = date_iter.toDateString();
		date_str = date_str.substring(0, date_str.length - 4);
		tablabdata = tablabdata + '<DIV id="daycolumn" style="border:1px solid black; -webkit-box-sizing: border-box; -moz-box-sizing: border-box; box-sizing: border-box;">' + date_str + '</DIV>';
	}
	document.getElementById("bigCalendarTabLab").innerHTML = tablabdata;
	

	var data ='';
	data = '<DIV id="fullcalendar">';
	var timeslotscol = bc_renderTimeCol();
	data = data + '<DIV id="daycolumn">'  + timeslotscol + '</DIV>';
	
	var jsonobj = JSON.parse(jsontxt);
	var date = startDate;
	for(var i=0; i<7; ++i)
	{
		
		var daynum = startDate.getDay();
		var todate = startDate.getDate();
		var endate = "";
		if(todate < 10) endate = "0" + todate;
		else endate = todate;
		var tomon = startDate.getMonth() + 1;
		var enmon = "";
		if(tomon < 10) enmon = "0" + tomon;
		else enmon = tomon;
		var dateString = startDate.getFullYear() + "-" + enmon + "-" + endate;
		var start_times = jsonobj[dateString].start;
		var end_times = jsonobj[dateString].end;
		var content = jsonobj[dateString].agenda;
		var daycolumn = bc_renderDate(day_of_week[daynum],start_times,end_times,content); // Add the data from servlet here
		data = data + '<DIV id="daycolumn">' + daycolumn + '</DIV>';				
		startDate.setDate(startDate.getDate() + 1);
	}
	data = data + '</DIV>'; // close fullcalendar div
	document.getElementById("bigCalendarBox").innerHTML = data;
}

function bc_renderDate(dayName, start_times, end_times, content)
{
	var weekday = dayName;
	var data = '';
	var array_index = 0;
	data = data + '<TABLE id="tablecol" >';  //Top week day name
	for(var i = 0; i < 48; ++i)
	{
		if(start_times[array_index] == i)
		{
			var nrows = 20 * (end_times[array_index] - start_times[array_index] + 1);
			data = data + '<TR> <TD style="background-color:pink; height:' + nrows + 'px;">' + content[array_index] + '</TD> </TR>';
			i = end_times[array_index];
			array_index++;
		}
		else
		{
			var col = (i%4<2) ? '#EEEEFF' : '#DDDDFF';
			data = data + '<TR> <TD id="' + i + '" style="background-color:' + col + ';"> </TD> </TR>';
		}
	}
	data =  data + '</TABLE>';
	return data;
}

function bc_renderTimeCol()
{
	data =  '<TABLE id="tablecol" >';

	for(var i = 0; i < 48; ++i)
	{

		var col;
		if((i%4)<2)col='#EEEEFF';
		else
			col='#DDDDFF';
		data = data + '<TR><TD id="' + i + '" style="background-color:'+col+';width:100%;height:20px;">A' + time_slots[i] + '</TD></TR>'; //

	}
	data = data + '</TABLE>';
	return data;
}

function bc_renderNow()
{
	bc_render(new Date());
}