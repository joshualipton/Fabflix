package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.SearchBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private EditText search;
    private TextView message;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "fabflix";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SearchBinding binding = SearchBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        search = binding.search;
        message = binding.message;
        final Button searchButton = binding.searchButton;

        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(view -> fulltextSearch());
    }

    @SuppressLint("SetTextI18n")
    public void fulltextSearch() {
        message.setText("Searching...");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final String query = search.getText().toString();
        // request type is POST
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                "https://ec2-3-133-140-90.us-east-2.compute.amazonaws.com:8443/fabflix" + "/api/search-fulltext?query=" + query,
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.
                    Log.d("search.success", response);
                    //Complete and destroy login activity once successful
                    finish();
                    // initialize the activity(page)/destination
                    Intent MovieListPage = new Intent(SearchActivity.this, MovieListActivity.class);
                    MovieListPage.putExtra("response", response);
                    // activate the list page.
                    startActivity(MovieListPage);
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("query", search.getText().toString());
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
        Log.d("search.request", searchRequest.toString());
    }
}
