import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

public class Customer {
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private HashMap<String,Integer> basket = new HashMap<>();
    private String id, name, password, phoneNumber;
    private Connection conn;
    public Customer(String id, String name, String password, String phoneNumber, Connection conn){
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.conn = conn;
    }


    public void operationMenu() throws SQLException {
        while(true) {
            System.out.println("Customer page: Please enter your operation [Search books / Browse books / Select books / Check out / Tracking order / Log out]:");
            String inputOperation = scanner.next();
            if(inputOperation.equals("Search books")){
                searchBooks();
            }else if(inputOperation.equals("Browse books")){
                browseBooks();
            }else if(inputOperation .equals("Log out")){
                break;
            }else if(inputOperation.equals("Select books")){
                selectBooks();
            }else if(inputOperation.equals("Check out")){
                checkOut();
            }else if(inputOperation.equals("Tracking order")){
                trackOrder();
            }else{
                System.out.println("Unrecognized operation, please check if there is a typo!");
            }
        }
    }

    private void searchBooks() throws SQLException {
        boolean isISBN = false, isName = false, isAuthor = false, isGenre = false;
        String filter, ISBN, bookName, author, genre;
        Statement stmt = conn.createStatement();
        String sql = "select * from book natural join book_copies where ";
        while(true) {
            System.out.println("Which filter do you want to add to search books? (Type \"END\" to stop adding filter) [ISBN / name / author / genre]:");
            filter = scanner.next();

            if(filter.equals("END")) {
                if (isISBN || isName || isAuthor || isGenre) {
                    break;
                } else {
                    return;
                }
            }

            if(isISBN || isName || isAuthor || isGenre){
                sql += " and ";
            }

            switch (filter) {
                case "ISBN":
                    if(isISBN){
                        System.out.println("Sorry you have entered ISBN already.");
                    }else {
                        System.out.println("Please enter your desired ISBN: ");
                        ISBN = scanner.next();
                        sql += "ISBN = '" + ISBN + "' ";
                        isISBN = true;
                    }
                    break;
                case "name":
                    if(isName){
                        System.out.println("Sorry you have entered name already.");
                    }else {
                        System.out.println("Please enter your desired book name: ");
                        bookName = scanner.next();
                        sql += "name = '" + bookName + "' ";
                        isISBN = true;
                    }
                    break;
                case "author":
                    if(isAuthor){
                        System.out.println("Sorry you have entered author already.");
                    }else {
                        System.out.println("Please enter your desired author name: ");
                        author = scanner.next();
                        sql += "author = '" + author + "' ";
                        isAuthor = true;
                    }
                    break;
                case "genre":
                    if(isGenre){
                        System.out.println("Sorry you have entered genre already.");
                    }else {
                        System.out.println("Please enter your desired genre: ");
                        genre = scanner.next();
                        sql += "genre = '" + genre + "' ";
                        isGenre = true;
                    }
                    break;
                default:
                    System.out.println("Unrecognized filter, try it again");
                    break;
            }
        }

        System.out.println(sql);
        ResultSet rset = stmt.executeQuery(sql);
        if(!rset.next()){
            System.out.println("Cannot find the related book. Check your input and try it again.");
            return;
        }else{
            rset = stmt.executeQuery(sql);
        }
        System.out.println("---------------------------------All Book List--------------------------------------------");
        System.out.println("ISBN\t\t\t\tname\t\t\t\t\tauthor\t\t\t\tgenre\t\t\t\ttotal pages\t\t\t\t price \t\t in stock");
        while(rset.next()) {
            System.out.print(rset.getString("ISBN") + "\t\t");
            System.out.print(rset.getString("name") + "\t\t\t");
            System.out.print(rset.getString("author") + "\t\t");
            System.out.print(rset.getString("genre") + "\t\t\t\t");
            System.out.print(rset.getString("number_of_pages") + "\t\t\t\t\t\t");
            System.out.print(rset.getString("price") + "\t\t\t\t");
            System.out.print(rset.getString("copies_amount") + "\n");
        }
        System.out.println("-------------------------------All Book List Ends-----------------------------------------");

    }

    /**
     * Browse all the in stock books
     */
    private void browseBooks() throws SQLException {
        Statement stmt = conn.createStatement();
        System.out.println("---------------------------------All Book List--------------------------------------------");
        System.out.println("ISBN\t\t\t\tname\t\t\t\t\tauthor\t\t\t\tgenre\t\t\t\ttotal pages\t\t\t\t price \t\t in stock");
        ResultSet rset = stmt.executeQuery(
                "select * from book natural join book_copies where (copies_amount != 0)"
        );
        while(rset.next()){
            System.out.print(rset.getString("ISBN")+"\t\t");
            System.out.print(rset.getString("name")+"\t\t\t");
            System.out.print(rset.getString("author")+"\t\t");
            System.out.print(rset.getString("genre")+"\t\t\t\t");
            System.out.print(rset.getString("number_of_pages")+"\t\t\t\t\t\t");
            System.out.print(rset.getString("price")+"\t\t\t\t");
            System.out.print(rset.getString("copies_amount")+"\n");
        }
        System.out.println("-------------------------------All Book List Ends-----------------------------------------");
    }

    private void selectBooks() throws SQLException {
        String ISBN;
        int copies;
        while(true) {
            System.out.println("Enter the book's ISBN that you want to select (type END to finisher selection): ");
            ISBN = scanner.next();
            if (ISBN.equals("END")) {
                break;
            }
            if (checkBookExistISBN(ISBN)) {
                int totalCopies = getCopiesAmount(ISBN);
                while (true) {
                    System.out.println("There are " + totalCopies + " in stock. How many copies do you want?: ");
                    copies = scanner.nextInt();
                    if (copies <= totalCopies) {
                        break;
                    }
                    System.out.println("There are not as many copies as you want. Try it again :P");
                }
                basket.put(ISBN, copies);
                System.out.println("The book(s) are added to your basket. Waiting for checking out ^V^");
            } else {
                System.out.println("Cannot find the related book");
            }
        }
    }

    private void checkOut() {
        if(basket.isEmpty()){
            System.out.println("You haven't select any books to your basket.");
            return;
        }



        basket.clear();
    }

    private void trackOrder() {
    }

    private boolean checkBookExistISBN(String ISBN) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(
                "select * from book where ISBN = '"+ ISBN+"'"
        );
        return rset.next();
    }

    private int getCopiesAmount(String ISBN) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(
                "select copies_amount from book natural join book_copies where ISBN = '"+ ISBN+"'"
        );
        rset.next();
        return rset.getInt(1);
    }
}
