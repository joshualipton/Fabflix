package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivityMovielistBinding;
import edu.uci.ics.fabflixmobile.databinding.SearchBinding;
import edu.uci.ics.fabflixmobile.databinding.SingleMovieBinding;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SingleMovieActivity extends AppCompatActivity {
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "fabflix";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingleMovieBinding binding = SingleMovieBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // TODO: this should be retrieved from the backend server
        Intent intent = getIntent();
        String movieName = "";
        short movieYear = 0;
        String movieRating = "";
        String movieDirector = "";
        String movieGenres = "";
        String movieStars = "";

        if (intent != null) {
            movieName = intent.getStringExtra("movieName");
            movieYear = intent.getShortExtra("movieYear", (short) 0);
            movieRating = intent.getStringExtra("movieRating");
            movieDirector = intent.getStringExtra("movieDirector");
            movieGenres = intent.getStringExtra("movieGenres");
            movieStars = intent.getStringExtra("movieStars");
        }

        String[] starEntries = movieStars.split(", ");

        ArrayList<String> starNames = new ArrayList<>();
        for (String starEntry : starEntries) {
            int indexOfOpeningParenthesis = starEntry.indexOf("(");
            if (indexOfOpeningParenthesis != -1) {
                String starName = starEntry.substring(0, indexOfOpeningParenthesis).trim();
                starNames.add(starName);
            }
        }

        String formattedStarNames = String.join(", ", starNames);

        TextView titleTextView = findViewById(R.id.singleMovieTitle);
        TextView yearTextView = findViewById(R.id.singleMovieYear);
        TextView ratingTextView = findViewById(R.id.singleMovieRating);
        TextView directorTextView = findViewById(R.id.singleMovieDirector);
        TextView genresTextView = findViewById(R.id.singleMovieGenres);
        TextView starsTextView = findViewById(R.id.singleMovieStars);

        titleTextView.setText(movieName);
        yearTextView.setText("Year: " + String.valueOf(movieYear));
        ratingTextView.setText("Rating: " + movieRating);
        directorTextView.setText("Director: " + movieDirector);
        genresTextView.setText("Genres: " + movieGenres);
        starsTextView.setText("Stars: " + formattedStarNames);
    }
}