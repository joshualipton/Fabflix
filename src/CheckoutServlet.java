import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

/**
 * This CartServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/checkout.
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String cardNumber = request.getParameter("cardNumber");
        String expDate = request.getParameter("expDate");
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM creditcards WHERE id=? AND firstName=? AND lastName=? AND DATE(expiration)=?;";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, cardNumber);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, expDate);
            System.out.println(statement);
            ResultSet rs = statement.executeQuery();
            System.out.println("HEY 1");
            if (rs.next()) {
                HttpSession session = request.getSession();
                System.out.println("HEY 2");
                HashMap<String, Integer> previousItemsMap = (HashMap<String, Integer>) session.getAttribute("previousItemsMap");
                JsonObject previousItemsJson = new JsonObject();

                for (HashMap.Entry<String, Integer> entry : previousItemsMap.entrySet()) {
                    previousItemsJson.addProperty(entry.getKey(), entry.getValue());
                }
                System.out.println("HELLO");
                System.out.println(previousItemsJson);

                if (previousItemsJson == null || previousItemsJson.toString().equals("{}")) {
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Your cart is empty");
                } else {
                    int id = ((User)session.getAttribute("user")).getId();
                    System.out.println("HEY 3");

                    for (String key : previousItemsJson.keySet()) {
                        System.out.println("HEY 4");
                        String movieId = key.split(":")[0];
                        System.out.println("HEY 5");
                        System.out.println("Movie ID: " + movieId);
                        query = "INSERT INTO sales(customerId, movieId, saleDate) VALUES (?, ?, CURDATE());";
                        PreparedStatement statement2 = conn.prepareStatement(query);
                        statement2.setInt(1, id);
                        statement2.setString(2, movieId);
                        System.out.println(statement2);
                        int rowsAffected = statement2.executeUpdate();
                        System.out.println(rowsAffected);
                    }

                }
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            } else {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Credit card information not found");
            }
        } catch (Exception e) {
            // Write error message JSON object to output
            System.out.println(e.getMessage());

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            response.getWriter().write(responseJsonObject.toString());
            out.close();
        }
    }
}