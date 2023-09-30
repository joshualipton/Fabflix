import com.google.gson.Gson;
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
import java.util.HashMap;


// Declaring a WebServlet called moviesSearchServlet, which maps to url "/api/movies/search"
@WebServlet(name = "MoviesSearchFulltextServlet", urlPatterns = "/api/search-fulltext")
public class MoviesSearchFulltextServlet extends HttpServlet {
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
        System.out.println("HELLO1");
        // Retrieve parameter id from url request.
        String searchTitle;

        System.out.println("HEY1");

        String search = request.getParameter("query");
        System.out.println(search);
        if (search == null || search.trim().isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            response.getWriter().write(jsonArray.toString());
            return;
        }
        System.out.println("HEY2");

        String[] words = search.split("\\s+");
        StringBuilder queryBuilder = new StringBuilder();
        for (String word : words) {
            queryBuilder.append("+").append(word).append("* ");
        }

        String queryParams = queryBuilder.toString().trim();

        Integer limit;
        try {
            limit = Integer.valueOf(request.getParameter("limit"));
        } catch (Exception e) {
            limit = 10;
        }

        System.out.println(search);
        System.out.println(queryParams);

        Integer offset;
        try {
            offset = Integer.valueOf(request.getParameter("offset"));
        } catch (Exception e) {
            offset = 0;
        }
        System.out.println("HEY7");
        // The log message can be found in localhost log

        String sortString = "";
        Integer sort = 0;

        try {
            sort = Integer.valueOf(request.getParameter("sort"));
            System.out.println(sort);
        } catch (Exception e) {
            sortString = "ORDER BY r.rating DESC, m.title ASC\n";
        }

        if (sort == 0) {
            sortString = "ORDER BY r.rating DESC, m.title ASC\n";
        } else if (sort == 1) {
            sortString = "ORDER BY r.rating DESC, m.title DESC\n";
        } else if (sort == 2) {
            sortString = "ORDER BY r.rating ASC, m.title ASC\n";
        } else if (sort == 3) {
            sortString = "ORDER BY r.rating ASC, m.title DESC\n";
        } else if (sort == 4) {
            sortString = "ORDER BY m.title DESC, r.rating DESC\n";
        } else if (sort == 5) {
            sortString = "ORDER BY m.title DESC, r.rating ASC\n";
        } else if (sort == 6) {
            sortString = "ORDER BY m.title ASC, r.rating DESC\n";
        } else if (sort == 7) {
            sortString = "ORDER BY m.title ASC, r.rating ASC\n";
        }

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("HELLO3");
            String query;

            query = "SELECT\n" +
                    "    m.title,\n" +
                    "    m.year,\n" +
                    "    m.director,\n" +
                    "    SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY gm.genreId SEPARATOR ', '), ', ', 3) AS genres,\n" +
                    "    SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT CONCAT(s.name, ' (', sm.starId, ')') ORDER BY star_count DESC, s.name ASC SEPARATOR ', '), ', ', 3) AS stars,\n" +
                    "    r.rating,\n" +
                    "    m.id\n" +
                    "FROM\n" +
                    "    movies m\n" +
                    "    LEFT JOIN ratings r ON m.id = r.movieId\n" +
                    "    LEFT JOIN genres_in_movies gm ON m.id = gm.movieId\n" +
                    "    LEFT JOIN genres g ON gm.genreId = g.id\n" +
                    "    LEFT JOIN stars_in_movies sm ON m.id = sm.movieId\n" +
                    "    LEFT JOIN (\n" +
                    "        SELECT\n" +
                    "            sm.starId,\n" +
                    "            COUNT(DISTINCT sm.movieId) AS star_count\n" +
                    "        FROM\n" +
                    "            stars_in_movies sm\n" +
                    "        GROUP BY\n" +
                    "            sm.starId\n" +
                    "    ) sc ON sm.starId = sc.starId\n" +
                    "    LEFT JOIN stars s ON sm.starId = s.id\n" +
                    "WHERE\n" +
                    "    (\n" +
                    "        MATCH (m.title) AGAINST (? IN BOOLEAN MODE)\n" +
                    "    )\n" +
                    "GROUP BY\n" +
                    "    m.id,\n" +
                    "    r.rating " +
                    "LIMIT 20";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, queryParams);
                System.out.println("HELLO4");


            // Perform the query
            System.out.println(statement);
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
            System.out.println(jsonArray);
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            System.out.println(e.getMessage());
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
