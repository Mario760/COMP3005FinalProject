import java.sql.*;
import java.util.Scanner;

public class Owner {
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private final String id, name, password;
    private Connection conn;
    public Owner(String id, String name, String password, Connection conn){
        this.id = id;
        this.name = name;
        this.password = password;
        this.conn = conn;
    }

    public void operationMenu() throws SQLException {
        while(true) {
            System.out.println("Owner page: Please enter your operation [Add books / Remove books / Display reports / Log out]:");
            String inputOperation = scanner.next();
            if(inputOperation.equals("Add books")){
                addBooks();
            }else if(inputOperation.equals("Remove books")){
                removeBooks();
            }else if(inputOperation .equals("Log out")){
                break;
            }else if(inputOperation.equals("Display reports")){
                displayReport();
            }else{
                System.out.println("Unrecognized operation, please check if there is a typo!");
            }
        }
    }

    /**
     * Method to add new book
     * @throws SQLException
     */
    private void addBooks() throws SQLException {
        Statement stmt = conn.createStatement();
        String ISBN, name, author, genre, number_of_pages, price, copies_amount, sale_rate;
        int pages, priceAmount, copies, saleRate;
        ResultSet rset = stmt.executeQuery(
                "select name from publisher"
        );
        System.out.println("Here are the list of exist publishers' name in the database.");
        while(rset.next()){
            System.out.println(rset.getString(1));
        }
        System.out.println("-------------------------------------------------------------");
        System.out.print("Please type the publisher of the new book: ");
        String publisher = scanner.next();
        if(!checkPublisherExist(publisher)){
            addNewPublisher(publisher);
        }

        do {
            System.out.println("Enter the ISBN-13 of the new book: ");
            ISBN = scanner.next();
            if(!JDBC.isNumeric(ISBN) || ISBN.length()!=13) System.out.println("Please check and enter the correct ISBN.");
            else break;
        }while(true);

        do {
            System.out.print("Enter the name of the new book (<20 characters): ");
            name = scanner.next();
            if(name.length()>20) System.out.println("Please check and enter the correct ISBN.");
            else break;
        }while(true);

        do {
            System.out.print("Enter the author of the new book (<20 characters): ");
            author = scanner.next();
            if(author.length()>20) System.out.println("Please check and enter the correct author.");
            else break;
        }while(true);

        do {
            System.out.print("Enter the genre of the new book (<20 characters): ");
            genre = scanner.next();
            if(genre.length()>20) System.out.println("Please check and enter the correct genre.");
            else break;
        }while(true);

        do {
            System.out.print("Enter the total pages of the new book (<4 digits): ");
            number_of_pages = scanner.next();
            if(number_of_pages.length()>4||!JDBC.isNumeric(number_of_pages)){
                System.out.println("Please check and enter the correct pages number.");
            }
            else{
                pages = Integer.parseInt(number_of_pages);
                break;
            }
        }while(true);

        while(true){
            System.out.print("Enter the price of the new book (<4 digits): ");
            price = scanner.next();
            if(price.length()>4||!JDBC.isNumeric(price)){
                System.out.println("Please check and enter the correct price.");
            }
            else{
                priceAmount = Integer.parseInt(price);
                break;
            }
        }

        while (true) {
            System.out.print("How many copies of the new book do you want to add? (<2 digits): ");
            copies_amount = scanner.next();
            if(copies_amount.length()>2||!JDBC.isNumeric(copies_amount)){
                System.out.println("Please check and enter the correct copies amount.");
            }
            else{
                copies = Integer.parseInt(copies_amount);
                break;
            }
        }

        while(true){
            System.out.print("What is the sale rate of the new book? (0 < rate < 99): ");
            sale_rate = scanner.next();
            if(sale_rate.length()>2||!JDBC.isNumeric(sale_rate)){
                System.out.println("Please check and enter the correct sale rate.");
            } else{
                saleRate = Integer.parseInt(sale_rate);
                break;
            }
        }

        stmt.execute(
             "insert into book values ('"+ISBN+"','"+name+"','"+author+"','"+genre+"','"+pages+"','"+priceAmount+"','"+saleRate+"');"
        );
        stmt.execute(
             "insert into book_copies values ('"+ISBN+"','"+copies+"')"
        );

        System.out.println("New book added successfully!");
    }


    /**
     * Method to add new publisher
     * @param publisherName
     * @throws SQLException
     */
    private void addNewPublisher(String publisherName) throws SQLException {
        String email, phone_number, banking_account;
        Statement stmt = conn.createStatement();
        System.out.println("----------Creating new publisher's information------------------");

        do {
            System.out.print("Please type the new publisher's email (<50 characters):");
            email = scanner.next();
            if(email.length()>50 || email.length()==0) System.out.println("Input length error, try it again!");
        }while(email.length()<=50 && email.length()>0);

        do {
            System.out.print("Please type the new publisher's phone_number' (<20 digits):");
            phone_number = scanner.next();
            if(phone_number.length()>20 || phone_number.length()==0) System.out.println("Input length error, try it again!");
            else if(!JDBC.isNumeric(phone_number))System.out.println("Error format for phone number, check and try it again!");
            else break;
        }while(true);

        do {
            System.out.print("Please type the new publisher's banking account'(<20 characters):");
            banking_account = scanner.next();
            if(banking_account.length()>20 || banking_account.length()==0) System.out.println("Input length error, try it again!");
        }while(banking_account.length()<=20 && banking_account.length()>0);

        long phoneNumber = Long.valueOf(phone_number);
        stmt.execute(
                "insert into publisher values ('"+publisherName+"','"+email+"','"+phoneNumber+"','"+banking_account +"');"
        );
    }

    private boolean checkPublisherExist(String publisherName) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(
                "select * from publisher where name = '"+ publisherName+"'"
        );
        return rset.next();
    }

    private boolean checkBookExist(String ISBN) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(
                "select * from book where ISBN = '"+ ISBN+"'"
        );
        return rset.next();
    }

    private void removeBooks() throws SQLException {
        Statement stmt = conn.createStatement();
        String ISBN;
        System.out.println("-----------------------All Book List------------------------------");
        System.out.println("ISBN\t\t\t\tname\t\t\t\t\tauthor\t\t\t\tgenre");
        ResultSet rset = stmt.executeQuery(
           "select * from book"
        );
        while(rset.next()){
            System.out.print(rset.getString("ISBN")+"\t\t");
            System.out.print(rset.getString("name")+"\t\t\t");
            System.out.print(rset.getString("author")+"\t\t");
            System.out.print(rset.getString("genre")+"\n");
        }

        while(true) {
            System.out.print("Please type ISBN of the book that you want to remove:");
            ISBN = scanner.next();
            if(checkBookExist(ISBN)){
               break;
            }
            System.out.println("No such book found in the database. Please check the ISBN and try it again");
        }

        stmt.execute(
          "delete from book where ISBN = '"+ISBN+"'"
        );
        System.out.println("The book has been deleted successfully!");
    }

    private void displayReport() throws SQLException {
        Statement stmt = conn.createStatement();
    }
}
