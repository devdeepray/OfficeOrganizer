insert into all_users values ('1', 'Pratik Fegade', 'pass1');
insert into all_users values ('2', 'Devdeep Ray', 'pass2');
insert into all_users values ('3', 'Shanik Dhar', 'pass3');
insert into all_users values ('4', 'P Bharath Kumar', 'pass4');
insert into all_users values ('5', 'Sir Bijoy Singh', 'pass5');
insert into all_users values ('6', 'NSS Krageesh', 'pass6');
insert into all_users values ('7', 'Sasha Grey', 'pass7');

insert into meeting_rooms values (1, 100);
insert into meeting_rooms values (2, 50);
insert into meeting_rooms values (3, 75);
insert into meeting_rooms values (4, 200);
insert into meeting_rooms values (5, 5);

insert into contacts values ('1', '2', 20);
insert into contacts values ('1', '3', 10);
insert into contacts values ('2', '1', 15);
insert into contacts values ('3', '4', 25);
insert into contacts values ('4', '3', 20);
insert into contacts values ('5', '2', 30);
insert into contacts values ('5', '3', 30);
insert into contacts values ('5', '4', 30);
insert into contacts values ('5', '6', 30);
insert into contacts values ('6', '5', 15);
insert into contacts values ('7', '4', 10);
insert into contacts values ('7', '6', 13);
insert into contacts values ('6', '7', 120);

insert into memos values ('1', row( array[row('memo11', null, null), row('memo12', null, null)]::memo_item[]));
insert into memos values ('2', row( array[row('memo21', null, null), row('memo22', null, null)]::memo_item[]));
insert into memos values ('3', row( array[row('memo31', null, null), row('memo32', null, null)]::memo_item[]));
insert into memos values ('4', row( array[row('memo41', null, null), row('memo42', null, null)]::memo_item[]));
insert into memos values ('5', row( array[row('memo51', null, null), row('memo52', null, null)]::memo_item[]));

insert into all_appointments values (1, '12-12-2014', 27, 35, 2, 'appt1', '1');
insert into all_appointments values (2, '12-11-2014', 29, 35, 1, 'appt1', '2');
insert into all_appointments values (3, '10-30-2014', 27, 31, 3, 'appt1', '3');
insert into all_appointments values (4, '10-30-2014', 32, 34, 4, 'appt1', '4');
insert into all_appointments values (5, '10-30-2014', 19, 25, 1, 'appt1', '5');
insert into all_appointments values (6, '10-29-2014', 21, 29, 2, 'appt1', '6');

insert into user_appointments values (1, '2', 'confirmed');
insert into user_appointments values (2, '1', 'confirmed');
insert into user_appointments values (3, '3', 'confirmed');
insert into user_appointments values (4, '4', 'confirmed');
insert into user_appointments values (5, '1', 'confirmed');
insert into user_appointments values (6, '2', 'confirmed');

insert into user_messages values ('1', '5', 'message1', 'false', '2014-11-18 04:05:06');
insert into user_messages values ('2', '3', 'message1',	'true', '2014-11-28 05:05:06');
insert into user_messages values ('3', '4', 'message1', 'false', '2014-11-19 06:05:06');
insert into user_messages values ('5', '5', 'message1', 'true', '2014-11-24 07:05:06');
insert into user_messages values ('3', '6', 'message1', 'true', '2014-11-16 08:05:06');
insert into user_messages values ('4', '7', 'message1', 'false', '2014-11-27 09:05:06');