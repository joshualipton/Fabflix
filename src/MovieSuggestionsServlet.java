import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebServlet("/api/movie-suggestion")
public class MovieSuggestionsServlet extends HttpServlet {
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

    /*
     *
     * Match the query against superheroes and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String search = request.getParameter("query");
            if (search == null || search.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            String[] words = search.split("\\s+");
            StringBuilder queryBuilder = new StringBuilder();
            for (String word : words) {
                queryBuilder.append("+").append(word).append("* ");
            }

            String queryParams = queryBuilder.toString().trim();

            // return the empty json array if query is null or empty


            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars
            PrintWriter out = response.getWriter();

            try (Connection conn = dataSource.getConnection()) {

                // Declare our statement
                String query = "SELECT * FROM movies WHERE MATCH (title) AGAINST (? IN BOOLEAN MODE) LIMIT 10;";

                // Perform the query
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, queryParams);
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    String movieId = rs.getString("id");
                    String movieTitle = rs.getString("title");

                    jsonArray.add(generateJsonObject(movieId, movieTitle));
                }
                response.getWriter().write(jsonArray.toString());
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
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "heroID": 11 }
     * }
     *
     */
    private static JsonObject generateJsonObject(String movieId, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("id", movieId);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }



    }