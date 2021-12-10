import com.sun.org.glassfish.external.statistics.annotations.Reset;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The type Jdbc.
 */
public class JDBC {
    private static Connection conn = null;
    private static final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    private static void register() throws SQLException {
        Statement stmt = conn.createStatement();
        String name, password, contactNumber;
        System.out.println("Please enter you information:");
        do {
            System.out.print("Username (<20 characters):");
            name = scanner.next();
            if(name.length()>20){
                System.out.println("Invalid input(>20 characters), please limit your username and try again.");
            }
            else if(checkUserExist(name)){
                System.out.println("Username already exist, please try another one.");
            }else{
                break;
            }
        }while (true);
        System.out.print("Password (<20 characters):");
        password = scanner.next();
        do {
            System.out.print("Phone Number (<20 digits):");
            contactNumber = scanner.next();
            if(!isNumeric(contactNumber) || contactNumber.length()>20){
                System.out.println("Please enter the phone number in correct format!");
            }else break;
        }while(true);
        long phoneNumber = Long.valueOf(contactNumber);
        stmt.execute(
                "insert into customer(name, password, contact_number) values ('"+name+"','"+password+"','"+phoneNumber+"');"
        );
        registerAddress(name);
        System.out.println("Registered successfully! Now you can login and use our system!");
    }


    public static void registerAddress(String username) throws SQLException {
        Statement stmt = conn.createStatement();
        String postal_code, street_no,street_name, city,province, country, userID;
        int streetNo;
        System.out.println("Please enter address information:");
        while (true){
            System.out.print("Postal Code:");
            postal_code = scanner.next();
            if(postal_code.length()>6){
                System.out.println("Invalid postal code,check your address info and try again.");
            }else{
                break;
            }
        }

        while (true){
            System.out.print("Street Number (<4 numbers):");
            street_no = scanner.next();
            if(street_no.length()>4){
                System.out.println("Invalid street number,check your address info and try again.");
            }else{
                streetNo = Integer.parseInt(street_no);
                break;
            }
        }

        while (true){
            System.out.print("Street Name (< 20 characters):");
            street_name = scanner.next();
            if(street_name.length()>20){
                System.out.println("Invalid street name, check your address info and try again.");
            }else{
                break;
            }
        }

        while (true){
            System.out.print("City (< 20 characters):");
            city = scanner.next();
            if(city.length()>20){
                System.out.println("Invalid city, check your address info and try again.");
            }else{
                break;
            }
        }

        while (true){
            System.out.print("Province (< 20 characters):");
            province = scanner.next();
            if(province.length()>20){
                System.out.println("Invalid province, check your address info and try again.");
            }else{
                break;
            }
        }

        while (true){
            System.out.print("Country (< 20 characters):");
            country = scanner.next();
            if(country.length()>20){
                System.out.println("Invalid country, check your address info and try again.");
            }else{
                break;
            }
        }

        HashMap<String,String> userInfo = getUserInfo(username);
        userID = userInfo.get("id");

        stmt.execute(
          "insert into address values('"+postal_code+"',"+streetNo+",'"+street_name+"','"+city+"','"+province+"','"+country+"');"
        );
        stmt.execute(
                "insert into register_address values('"+userID+"','"+postal_code+"')"
        );
    }

    private static void login() throws SQLException {

        String name, password;
        System.out.println("Please enter you information:");
        do {
            System.out.print("Username (<20 characters):");
            name = scanner.next();
            if(name.length()>20){
                System.out.println("Invalid input(>20 characters), please limit your username and try again.");
            }
            else if(checkUserExist(name)){
                break;
            }else{
                System.out.println("!!No such user found in the system, try to register an account and login!!");
                return;
            }
        }while (true);

        HashMap<String,String>userInfo = getUserInfo(name);
        System.out.print("Password (<20 characters):");
        password = scanner.next();
        if(password.equals(userInfo.get("password"))) {
            if (userInfo.get("contact_number") == null) {
                Owner owner = new Owner(userInfo.get("id"), userInfo.get("name"), userInfo.get("password"),conn);
                System.out.println("Create an owner");
                owner.operationMenu();
            } else {
                Customer customer = new Customer(userInfo.get("id"), userInfo.get("name"), userInfo.get("password"), userInfo.get("contact_number"),conn);
                System.out.println("Create a customer");
                customer.operationMenu();
            }
        }else{
            System.out.println("Login failed. Try login again.");
        }
    }

    private static boolean checkUserExist(String name) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(
                "select * from \"user\" where name = '"+ name+"'"
        );
        return rset.next();
    }

    private static HashMap<String,String> getUserInfo(String name) throws SQLException {
        HashMap<String, String> userInfo = new HashMap<>();
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(
                "select * from \"user\" where name = '"+ name+"'"
        );
        while(rset.next()){
            userInfo.put("id",rset.getString("id"));
            userInfo.put("name",rset.getString("name"));
            userInfo.put("password",rset.getString("password"));
            userInfo.put("contact_number", rset.getString("contact_number"));
        }
        return userInfo;
    }

    /**
     * Is numeric boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isNumeric(String str){
        return str != null && str.matches("[0-9]+");
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:8888/bookstore","postgres","si4848748"
            );

            System.out.println("******************Welcome to Look Inna Book online service system*******************");
            String inputOperation;
            label:
            while(true){
                System.out.println("Main page: Please enter your operation [Register / Login / Exit]:");
                inputOperation = scanner.next();
                switch (inputOperation) {
                    case "Register":
                        register();
                        break;
                    case "Login":
                        login();
                        break;
                    case "Exit":
                        break label;
                    default:
                        System.out.println("Error! Unrecognized operation, please try it again!");
                        break;
                }
            }
            System.out.println("******************Thanks for using Look Inna Book online service********************");
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
