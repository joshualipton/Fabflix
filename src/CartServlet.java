import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

/**
 * This CartServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/cart.
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        HashMap<String, Integer> previousItemsMap = (HashMap<String, Integer>) session.getAttribute("previousItemsMap");
        if (previousItemsMap == null) {
            previousItemsMap = new HashMap<String, Integer>();
        }
        // Log to localhost log
        request.getServletContext().log("getting " + previousItemsMap.size() + " items");
        JsonObject previousItemsJsonObject = new JsonObject();
        for (String key : previousItemsMap.keySet()) {
            previousItemsJsonObject.addProperty(key, previousItemsMap.get(key));
        }
        responseJsonObject.add("previousItems", previousItemsJsonObject);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String itemId = request.getParameter("itemId");
        String itemTitle = request.getParameter("itemTitle");
        String minus = request.getParameter("minus");
        String delete = request.getParameter("delete");
        String itemString = itemId + ":" + itemTitle;
        System.out.println(itemString);
        HttpSession session = request.getSession();

        // get the previous items in a HashMap
        HashMap<String, Integer> previousItemsMap = (HashMap<String, Integer>) session.getAttribute("previousItemsMap");
        if (previousItemsMap == null) {
            previousItemsMap = new HashMap<String, Integer>();
            previousItemsMap.put(itemString, 1);
            session.setAttribute("previousItemsMap", previousItemsMap);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItemsMap) {
                // increment the quantity of the item, or add it with a quantity of 1 if it doesn't exist
                if (minus.equals("true")) {
                    if (previousItemsMap.get(itemString) > 0) {
                        previousItemsMap.put(itemString, previousItemsMap.getOrDefault(itemString, 0) - 1);
                    }
                } else if (delete.equals("true")) {
                    previousItemsMap.remove(itemString);
                } else {
                    previousItemsMap.put(itemString, previousItemsMap.getOrDefault(itemString, 0) + 1);
                }
            }
        }

        JsonObject responseJsonObject = new JsonObject();
        System.out.println(previousItemsMap);

        // create a new JsonObject to hold the previous items and their quantities
        JsonObject previousItemsJsonObject = new JsonObject();
        for (String key : previousItemsMap.keySet()) {
            System.out.println(key);
            System.out.println(previousItemsMap.get(key));
            previousItemsJsonObject.addProperty(key, previousItemsMap.get(key));
            System.out.println(previousItemsJsonObject);
        }
        responseJsonObject.add("previousItems", previousItemsJsonObject);
        System.out.println(responseJsonObject);

        response.getWriter().write(responseJsonObject.toString());
    }
}