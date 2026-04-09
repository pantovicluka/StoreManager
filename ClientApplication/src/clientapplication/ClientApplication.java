package clientapplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ClientApplication {

    private static final String BASE_URL = "http://localhost:8080/CentralSystem/resources/Central/";

    private static int loggedInId   = -1;
    private static int loggedInRole = -1;

    public static String sendRequest(int requestNumber, String method, String queryParams) {
        try {
            String urlString = BASE_URL + requestNumber;
            if (queryParams != null && !queryParams.isEmpty()) {
                urlString += "?" + queryParams;
            }

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method.toUpperCase());
            conn.setRequestProperty("Accept", "text/plain");

            int status = conn.getResponseCode();

            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    status == 200 ? conn.getInputStream() : conn.getErrorStream()
                )
            );

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            in.close();

            if (status == 200) {
                System.out.print(response.toString());
            } else {
                System.out.println("Error! Status code: " + status);
                System.out.println("Details: " + response.toString());
            }
            System.out.println("-------------------------------------------\n");

            conn.disconnect();
            return response.toString().trim();

        } catch (Exception e) {
            System.err.println("Communication error: " + e.getMessage());
            e.printStackTrace();
            return "ERROR";
        }
    }

    public static void login(Scanner sc) {
        while (loggedInId == -1) {
            System.out.println("====== LOGIN ======");
            System.out.print("Username: ");
            String userName = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();

            String params = "userName=" + userName + "&password=" + password;
            String response = sendRequest(1, "GET", params);

            if (response == null || response.isEmpty() || response.equals("ERROR")) {
                System.out.println("Login failed. Please check your credentials.");
            } else {
                try {
                    String[] parts = response.split(",");
                    loggedInId   = Integer.parseInt(parts[0].trim());
                    loggedInRole = Integer.parseInt(parts[1].trim());
                    System.out.println("Login successful!");
                } catch (Exception e) {
                    System.out.println("Server error: " + response.trim());
                }
            }
        }
    }

    // Request 1: Verify credentials
    public static String buildLoginParams(Scanner sc) {
        System.out.print("Username: ");
        String userName = readString(sc);
        System.out.print("Password: ");
        String password = readString(sc);
        return "userName=" + userName + "&password=" + password;
    }

    // Request 2: Create city
    public static String buildCreateCityParams(Scanner sc) throws UnsupportedEncodingException {
        System.out.print("City name: ");
        String name = readString(sc);
        return "name=" + java.net.URLEncoder.encode(name, "UTF-8");
    }

    // Request 3: Create user
    public static String buildCreateUserParams(Scanner sc) throws UnsupportedEncodingException {
        System.out.print("Username: ");
        String userName = readString(sc);
        System.out.print("Password: ");
        String password = readString(sc);
        System.out.print("First name: ");
        String firstName = readString(sc);
        System.out.print("Surname: ");
        String surname = readString(sc);
        System.out.print("Address: ");
        String address = readString(sc);
        System.out.print("Balance: ");
        int balance = readInt(sc);
        System.out.print("City ID: ");
        int cityId = readInt(sc);
        return "userName=" + userName
             + "&password=" + password
             + "&firstName=" + java.net.URLEncoder.encode(firstName, "UTF-8")
             + "&surname=" + java.net.URLEncoder.encode(surname, "UTF-8")
             + "&address=" + java.net.URLEncoder.encode(address, "UTF-8")
             + "&balance=" + balance
             + "&cityId=" + cityId;
    }

    // Request 4: Add funds
    public static String buildAddFundsParams(Scanner sc) {
        System.out.print("User ID: ");
        int userId = readInt(sc);
        System.out.print("Amount: ");
        int amount = readInt(sc);
        return "userId=" + userId + "&amount=" + amount;
    }

    // Request 5: Change address and city
    public static String buildChangeAddressParams(Scanner sc) throws UnsupportedEncodingException {
        System.out.print("User ID: ");
        int userId = readInt(sc);
        System.out.print("New address: ");
        String address = readString(sc);
        System.out.print("New city ID: ");
        int cityId = readInt(sc);
        return "userId=" + userId
             + "&address=" + java.net.URLEncoder.encode(address, "UTF-8")
             + "&cityId=" + cityId;
    }

    // Request 6: Create category
    public static String buildCreateCategoryParams(Scanner sc) throws UnsupportedEncodingException {
        System.out.print("Category name: ");
        String name = readString(sc);
        System.out.print("Parent category ID (0 for root): ");
        int parentId = readInt(sc);
        return "name=" + java.net.URLEncoder.encode(name, "UTF-8") + "&parentId=" + parentId;
    }

    // Request 7: Create item
    public static String buildCreateItemParams(Scanner sc) throws UnsupportedEncodingException {
        System.out.print("Item name: ");
        String name = readString(sc);
        System.out.print("Description: ");
        String description = readString(sc);
        System.out.print("Price: ");
        int price = readInt(sc);
        System.out.print("Discount (%): ");
        int discount = readInt(sc);
        System.out.print("Category ID: ");
        int categoryId = readInt(sc);
        int creatorId = loggedInId;
        return "name=" + java.net.URLEncoder.encode(name, "UTF-8")
             + "&description=" + java.net.URLEncoder.encode(description, "UTF-8")
             + "&price=" + price
             + "&discount=" + discount
             + "&categoryId=" + categoryId
             + "&creatorId=" + creatorId;
    }

    // Request 8: Change item price
    public static String buildChangePriceParams(Scanner sc) {
        System.out.print("Item ID: ");
        int itemId = readInt(sc);
        System.out.print("New price: ");
        int price = readInt(sc);
        return "itemId=" + itemId + "&price=" + price;
    }

    // Request 9: Set item discount
    public static String buildSetDiscountParams(Scanner sc) {
        System.out.print("Item ID: ");
        int itemId = readInt(sc);
        System.out.print("Discount (%): ");
        int discount = readInt(sc);
        return "itemId=" + itemId + "&discount=" + discount;
    }

    // Request 10: Add item to basket
    public static String buildAddToBasketParams(Scanner sc) {
        System.out.print("User ID: ");
        int userId = readInt(sc);
        System.out.print("Item ID: ");
        int itemId = readInt(sc);
        System.out.print("Quantity: ");
        int amount = readInt(sc);
        return "userId=" + userId + "&itemId=" + itemId + "&amount=" + amount;
    }

    // Request 11: Remove item from basket
    public static String buildRemoveFromBasketParams(Scanner sc) {
        System.out.print("User ID: ");
        int userId = readInt(sc);
        System.out.print("Item ID: ");
        int itemId = readInt(sc);
        System.out.print("Quantity: ");
        int amount = readInt(sc);
        return "userId=" + userId + "&itemId=" + itemId + "&amount=" + amount;
    }

    // Request 12: Add item to wish list
    public static String buildAddToWishListParams(Scanner sc) {
        System.out.print("User ID: ");
        int userId = readInt(sc);
        System.out.print("Item ID: ");
        int itemId = readInt(sc);
        return "userId=" + userId + "&itemId=" + itemId;
    }

    // Request 13: Remove item from wish list
    public static String buildRemoveFromWishListParams(Scanner sc) {
        System.out.print("User ID: ");
        int userId = readInt(sc);
        System.out.print("Item ID: ");
        int itemId = readInt(sc);
        return "userId=" + userId + "&itemId=" + itemId;
    }

    // Request 14: Checkout / payment
    public static String buildCheckoutParams(Scanner sc) {
        System.out.print("User ID: ");
        int userId = readInt(sc);
        return "userId=" + userId;
    }

    // Request 18: Get seller's items (uses logged-in user)
    public static String buildSellerItemsParams() {
        return "userId=" + loggedInId;
    }

    // Request 19: Get basket contents
    public static String buildBasketParams(Scanner sc) {
        System.out.print("User ID: ");
        int userId = readInt(sc);
        return "userId=" + userId;
    }

    // Request 20: Get wish list contents
    public static String buildWishListParams(Scanner sc) {
        System.out.print("User ID: ");
        int userId = readInt(sc);
        return "userId=" + userId;
    }

    // Request 21: Get user's orders
    public static String buildOrdersParams(Scanner sc) {
        System.out.print("User ID: ");
        int userId = readInt(sc);
        return "userId=" + userId;
    }

    public static int readInt(Scanner sc) {
        int number = sc.nextInt();
        sc.nextLine();
        return number;
    }

    public static String readString(Scanner sc) {
        return sc.nextLine();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        Scanner sc = new Scanner(System.in);
        login(sc);

        String params;

        while (true) {
            System.out.print("Enter request number: ");
            int requestNum = readInt(sc);

            if (loggedInRole != 1 && (requestNum == 2 || requestNum == 3 || requestNum == 4 || requestNum == 5 || requestNum == 15 || requestNum == 16)) {
                System.out.println("You do not have administrator privileges.");
                break;
            }

            switch (requestNum) {
                case 1:
                    params = buildLoginParams(sc);
                    String resp1 = sendRequest(1, "GET", params);
                    System.out.println(resp1.equals("ERROR") || resp1.isEmpty() ? "User does not exist." : "User exists.");
                    break;
                case 2:
                    params = buildCreateCityParams(sc);
                    String resp2 = sendRequest(2, "POST", params);
                    if (resp2.equals("ERROR") || resp2.isEmpty()) System.out.println("Error creating city.");
                    break;
                case 3:
                    params = buildCreateUserParams(sc);
                    String resp3 = sendRequest(3, "POST", params);
                    if (resp3.equals("ERROR") || resp3.isEmpty()) System.out.println("Error creating user.");
                    else System.out.println(resp3);
                    break;
                case 4:
                    params = buildAddFundsParams(sc);
                    String resp4 = sendRequest(4, "PUT", params);
                    if (resp4.equals("ERROR") || resp4.isEmpty()) System.out.println("Error adding funds.");
                    else System.out.println(resp4);
                    break;
                case 5:
                    params = buildChangeAddressParams(sc);
                    String resp5 = sendRequest(5, "PUT", params);
                    if (resp5.equals("ERROR") || resp5.isEmpty()) System.out.println("Error updating address.");
                    else System.out.println(resp5);
                    break;
                case 6:
                    params = buildCreateCategoryParams(sc);
                    String resp6 = sendRequest(6, "POST", params);
                    if (resp6.equals("ERROR") || resp6.isEmpty()) System.out.println("Error creating category.");
                    else System.out.println(resp6);
                    break;
                case 7:
                    params = buildCreateItemParams(sc);
                    String resp7 = sendRequest(7, "POST", params);
                    if (resp7.equals("ERROR") || resp7.isEmpty()) System.out.println("Error creating item.");
                    else System.out.println(resp7);
                    break;
                case 8:
                    params = buildChangePriceParams(sc);
                    String resp8 = sendRequest(8, "PUT", params);
                    if (resp8.equals("ERROR") || resp8.isEmpty()) System.out.println("Error updating price.");
                    else System.out.println(resp8);
                    break;
                case 9:
                    params = buildSetDiscountParams(sc);
                    String resp9 = sendRequest(9, "PUT", params);
                    if (resp9.equals("ERROR") || resp9.isEmpty()) System.out.println("Error setting discount.");
                    else System.out.println(resp9);
                    break;
                case 10:
                    params = buildAddToBasketParams(sc);
                    String resp10 = sendRequest(10, "POST", params);
                    if (resp10.equals("ERROR") || resp10.isEmpty()) System.out.println("Error adding item to basket.");
                    else System.out.println(resp10);
                    break;
                case 11:
                    params = buildRemoveFromBasketParams(sc);
                    String resp11 = sendRequest(11, "DELETE", params);
                    if (resp11.equals("ERROR") || resp11.isEmpty()) System.out.println("Error removing item from basket.");
                    else System.out.println(resp11);
                    break;
                case 12:
                    params = buildAddToWishListParams(sc);
                    String resp12 = sendRequest(12, "POST", params);
                    if (resp12.equals("ERROR") || resp12.isEmpty()) System.out.println("Error adding item to wish list.");
                    else System.out.println(resp12);
                    break;
                case 13:
                    params = buildRemoveFromWishListParams(sc);
                    String resp13 = sendRequest(13, "DELETE", params);
                    if (resp13.equals("ERROR") || resp13.isEmpty()) System.out.println("Error removing item from wish list.");
                    else System.out.println(resp13);
                    break;
                case 14:
                    params = buildCheckoutParams(sc);
                    String resp14 = sendRequest(14, "POST", params);
                    if (resp14.equals("ERROR") || resp14.isEmpty()) System.out.println("Error processing payment.");
                    else System.out.println(resp14);
                    break;
                case 15:
                    String resp15 = sendRequest(15, "GET", "");
                    if (resp15.equals("ERROR") || resp15.isEmpty()) System.out.println("Error fetching cities.");
                    else System.out.println(resp15);
                    break;
                case 16:
                    String resp16 = sendRequest(16, "GET", "");
                    if (resp16.equals("ERROR") || resp16.isEmpty()) System.out.println("Error fetching users.");
                    else System.out.println(resp16);
                    break;
                case 17:
                    String resp17 = sendRequest(17, "GET", "");
                    if (resp17.equals("ERROR") || resp17.isEmpty()) System.out.println("Error fetching categories.");
                    else System.out.println(resp17);
                    break;
                case 18:
                    params = buildSellerItemsParams();
                    String resp18 = sendRequest(18, "GET", params);
                    if (resp18.equals("ERROR") || resp18.isEmpty()) System.out.println("Error fetching your items.");
                    else System.out.println(resp18);
                    break;
                case 19:
                    params = buildBasketParams(sc);
                    String resp19 = sendRequest(19, "GET", params);
                    if (resp19.equals("ERROR") || resp19.isEmpty()) System.out.println("Error fetching basket.");
                    else System.out.println(resp19);
                    break;
                case 20:
                    params = buildWishListParams(sc);
                    String resp20 = sendRequest(20, "GET", params);
                    if (resp20.equals("ERROR") || resp20.isEmpty()) System.out.println("Error fetching wish list.");
                    else System.out.println(resp20);
                    break;
                case 21:
                    params = buildOrdersParams(sc);
                    String resp21 = sendRequest(21, "GET", params);
                    if (resp21.equals("ERROR") || resp21.isEmpty()) System.out.println("Error fetching orders.");
                    else System.out.println(resp21);
                    break;
                case 22:
                    String resp22 = sendRequest(22, "GET", "");
                    if (resp22.equals("ERROR") || resp22.isEmpty()) System.out.println("Error fetching all orders.");
                    else System.out.println(resp22);
                    break;
                case 23:
                    String resp23 = sendRequest(23, "GET", "");
                    if (resp23.equals("ERROR") || resp23.isEmpty()) System.out.println("Error fetching transactions.");
                    else System.out.println(resp23);
                    break;
                case 0:
                    System.out.println("Exiting.");
                    return;
                default:
                    System.out.println("Unknown request number.");
            }
        }
    }
}
