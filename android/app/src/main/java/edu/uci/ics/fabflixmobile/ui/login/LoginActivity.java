package edu.uci.ics.fabflixmobile.ui.login;

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
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.SearchActivity;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
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

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        username = binding.username;
        password = binding.password;
        message = binding.message;
        final Button loginButton = binding.login;

        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(view -> login());
    }

    @SuppressLint("SetTextI18n")
    public void login() {
        message.setText("Trying to login");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                "https://ec2-3-133-140-90.us-east-2.compute.amazonaws.com:8443/fabflix" + "/api/login",
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.
                    Log.d("login.success", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response); // Parse the JSON string
                        String status = jsonResponse.getString("status"); // Get the value of the "status" field

                        if (status.equals("success")) {
                            // Handle success scenario
                            String message = jsonResponse.getString("message"); // Get the value of the "message" field
                            finish();
                            Intent MovieListPage = new Intent(LoginActivity.this, SearchActivity.class);
                            startActivity(MovieListPage);
                            // Perform actions for success
                        } else if (status.equals("fail")) {
                            // Handle fail scenario
                            String errorMessage = jsonResponse.getString("message"); // Get the value of the "message" field
                            message.setText(errorMessage);
                            // Perform actions for failure
                        }
                    } catch (Exception e) {
                        // Handle JSON parsing exception
                        Log.d("JSON.error", e.getMessage());
                    }
//                    //Complete and destroy login activity once successful
//                    finish();
//                    // initialize the activity(page)/destination
//                    Intent MovieListPage = new Intent(LoginActivity.this, SearchActivity.class);
//                    // activate the list page.
//                    startActivity(MovieListPage);
                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}