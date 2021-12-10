create table book
	(ISBN			numeric(13),
	 name			varchar(20) not null,
	 author			varchar(20),
	 genre			varchar(20),
	 number_of_pages	numeric(4) check (number_of_pages > 0),
	 price			numeric(4) check (price > 0),
	 primary key (ISBN)
	);

create table book_copies
	(ISBN			numeric(13),
	 copies_amount		numeric(2) check (copies_amount > 0),
	 sale_rate		numeric(2) check (sale_rate > 0),
	 primary key (ISBN),
	 foreign key (ISBN) references book(ISBN)
		on delete cascade
	);

create table "order"
	(order_number		serial,
	 biller_name		varchar(20) not null,
	 billing_price		numeric(6) check (billing_price > 0),
	 date			DATE not null default CURRENT_DATE,
	 primary key (order_number)
	);

create table tracking
	(order_number		serial,
	 shipping_status	varchar(10)
		check (shipping_status in ('Progress', 'Shipped', 'Delivered')),
	 primary key (order_number),
	 foreign key (order_number) references "order"(order_number)
		on delete cascade
	);

create table address
	(postal_code		varchar(6),
	 street_no		numeric(4) check(street_no > 0),
	 street_name		varchar(20) not null,
	 city			varchar(20) not null,
	 province		varchar(20) not null,
	 country		varchar(20) not null,
	 primary key (postal_code)
	);

create table "user"
	(ID			serial,
	 name			varchar(20) not null,
	 password		varchar(20) not null,
	 contact_number		numeric(20),
	 primary key (ID)
	);

create table publisher
	(name			varchar(20),
	 email			varchar(50),
	 phone_number		numeric(20) not null,
	 banking_account	varchar(20) not null,
	 primary key (name)
	);

create table order_book
	(order_number		serial,
	 ISBN			numeric(13),
	 order_copies_amount	numeric(2),
	 primary key(ISBN, order_number),
	 foreign key(ISBN) references book(ISBN),
	 foreign key(order_number) references "order"(order_number)
		on delete cascade
	);

create table place_order
	(customer_ID		serial,
	 order_number		serial,
	 primary key(customer_ID, order_number),
	 foreign key(customer_ID) references "user"(ID)
	 	on delete cascade,
	 foreign key(order_number) references "order"(order_number)
	);

create table shipping_address
	(order_number		serial,
	 postal_code		varchar(6),
	 primary key(order_number, postal_code),
	 foreign key(order_number) references "order"(order_number)
		on delete cascade,
	 foreign key(postal_code) references address(postal_code)
	);

create table publish_book
	(ISBN			numeric(13),
	 publisher_name		varchar(20),
	 primary key(ISBN, publisher_name),
	 foreign key(ISBN) references book(ISBN),
	 foreign key(publisher_name) references publisher(name)
	);

create table transfer_sale
	(owner_ID		serial,
	 publisher_name		varchar(20),
	 primary key(owner_ID, publisher_name),
	 foreign key(publisher_name) references publisher(name),
	 foreign key(owner_ID) references "user"(ID)
	);

create table register_address
	(customer_ID		serial,
	 postal_code		varchar(6),
	 primary key(customer_ID, postal_code),
	 foreign key(customer_ID) references "user"(ID)
		on delete cascade,
	 foreign key(postal_code) references address(postal_code)
	);

create table publisher_address
	(publisher_name 	varchar(20),
	 postal_code		varchar(6),
	 primary key(publisher_name, postal_code),
	 foreign key(publisher_name) references publisher(name)
		on delete cascade,
	 foreign key(postal_code) references address(postal_code)
	);

create view owner as
	select id, name, password
	from "user"
	where contact_number IS NULL;

create view customer as
	select *
	from "user"
	where contact_number IS NOT NULL;
