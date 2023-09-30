import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;


// Declaring a WebServlet called moviesSearchServlet, which maps to url "/api/movies/search"
@WebServlet(name = "MoviesSearchServlet", urlPatterns = "/api/search")
public class MoviesSearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;
    private String contextPath;

    public void init(ServletConfig config) {
        try {
            super.init(config);
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            System.out.println("HERE 11");
            ServletContext servletContext = config.getServletContext();
            contextPath = servletContext.getRealPath("/");
            System.out.println(getServletContext());
            System.out.println(servletContext);
            System.out.println("Servlet context path: " + servletContext.getContextPath());
            System.out.println("Servlet context path: " + servletContext.getRealPath("/"));
            System.out.println("Servlet context path: " + servletContext.getRealPath(""));
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long startTimeTS = System.nanoTime();

        response.setContentType("application/json"); // Response mime type
        System.out.println("HELLO1");
        // Retrieve parameter id from url request.
        String searchTitle;
        Integer searchYear;
        String searchDirector;
        String searchStar;
        String prefix = "";
        String genre = "";

        System.out.println("HEY1");

        try {
            prefix = request.getParameter("prefix");
        } catch (Exception e) {
            prefix = "";
        }

        System.out.println("HEY1.5");

        try {
            searchTitle = request.getParameter("title");
        } catch (Exception e) {
            searchTitle = "";
        }

        try {
            genre = request.getParameter("genre");
        } catch (Exception e) {
            genre = "";
        }

        System.out.println("HEY2");

        try {
            searchYear = Integer.valueOf(request.getParameter("year"));
        } catch (Exception e) {
            searchYear = null;
        }
        System.out.println("HEY3");

        try {
            searchDirector = request.getParameter("director");
        } catch (Exception e) {
            searchDirector = "";
        }
        System.out.println("HEY4");

        try {
            searchStar = request.getParameter("star");
        } catch (Exception e) {
            searchStar = "";
        }
        System.out.println("HEY5");

        Integer limit;
        try {
            limit = Integer.valueOf(request.getParameter("limit"));
        } catch (Exception e) {
            limit = 10;
        }

        System.out.println("HEY6");

        Integer offset;
        try {
            offset = Integer.valueOf(request.getParameter("offset"));
        } catch (Exception e) {
            offset = 0;
        }
        System.out.println("HEY7");
        // The log message can be found in localhost log
        request.getServletContext().log("getting searchTitle: " + searchTitle);

        String sortString = "";
        Integer sort = 0;

        try {
            sort = Integer.valueOf(request.getParameter("sort"));
            System.out.println(sort);
        } catch (Exception e) {
            sortString = "ORDER BY r.rating DESC, m.title ASC\n";
        }

        System.out.println("HEY8");
        HttpSession session = request.getSession();

        System.out.println("HEY9");

        String useSession;
        try {
            useSession = request.getParameter("useSession");
            if (useSession == null) {
                useSession = "false";
            }
            System.out.println(useSession);
            System.out.println("HEY10");
        } catch (Exception e) {
            useSession = "false";
            System.out.println("HEY11");
        }
        HashMap<String, Object> searchParams = new HashMap<>();
        if (useSession.equals("true")) {
            System.out.println("HEY12");
            HashMap<String, Object> searchParamsMap = (HashMap<String, Object>) session.getAttribute("previousSearchParams");
            searchTitle = (String) searchParamsMap.get("searchTitle");
            searchYear = (Integer) searchParamsMap.get("searchYear");
            searchDirector = (String) searchParamsMap.get("searchDirector");
            searchStar = (String) searchParamsMap.get("searchStar");
            prefix = (String) searchParamsMap.get("prefix");
            genre = (String) searchParamsMap.get("genre");
            limit = (Integer) searchParamsMap.get("limit");
            offset = (Integer) searchParamsMap.get("offset");
            sort = (Integer) searchParamsMap.get("sort");
            System.out.println("HEY13");
        }
        System.out.println("HEY14");
        searchParams.put("searchTitle", searchTitle);
        searchParams.put("searchYear", searchYear);
        searchParams.put("searchDirector", searchDirector);
        searchParams.put("searchStar", searchStar);
        searchParams.put("prefix", prefix);
        searchParams.put("genre", genre);
        searchParams.put("limit", limit);
        searchParams.put("offset", offset);
        searchParams.put("sort", sort);
        session.setAttribute("previousSearchParams", searchParams);
        System.out.println("HEY15");
        System.out.println(searchParams);


        System.out.println("HELLO2");

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
        long endTimeTJ = 0;
        long startTimeTJ = 0;
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("HELLO3");
            String query;
            System.out.println("THIS0");
            if (prefix.equals("*")) {
                query = "SELECT m.title,\n" +
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
                        "LEFT JOIN stars s ON sm.starId = s.id\n" +
                        "WHERE m.title regexp '^[^a-zA-Z0-9]'\n" +
                        " GROUP BY m.id, r.rating\n" +
                        sortString +
                        "LIMIT ?\n" +
                        "OFFSET ?\n" +
                        ";";
            } else if (!prefix.equals("")) {
                query = "SELECT m.title,\n" +
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
                        "LEFT JOIN stars s ON sm.starId = s.id\n" +
                        "WHERE (LOWER(m.title) LIKE LOWER(COALESCE(?, LOWER(m.title))))\n" +
                        " GROUP BY m.id, r.rating\n" +
                        sortString +
                        "LIMIT ?\n" +
                        "OFFSET ?;";
            } else if (!genre.equals("")) {
                query = "SELECT m.title,\n" +
                        "       m.year,\n" +
                        "       m.director,\n" +
                        "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY gm.genreId SEPARATOR ', '), ', ', 3) AS genres,\n" +
                        "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT CONCAT(s.name, ' (', sm.starId, ')') \n" +
                        "                     ORDER BY star_count DESC, s.name ASC SEPARATOR ', '), ', ', 3) AS stars,\n" +
                        "       r.rating,\n" +
                        "       m.id\n" +
                        "FROM (\n" +
                        "    SELECT id, title, year, director\n" +
                        "    FROM movies\n" +
                        "    WHERE id IN (\n" +
                        "        SELECT DISTINCT movieId\n" +
                        "        FROM genres_in_movies\n" +
                        "        WHERE genreId = (SELECT id FROM genres WHERE name = ?)\n" +
                        "    )\n" +
                        ") AS m\n" +
                        "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId\n" +
                        "LEFT JOIN genres g ON gm.genreId = g.id\n" +
                        "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId\n" +
                        "LEFT JOIN stars s ON sm.starId = s.id\n" +
                        "LEFT JOIN (\n" +
                        "   SELECT sm.starId, COUNT(DISTINCT sm.movieId) AS star_count\n" +
                        "   FROM stars_in_movies sm\n" +
                        "   GROUP BY sm.starId\n" +
                        " ) sc ON s.id = sc.starId \n" +
                        "LEFT JOIN ratings r ON m.id = r.movieId\n" +
                        "GROUP BY m.id, r.rating\n" +
                        sortString +
                        "LIMIT ?\n" +
                        "OFFSET ?;";
            } else {
                query = "SELECT m.title,\n" +
                        "        m.year,\n" +
                        "        m.director,\n" +
                        "        SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY gm.genreId SEPARATOR ', '), ', ', 3) AS genres,\n" +
                        "        SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT CONCAT(s.name, ' (', sm.starId, ')') \n" +
                        "                     ORDER BY star_count DESC, s.name ASC SEPARATOR ', '), ', ', 3) AS stars,\n" +
                        "        r.rating,\n" +
                        "        m.id\n" +
                        "FROM movies m\n" +
                        "LEFT JOIN ratings r ON m.id = r.movieId\n" +
                        "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId\n" +
                        "LEFT JOIN genres g ON gm.genreId = g.id\n" +
                        "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId\n" +
                        "LEFT JOIN (\n" +
                        "  SELECT sm.starId, COUNT(DISTINCT sm.movieId) AS star_count\n" +
                        "  FROM stars_in_movies sm\n" +
                        "  GROUP BY sm.starId\n" +
                        ") sc ON sm.starId = sc.starId\n" +
                        "LEFT JOIN stars s ON sm.starId = s.id \n" +
                        "WHERE (LOWER(m.title) LIKE LOWER(COALESCE(?, LOWER(m.title)))\n" +
                        "        AND m.year = COALESCE(?, m.year)\n" +
                        "        AND LOWER(m.director) LIKE LOWER(COALESCE(?, LOWER(m.director)))\n" +
                        "        AND LOWER(s.name) LIKE LOWER(COALESCE(?, LOWER(s.name))))\n" +
                        "GROUP BY m.id, r.rating\n" +
                        sortString +
                        "LIMIT ?\n" +
                        "OFFSET ?;";
            }
            System.out.println("THIS");

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);
            System.out.println("THIS1");

            if (!prefix.equals("")) {
                if (!prefix.equals("*")) {
                    statement.setString(1, prefix + "%");
                    statement.setInt(2, limit);
                    statement.setInt(3, offset);
                } else {
                    statement.setInt(1, limit);
                    statement.setInt(2, offset);
                }
            } else if (!genre.equals("")) {
                statement.setString(1, genre);
                statement.setInt(2, limit);
                statement.setInt(3, offset);
            } else {
                statement.setString(1, "%" + searchTitle + "%");
                if (searchYear == null) {
                    statement.setNull(2, java.sql.Types.INTEGER);
                } else {
                    statement.setInt(2, searchYear);
                }
                statement.setString(3, "%" + searchDirector + "%");
                statement.setString(4, "%" + searchStar + "%");
                System.out.println("HELLO4");
                statement.setInt(5, limit);
                statement.setInt(6, offset);
            }


            // Perform the query
            System.out.println(statement);
            startTimeTJ = System.nanoTime();
            ResultSet rs = statement.executeQuery();
            endTimeTJ = System.nanoTime();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            Boolean ran = false;
            while (rs.next()) {
                ran = true;
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

            if (!ran) {
                offset -= limit;
                searchParams.put("offset", offset);
                sort = Integer.valueOf(request.getParameter("sort"));
                searchParams.put("sort", sort);
                session.setAttribute("previousSearchParams", searchParams);
                System.out.println(searchParams);

            }

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");
            Gson gson = new Gson();
            JsonObject searchParamsObject = gson.toJsonTree(searchParams).getAsJsonObject();
            jsonArray.add(searchParamsObject);
            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

            long endTimeTS = System.nanoTime();
            long elapsedTimeTS = endTimeTS - startTimeTS;
            long elapsedTimeTJ = endTimeTJ - startTimeTJ;
            System.out.println(elapsedTimeTS);
            System.out.println(elapsedTimeTJ);
            try {
                System.out.println("HERE 1");
                System.out.println(request.getServletContext().getRealPath("/"));
                System.out.println(request.getServletContext());
                String filePath = request.getServletContext().getRealPath("/") + "log.txt";
                System.out.println(filePath);
                File myfile = new File(filePath);

                // Create the file if it doesn't exist
                if (!myfile.exists()) {
                    myfile.createNewFile();
                }

                // Write to the file
                FileWriter fileWriter = new FileWriter(myfile, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                String line = elapsedTimeTJ + " " + elapsedTimeTS;
                bufferedWriter.write(line);
                bufferedWriter.newLine();

                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

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
