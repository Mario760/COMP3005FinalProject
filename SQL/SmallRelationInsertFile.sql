delete from book_copies;
delete from order_book;
delete from place_order;
delete from tracking;
delete from shipping_address;
delete from register_address;
delete from publish_book;
delete from transfer_sale;
delete from publisher_address;
delete from book;
delete from "order";
delete from "user";
delete from address;
delete from publisher;
insert into "user"(ID,name,password) values(99,'admin','admin');

insert into book values(9780747532743,'Harry Potter','J.K.Rowling','Fantasy Fiction',309,15);
insert into book values(9874327421487,'Filosofía','Wille Rosas Rios','Education',126,3);
insert into book values(9351248795464,'Descubrimientos','Omari','History',263,12);

insert into book_copies values(9780747532743, 12, 15);
insert into book_copies values(9874327421487, 5, 3);
insert into book_copies values(9351248795464, 3, 4);

insert into publisher values('Bloomsbury Pub Ltd','bloomsburyPub@gmail.com',5198823626,57331192604865938);
insert into publisher values('EduCafe','klylzs3zs1p@temporary-mail.net',4166210969,359205660324407);

insert into publish_book values(9780747532743, 'Bloomsbury Pub Ltd');
insert into publish_book values(9874327421487, 'EduCafe');
insert into publish_book values(9351248795464, 'EduCafe');

insert into address values('N0N1R0',1660,'River Street','Petrolia','Ontario','Canada');
insert into address values('M9C3J5', 4656,'Wellington Street','Toronto','Ontario', 'Canada');

insert into publisher_address values('Bloomsbury Pub Ltd', 'N0N1R0');
insert into publisher_address values('EduCafe','M9C3J5');


insert into transfer_sale values (99,'Bloomsbury Pub Ltd');
insert into transfer_sale values (99,'EduCafe');