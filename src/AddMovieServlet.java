import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/_dashboard/api/add-movie")
public class AddMovieServlet extends HttpServlet {
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
        String movieTitle = request.getParameter("movie-title");
        int movieYear = Integer.parseInt(request.getParameter("movie-year"));
        String director = request.getParameter("director");
        String starName = request.getParameter("star-name");
        String genre = request.getParameter("genre");
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
            CallableStatement statement = conn.prepareCall("{CALL add_movie(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            statement.setString(1, movieTitle);
            statement.setInt(2, movieYear);
            statement.setString(3, director);
            statement.setString(4, starName);
            try {
                statement.setInt(5, starBirthYear);
            } catch (Exception e) {
                statement.setObject(5, starBirthYear);
            }
            statement.setString(6, genre);
            System.out.println("hey1");
            statement.registerOutParameter(7, Types.VARCHAR);
            statement.registerOutParameter(8, Types.VARCHAR);
            statement.registerOutParameter(9, Types.VARCHAR);
            System.out.println("hey2");
            statement.execute();
            System.out.println("hey3");
            String newIdMovie = statement.getString(7);
            String newIdStar = statement.getString(8);
            String newIdGenre = statement.getString(9);
            System.out.println("hey4");
            responseJsonObject.addProperty("status", "success");
            System.out.println("hey5");
            if (newIdMovie != null) {
                responseJsonObject.addProperty("message", "Success! Movie ID: " + newIdMovie + " Star ID: " + newIdStar + " Genre ID: " + newIdGenre);
            } else {
                responseJsonObject.addProperty("message", "Duplicate Movie!");
            }
            System.out.println("hey6");
        } catch (Exception e) {
            System.out.println("hey7");
            System.out.println(e.getMessage());
            // Write error message JSON object to output
            JsonObject jsonErrorObject = new JsonObject();
            jsonErrorObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonErrorObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "could not add movie");
            response.setStatus(500);
        } finally {
            response.getWriter().write(responseJsonObject.toString());
            out.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }
}