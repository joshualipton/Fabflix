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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/_dashboard/api/employee-login")
public class EmployeeLoginServlet extends HttpServlet {
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
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        JsonObject responseJsonObject = new JsonObject();
        PrintWriter out = response.getWriter();

        // Verify reCAPTCHA
        try {
            System.out.println("Hey");
            responseJsonObject.addProperty("status", "success");
            System.out.println("Hey1");
            responseJsonObject.addProperty("message", "success");
            System.out.println("Hey2");
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            System.out.println("Hey3");
        } catch (Exception e) {
            System.out.println("Hey4");
            responseJsonObject.addProperty("status", "fail");
            request.getServletContext().log("Login failed");
            responseJsonObject.addProperty("message", "recaptcha verification error");
            response.getWriter().write(responseJsonObject.toString());
            out.close();
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */


        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT * FROM employees\n" +
                    "WHERE email = ?\n" +
                    ";";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, username);

            // Perform the query
            ResultSet rs = statement.executeQuery();
            boolean success = false;
            if (rs.next()) {
                // Username exists:
                String encryptedPassword = rs.getString("password");
                success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                if (success) {
                    // Success
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                } else {
                    // Incorrect password
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Login failed");
                    responseJsonObject.addProperty("message", "incorrect password");
                }
                // set this user into the session


            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");

            }
        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonErrorObject = new JsonObject();
            jsonErrorObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonErrorObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            response.getWriter().write(responseJsonObject.toString());
            out.close();
        }
    }
}