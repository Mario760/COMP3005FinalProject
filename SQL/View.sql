create view sales(date,copies, author, genre, price) as
	select date, order_copies_amount, author, genre, price
	from "order" natural join order_book natural join book;

create view owner as
	select id, name, password
	from "user"
	where contact_number IS NULL;

create view customer as
	select *
	from "user"
	where contact_number IS NOT NULL;