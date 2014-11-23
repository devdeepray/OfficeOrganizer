	
var day_of_week = new Array('Sun','Mon','Tue','Wed','Thu','Fri','Sat');
var month_of_year = new Array('January','February','March','April','May','June','July','August','September','October','November','December');

//DECLARE AND INITIALIZE VARIABLES
var sc_calendar = new Date();

var sc_viewyear = sc_calendar.getFullYear();     // Returns year
var sc_trueyear = sc_viewyear;
var sc_viewmonth = sc_calendar.getMonth();    // Returns month (0-11)
var sc_truemonth = sc_viewmonth;
var sc_viewday = sc_calendar.getDate();    // Returns day (1-31)
var sc_trueday = sc_viewday;
var sc_trueweekday = sc_calendar.getDay();
var sc_DAYS_OF_WEEK = 7;    // "constant" for number of days in a week
var sc_DAYS_OF_MONTH = 42;    // "constant" for number of days in a month

function sc_nextMonth()
{
	sc_viewmonth = (sc_viewmonth+1) % 12;
	if(sc_viewmonth == 0) sc_viewyear = sc_viewyear+1;
	sc_render();
}
function sc_prevMonth()
{
	sc_viewmonth = (sc_viewmonth+11) % 12;
	if(sc_viewmonth == 11) sc_viewyear = sc_viewyear-1;
	sc_render();
}
function sc_render()
{

	var cal;    // Used for printing
	sc_calendar.setDate(1);    // Start the calendar day at '1'
	sc_calendar.setMonth(sc_viewmonth);    // Start the calendar month at now
	sc_calendar.setYear(sc_viewyear);

	/* VARIABLES FOR FORMATTING
NOTE: You can format the 'BORDER', 'BGCOLOR', 'CELLPADDING', 'BORDERCOLOR'
      tags to customize your caledanr's look. */

	var TR_start = '<TR>';
	var TR_end = '</TR>';
	var TD_end = '</CENTER></TD>';

	/* BEGIN CODE FOR CALENDAR
NOTE: You can format the 'BORDER', 'BGCOLOR', 'CELLPADDING', 'BORDERCOLOR'
tags to customize your calendar's look.*/

	cal = '';


	cal +=  '<TABLE>';
	cal += '<TR><TD colspan=1><a href="#" onclick="sc_prevMonth()">Prev</a></TD><TD colspan=5>' + month_of_year[sc_viewmonth] + ', ' + sc_viewyear + '</TD><TD colspan=1><a href="#" onclick="sc_nextMonth()">Next</a></TD></TR>';
	cal += '<tr><td id="numcell">Su</td><td id="numcell">Mo</td><td id="numcell">Tu</td><td id="numcell">We</td><td id="numcell">Th</td><td id="numcell">Fr</td><td id="numcell">Sa</td></tr>';

	if(sc_calendar.getDay() != 0) cal+= '<TR>';


	var startskip = sc_calendar.getDay();
//	FILL IN BLANK GAPS UNTIL TODAY'S DAY
	for(index=0; index < sc_calendar.getDay(); index++)
		cal += '<TD id="numcell">' + '_' + '</TD>';

//	LOOPS FOR EACH DAY IN CALENDAR
	for(index=0; index < sc_DAYS_OF_MONTH - startskip; index++)
	{

		// RETURNS THE NEXT DAY TO PRINT
		week_day =sc_calendar.getDay();

		// START NEW ROW FOR FIRST DAY OF WEEK
		if(week_day == 0)
			cal += '<TR>';

		if(sc_calendar.getDate() > index){
			if(week_day != sc_DAYS_OF_WEEK)
			{

				// SET VARIABLE INSIDE LOOP FOR INCREMENTING PURPOSES
				var day  = sc_calendar.getDate();

				// HIGHLIGHT TODAY'S DATE
				if( day==sc_trueday && sc_truemonth==sc_viewmonth && sc_trueyear==sc_viewyear)
					cal += '<TD id="numcell">' + '<a href="#" onclick=changeBigCal(' + day + ',' + sc_viewmonth + ',' + sc_viewyear +      ')>' +day + '</a>'+ TD_end;

				// PRINTS DAY
				else
					cal += '<TD id="numcell">'+'<a href="#" onclick=changeBigCal(' + day + ',' + sc_viewmonth + ',' + sc_viewyear +      ')>' + day + '</a>'  +  TD_end;
			}
		}
		else cal+= '<TD id="numcell">' + '_' + '</TD>';
		// END ROW FOR LAST DAY OF WEEK
		if(week_day == sc_DAYS_OF_WEEK)
			cal += '</TR>';


		// INCREMENTS UNTIL END OF THE MONTH
		sc_calendar.setDate(sc_calendar.getDate()+1);

	}// end for loop
//	if(Calendar.getDay() != 0)
	//for(var i=Calendar.getDay(); i < 7; ++i) cal+='<TD id="numcell">' + ' ' + '</TD>';

	cal += '</TABLE>';

//	PRINT CALENDAR
	document.getElementById("smallCalendarBox").innerHTML = cal;
}
function changeBigCal(day, month, year) {
	
		bc_render(new Date(year, month, day));
		
	//
}