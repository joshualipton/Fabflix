import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called moviesServlet, which maps to url "/api/movies"
@WebServlet(name = "moviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
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

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            String query = "SELECT m.title,\n" +
                    "        m.year,\n" +
                    "        m.director,\n" +
                    "        SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY gm.genreId SEPARATOR ', '), ', ', 3) AS genres,\n" +
                    "        SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT CONCAT(s.name, ' (', sm.starId, ')') \n" +
                    "                     ORDER BY star_count DESC, s.name ASC SEPARATOR ', '), ', ', 3) AS stars,\n" +
                    "        r.rating,\n" +
                    "        m.id\n" +
                    " FROM movies m\n" +
                    " LEFT JOIN ratings r ON m.id = r.movieId\n" +
                    "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId\n" +
                    "LEFT JOIN genres g ON gm.genreId = g.id\n" +
                    "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId\n" +
                    "LEFT JOIN (\n" +
                    "  SELECT sm.starId, COUNT(DISTINCT sm.movieId) AS star_count\n" +
                    "  FROM stars_in_movies sm\n" +
                    "  GROUP BY sm.starId\n" +
                    ") sc ON sm.starId = sc.starId\n" +
                    "LEFT JOIN stars s ON sm.starId = s.id" +
                    " GROUP BY m.id, r.rating\n" +
                    "ORDER BY r.rating DESC\n" +
                    "LIMIT 20;\n";

            // Perform the query
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genres = rs.getString("genres");
                String movie_stars = rs.getString("stars");
                String movie_rating = rs.getString("rating");
                String movie_id = rs.getString("id");


                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genres", movie_genres);
                jsonObject.addProperty("movie_stars", movie_stars);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_id", movie_id);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
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

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
