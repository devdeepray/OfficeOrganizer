-- A list of all users using the system. Each user will have some details like UserID, Name, DOB, .....
-- A list of all meeting rooms available

-- Features:
-- 	- Address book
-- 		- A list of contacts for each user.
-- 		- Implementation: One big table of the form (u_id, contact_id)

-- 	- Memo
-- 		- A list of all memos for a user (Direct html storage??)
-- 			- Each memo is a list of tasks where each task is a tuple (description, enddateandtime)

-- 	- Personal Calendar
-- 		- A list of events for each user. Each event will be a tuple (description, enddateandtime). Other users can view the calender of an user.

-- 	- Appointments/Meetings
-- 		- A list of all appointments in the entire DB. Each appointment is a tuple (Date and Time, Place, CreatedBy, Attendees, Agenda)
-- 		- For each user, a list of his appointments

-- 	- Cloud
-- 		- For each user, a list of all his files

-- 	- Chat
--		- For each user a list of access frequencies for each contact



--Delete all tables in the database
drop schema public cascade;
create schema public;

-- A table of users in the system
create table all_users (
	user_id varchar(20),
	name varchar(40),
	password varchar(30),
	--date_of_birth date,
	primary key(user_id)
);

-- A table of all meeting rooms available in the organisation
create table meeting_rooms (
	room_no integer,
	--room_name varchar(20),
	capacity integer,
	primary key(room_no)
);

create function check_if_exists(varchar(20)) returns bigint AS $$
    (select count(*) from 
    	(select user_id from all_users) as cumulative_table
    	where user_id = $1);
$$ language sql;

-- A table of all contacts of all users
create table contacts (
	user_id varchar(20),
	contact_id varchar(20),
	access_frequency integer,
	foreign key(user_id) references all_users,
	constraint contact_exists check (check_if_exists(contact_id) > 0),
	primary key (user_id, contact_id)  
);

-- create function check_valid_datetime_memo (memo_date date, memo_time time) returns boolean as 
-- $$
-- begin
-- 	if memo_date is null and memo_time is null then
-- 		return true;
-- 	elsif memo_date is null then
-- 		return true;
-- 	elsif memo_time is null then
-- 		return (select memo_date > current_date);
-- 	else
-- 		return (select memo_date > current_date) and (select memo_time > current_time);
-- 	end if;
-- end;
-- $$ language plpgsql;

-- A memo item
create type memo_item as (
	memo_item_desc varchar(50),
	memo_item_date date,
	memo_item_time time
--	constraint only_future_memo check (check_valid_datetime_memo(memo_item_date, memo_item_time))
);

-- A memo
create type memo_type as (
	memo_items memo_item []
);

-- A table of all memoes for all users 
create table memos (
	user_id varchar(20),
	memo memo_type,
	foreign key(user_id) references all_users,
	primary key (user_id, memo)
);

create or replace function check_valid_datetime_appointment (date, time) returns boolean as $$
	(select (select $1::date > current_date) or ((select $1::date = current_date) and (select $2::time > current_time)));
	--(select $1::timestamp > current_timestamp(0));
$$ language sql;

create domain time_slot integer check (value <= 47 and value >= 0);

-- A list of all appoitments of all users
create table all_appointments (
	appointment_id integer,
	appointment_date date,
	appointment_time time_slot,
	appointment_end time_slot,
	room_no integer,
	agenda varchar(100),
	created_by varchar(20),
	primary key (appointment_id),
	-- constraint unique_appt unique (appointment_date, appointment_time, room_no),
	-- constraint only_future_appointment check (check_valid_datetime_appointment(appointment_date, appointment_time)),
	foreign key (room_no) references meeting_rooms on delete cascade
);

-- A table of users and their appointments
create table user_appointments (
	appointment_id integer,
	user_id varchar(20) not null,
	status varchar(20),
	primary key (user_id, appointment_id),
	foreign key (user_id) references all_users,
	foreign key (appointment_id) references all_appointments
);

create function update_freqs_appt() returns trigger as $$
	begin
		update contacts set access_frequency = access_frequency + 1 where user_id = (select created_by from all_appointments where appointment_id = NEW.appointment_id) and contact_id = NEW.user_id;
		return NEW;
	end;
	$$
	language plpgsql;
	
create trigger update_freqs_trigger after insert
	on user_appointments
	for each row
	execute procedure update_freqs_appt();

--create table all_messages (
--	message_id SERIAL,
--	message_text varchar(200),
--	primary key (message_id)
--);

create table user_messages (
	from_id varchar(20),
	to_id varchar(20),
	message_text varchar(200),
	read boolean,
	message_time timestamp,
	primary key (from_id, to_id, message_time)
);

create function update_freqs_messages() returns trigger as $$
	begin
		update contacts set access_frequency = access_frequency + 1 where user_id = NEW.from_id and contact_id = NEW.to_id;
		return NEW;
	end;
	$$
	language plpgsql;
	
create trigger update_freqs_trigger after insert
	on user_messages
	for each row
	execute procedure update_freqs_messages();

create table all_files (
	user_id varchar(20),
	file_name varchar(50),
	file bytea,
	primary key (user_id, file_name),
	foreign key (user_id) references all_users
);
	
--create or replace function get_weekly_appointments(integer, date) returns table(appointment_id integer, appointment_date date, appointment_time time, appointment_end time, room_no integer, agenda varchar(100), created_by integer, user_id integer, status varchar(20)) as $$
--	select * 
--	from all_appointments as x natural join user_appointments as y 
--	where x.appointment_date>=$2::date and x.appointment_date<=(select $2::date + interval '7 days') and y.user_id=$1::integer
--	order by appointment_date;
--$$ language sql;