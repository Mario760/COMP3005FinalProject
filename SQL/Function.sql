create function autoAddBook()
returns trigger as $new_order_book$
begin
	if (select sum(copies_amount) from book_copies)<10
	then
		update book_copies
		set copies_amount = copies_amount +
			(select sum(order_copies_amount)
		 	 from order_book natural join "order"
		 	 where ISBN = NEW.ISBN and date >= (now()-interval '1' month))
		where ISBN = NEW.ISBN;
	end if;
return new;
end;
$new_order_book$
language plpgsql;


