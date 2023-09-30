import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "AddStarServlet", urlPatterns = "/_dashboard/api/add-star")
public class AddStarServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String starName = request.getParameter("star-name");
        Integer starBirthYear;
        try {
            starBirthYear = Integer.parseInt(request.getParameter("birth-year"));
        } catch (Exception e) {
            starBirthYear = null;
        }


        JsonObject responseJsonObject = new JsonObject();
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement statement = conn.prepareCall("{CALL add_star(?, ?, ?)}");
            statement.setString(1, starName);
            statement.setInt(2, starBirthYear);
            System.out.println("hey1");
            statement.registerOutParameter(3, Types.VARCHAR);
            System.out.println("hey2");
            statement.execute();
            System.out.println("hey3");
            String newId = statement.getString(3);
            System.out.println("hey4");
            responseJsonObject.addProperty("status", "success");
            System.out.println("hey5");
            responseJsonObject.addProperty("message", "Success! Star ID: " + newId);
            System.out.println("hey6");
        } catch (Exception e) {
            System.out.println("hey7");
            // Write error message JSON object to output
            JsonObject jsonErrorObject = new JsonObject();
            jsonErrorObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonErrorObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "could not add star");
            response.setStatus(500);
        } finally {
            response.getWriter().write(responseJsonObject.toString());
            out.close();
        }
    }
}