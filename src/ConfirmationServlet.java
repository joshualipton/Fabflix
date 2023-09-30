import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            String query = " SELECT * FROM sales\n" +
                    " WHERE customerId = ?\n" +
                    " AND saleDate = curDate()\n" +
                    " ORDER BY id DESC\n" +
                    " LIMIT ?;";

            // Perform the query
            PreparedStatement statement = conn.prepareStatement(query);

            HttpSession session = request.getSession();
            int id = ((User)session.getAttribute("user")).getId();

            HashMap<String, Integer> previousItemsMap = (HashMap<String, Integer>) session.getAttribute("previousItemsMap");
            JsonObject previousItemsJson = new JsonObject();

            Integer numSales = 0;
            for (HashMap.Entry<String, Integer> entry : previousItemsMap.entrySet()) {
                previousItemsJson.addProperty(entry.getKey(), entry.getValue());
                numSales += entry.getValue();
            }
            System.out.println(previousItemsJson);

//            for (String key : previousItemsJson.keySet()) {
//                System.out.println("HEY 4");
//                String movieId = key.split(":")[0];
//            }

            statement.setInt(1, id);
            statement.setInt(2, numSales);
            System.out.println(statement);
            ResultSet rs = statement.executeQuery();

            JsonObject responseJsonObject = new JsonObject();

            // Iterate through each row of rs
            while (rs.next()) {
                Integer sale_id = rs.getInt("id");
                String movieId = rs.getString("movieId");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("sale_id", sale_id);

                responseJsonObject.add(movieId, jsonObject);
            }
            rs.close();
            statement.close();

            JsonObject previousItemsJsonObject = new JsonObject();
            for (String key : previousItemsMap.keySet()) {
                previousItemsJsonObject.addProperty(key, previousItemsMap.get(key));
            }
            responseJsonObject.add("previousItems", previousItemsJsonObject);

            // Write JSON string to output
            out.write(responseJsonObject.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
