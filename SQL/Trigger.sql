create trigger new_order_book after update on book_copies
for each row 
execute procedure autoAddBook();