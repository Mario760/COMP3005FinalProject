import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The type Customer.
 * Development customer's interfaces and operations
 */
public class Customer {
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private HashMap<String,Integer> basket = new HashMap<>();
    private String id, name, password, phoneNumber;
    private Connection conn;

    /**
     * Instantiates a new Customer.
     *
     * @param id          the id
     * @param name        the name
     * @param password    the password
     * @param phoneNumber the phone number
     * @param conn        the conn
     */
    public Customer(String id, String name, String password, String phoneNumber, Connection conn){
        this.id = id;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.conn = conn;
    }


    /**
     * Operation menu.
     *
     * @throws SQLException the sql exception
     */
    public void operationMenu() throws SQLException {
        while(true) {
            System.out.println("Customer page: Please enter your operation [Search books / Browse books / Select books / Check out / Tracking order / Log out]:");
            String inputOperation = scanner.nextLine();
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


    /**
     * Search books function, allow user to search a book through ISBN, name, author, genre
     * @throws SQLException
     */
    private void searchBooks() throws SQLException {
        boolean isISBN = false, isName = false, isAuthor = false, isGenre = false;
        String filter, ISBN, bookName, author, genre;
        Statement stmt = conn.createStatement();
        String sql = "select * from book natural join book_copies where ";

        //get filter input from user
        while(true) {
            System.out.println("Which filter do you want to add to search books? (Type \"END\" to stop adding filter) [ISBN / name / author / genre]:");
            filter = scanner.nextLine();

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
                        ISBN = scanner.nextLine();
                        sql += "ISBN = '" + ISBN + "' ";
                        isISBN = true;
                    }
                    break;
                case "name":
                    if(isName){
                        System.out.println("Sorry you have entered name already.");
                    }else {
                        System.out.println("Please enter your desired book name: ");
                        bookName = scanner.nextLine();
                        sql += "name = '" + bookName + "' ";
                        isISBN = true;
                    }
                    break;
                case "author":
                    if(isAuthor){
                        System.out.println("Sorry you have entered author already.");
                    }else {
                        System.out.println("Please enter your desired author name: ");
                        author = scanner.nextLine();
                        sql += "author = '" + author + "' ";
                        isAuthor = true;
                    }
                    break;
                case "genre":
                    if(isGenre){
                        System.out.println("Sorry you have entered genre already.");
                    }else {
                        System.out.println("Please enter your desired genre: ");
                        genre = scanner.nextLine();
                        sql += "genre = '" + genre + "' ";
                        isGenre = true;
                    }
                    break;
                default:
                    System.out.println("Unrecognized filter, try it again");
                    break;
            }
        }

        //Search the related book in the database
        ResultSet rset = stmt.executeQuery(sql);
        if(!rset.next()){
            System.out.println("Cannot find the related book. Check your input and try it again.");
            return;
        }else{
            rset = stmt.executeQuery(sql);
        }

        //print out the search result
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


    /**
     * Select books into the basket
     * @throws SQLException
     */
    private void selectBooks() throws SQLException {
        String ISBN,copies, basketInfo="";
        Statement stmt = conn.createStatement();
        //get ISBN from user to add books into the basket
        while(true) {
            System.out.println("Enter the book's ISBN that you want to select (type END to finisher selection): ");
            ISBN = scanner.nextLine();
            if (ISBN.equals("END")) {
                break;
            }
            if(JDBC.isNumeric(ISBN)) {
                if(basket.containsKey(ISBN)){
                    int totalCopies = getCopiesAmount(ISBN);
                    while (true) {
                        System.out.println("There are " + totalCopies + " in stock. How many copies do you want?: ");
                        copies = scanner.nextLine();
                        if (JDBC.isNumeric(copies)) {
                            if (Integer.parseInt(copies) <= totalCopies) {
                                break;
                            } else if (Integer.parseInt(copies) > totalCopies) {
                                System.out.println("There are not as many copies as you want. Try it again :P");
                            }
                        } else {
                            System.out.println("Error input format, try it again!");
                        }
                    }
                    basket.put(ISBN, Integer.parseInt(copies)+basket.get(ISBN));
                    System.out.println("The book(s) are added to your basket. Waiting for checking out ^V^");
                }else {
                    if (checkBookExistISBN(ISBN)) {
                        int totalCopies = getCopiesAmount(ISBN);
                        while (true) {
                            System.out.println("There are " + totalCopies + " in stock. How many copies do you want?: ");
                            copies = scanner.nextLine();
                            if (JDBC.isNumeric(copies)) {
                                if (Integer.parseInt(copies) <= totalCopies) {
                                    break;
                                } else if (Integer.parseInt(copies) > totalCopies) {
                                    System.out.println("There are not as many copies as you want. Try it again :P");
                                }
                            } else {
                                System.out.println("Error input format, try it again!");
                            }
                        }
                        basket.put(ISBN, Integer.parseInt(copies));
                        System.out.println("The book(s) are added to your basket. Waiting for checking out ^V^");
                    } else {
                        System.out.println("Cannot find the related book");
                    }
                }
            }else{
                System.out.println("Please double check your ISBN input.");
            }

            //print out basket information
            System.out.println("---------------------------------------------------------------------------------------------");
            System.out.println("The books that you selected in your basket:");
            System.out.println("ISBN\t\t\t\tname\t\t\t\t\tauthor\t\t\t\tgenre\t\t\t\ttotal pages\t\t\t\t price\t\t\tquantities");
            printBasketInfo();
            System.out.println("---------------------------------------------------------------------------------------------");
        }
    }


    /**
     * Procedure to check out the books in the basket.
     * @throws SQLException
     */
    private void checkOut() throws SQLException {
        String biller_name;
        int billing_price, orderNumber = 0;
        Statement stmt = conn.createStatement();
        if(basket.isEmpty()){
            System.out.println("You haven't select any books to your basket.");
            return;
        }

        while(true) {
            System.out.println("Please enter the biller's name (<20 characters):");
            biller_name = scanner.nextLine();
            if(biller_name.length()<=20){
                break;
            }
            System.out.println("The biller's name is too long. Try again later.");
        }

        billing_price = getBillingPrice();
        stmt.execute(
                "insert into \"order\" (biller_name, billing_price) values ('"+biller_name+"','"+billing_price+"')"
        );
        ResultSet rset = stmt.executeQuery(
                "select order_number from \"order\" where biller_name = '"+biller_name+"'"
        );

        while(rset.next()) {
            if(rset.isLast()) {
                orderNumber = rset.getInt(1);
            }
        }

        for(String ISBN : basket.keySet()){
            stmt.execute(
                    "insert into order_book values ('"+orderNumber+"','"+ISBN+"','"+basket.get(ISBN)+"')"
            );
        }

        stmt.execute(
                "insert into tracking values ('"+orderNumber+"','Progress')"
        );

        HashMap<String,String> addressInfo = getRegisteredAddressInfo();
        System.out.println("Here is your registered address: ");
        System.out.println("Postal Code: "+addressInfo.get("postal_code"));
        System.out.println("\t\tStreet :"+addressInfo.get("street_no")+"  "+addressInfo.get("street_name")+",  "+addressInfo.get("city")+",  "+addressInfo.get("province")+",  "+addressInfo.get("country"));

        System.out.println("Shipping address same as the register address? [Yes / No]");

        String sameAddress = scanner.nextLine();;
        while(true) {
            if (sameAddress.equals("No")) {
                newShippingAddress(orderNumber);
                break;
            } else if (sameAddress.equals("Yes")) {
                stmt.execute(
                        "insert into shipping_address values(" + orderNumber + ",'" + addressInfo.get("postal_code") + "');"
                );
                break;
            } else {
                System.out.println("Error input, check your response and type again.");
            }
        }
        stmt.execute(
                "insert into place_order values('"+this.id+"','"+orderNumber+"');"
        );
        updateInstockBooks();
        System.out.println("You have been checked out successfully, your order number is: "+orderNumber+", you can use this number to track order.");
        basket.clear();
    }

    private void printBasketInfo() throws SQLException{
        Statement stmt = conn.createStatement();
        ResultSet rset;
        for(String ISBN : basket.keySet()){
            rset = stmt.executeQuery(
                    "select * from book where ISBN = '"+ISBN+"'"
            );
            rset.next();
            System.out.print(rset.getString("ISBN")+"\t\t");
            System.out.print(rset.getString("name")+"\t\t\t");
            System.out.print(rset.getString("author")+"\t\t");
            System.out.print(rset.getString("genre")+"\t\t\t\t");
            System.out.print(rset.getString("number_of_pages")+"\t\t\t\t\t\t");
            System.out.print(rset.getString("price")+"\t\t\t\t");
            System.out.print(basket.get(ISBN)+"\n");
        }
    }

    private void updateInstockBooks() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rset;
        for(String ISBN : basket.keySet()) {
            rset = stmt.executeQuery(
                    "select copies_amount from book_copies where ISBN = '"+ISBN+"'"
            );
            rset.next();
            int copiesNumber = rset.getInt(1);
            stmt.execute(
                    "update book_copies set copies_amount ="+(copiesNumber-basket.get(ISBN))+" where ISBN = '"+ISBN+"'"
            );
        }
    }

    private void trackOrder() throws SQLException {
        Statement stmt = conn.createStatement();
        HashMap<String,String> orderNumberList = new HashMap<>();
        int date = new Date(System.currentTimeMillis()).getDate();
        String order_number;
        ResultSet rset;
        System.out.println("According to the system, you have the orders below: ");
        rset = stmt.executeQuery(
                "select * from place_order natural join tracking natural join \"order\""
        );

        while(rset.next()){
            System.out.print("Order Number: "+rset.getInt("order_number")+"--------");
            System.out.println("Purcahsed Date: "+rset.getDate("date"));
            if((date - rset.getDate("date").getDate()) == 1){
                stmt.execute(
                        "update tracking set shipping_status = 'Shipped' where order_number = '"+rset.getInt("order_number")+"'"
                );
            }else if((date - rset.getDate("date").getDate()) > 1){
                stmt.execute(
                        "update tracking set shipping_status = 'Delivered' where order_number = '"+rset.getInt("order_number")+"'"
                );
            }
            orderNumberList.put(String.valueOf(rset.getInt("order_number")),rset.getString("shipping_status"));
        }

        while(true) {
            System.out.println("Please enter the order number that you want to track:");
            order_number = scanner.nextLine();
            if (orderNumberList.containsKey(order_number)) break;
            System.out.println("Order number not exit, please check and enter again.");
        }

        System.out.println("The status of your order is: "+orderNumberList.get(order_number));

    }

    private boolean checkBookExistISBN(String ISBN) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(
                "select * from book where ISBN = '"+ ISBN+"'"
        );
        return rset.next();
    }

    private int getBillingPrice() throws SQLException {
        Statement stmt = conn.createStatement();
        int totalPrice=0;
        for(String ISBN : basket.keySet()){
            totalPrice+=getBookPrice(ISBN,basket.get(ISBN));
        }
        return totalPrice;
    }

    private int getBookPrice(String ISBN, int copies) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(
                "select price from book where ISBN = '"+ ISBN+"'"
        );
        rset.next();
        return rset.getInt(1)*copies;
    }


    private void newShippingAddress(int orderNumber) throws SQLException {
        Statement stmt = conn.createStatement();
        String postal_code, street_no,street_name, city,province, country;
        int streetNo;
        System.out.println("Please enter address information:");
        while (true){
            System.out.print("Postal Code:");
            postal_code = scanner.nextLine();
            if(postal_code.length()>6){
                System.out.println("Invalid postal code,check your address info and try again.");
            }else{
                break;
            }
        }

        while (true){
            System.out.print("Street Number (<4 numbers):");
            street_no = scanner.nextLine();
            if(street_no.length()>4){
                System.out.println("Invalid street number,check your address info and try again.");
            }else{
                streetNo = Integer.parseInt(street_no);
                break;
            }
        }

        while (true){
            System.out.print("Street Name (< 20 characters):");
            street_name = scanner.nextLine();
            if(street_name.length()>20){
                System.out.println("Invalid street name, check your address info and try again.");
            }else{
                break;
            }
        }

        while (true){
            System.out.print("City (< 20 characters):");
            city = scanner.nextLine();
            if(city.length()>20){
                System.out.println("Invalid city, check your address info and try again.");
            }else{
                break;
            }
        }

        while (true){
            System.out.print("Province (< 20 characters):");
            province = scanner.nextLine();
            if(province.length()>20){
                System.out.println("Invalid province, check your address info and try again.");
            }else{
                break;
            }
        }

        while (true){
            System.out.print("Country (< 20 characters):");
            country = scanner.nextLine();
            if(country.length()>20){
                System.out.println("Invalid country, check your address info and try again.");
            }else{
                break;
            }
        }

        stmt.execute(
                "insert into address values('"+postal_code+"',"+streetNo+",'"+street_name+"','"+city+"','"+province+"','"+country+"');"
        );
        stmt.execute(
                "insert into shipping_address values('"+orderNumber+"',"+postal_code+"');"
        );
    }

    private HashMap<String,String> getRegisteredAddressInfo() throws SQLException {
        HashMap<String, String> addressInfo = new HashMap<>();
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(
                "select * from \"user\" natural join register_address natural join address where customer_ID = '"+ id+"'"
        );
        while(rset.next()){
            addressInfo.put("postal_code",rset.getString("postal_code"));
            addressInfo.put("street_no",rset.getString("street_no"));
            addressInfo.put("street_name",rset.getString("street_name"));
            addressInfo.put("city", rset.getString("city"));
            addressInfo.put("province", rset.getString("province"));
            addressInfo.put("country", rset.getString("country"));
        }
        return addressInfo;
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
