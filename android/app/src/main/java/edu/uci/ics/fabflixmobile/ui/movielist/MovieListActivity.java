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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {
    private TextView pageNumber;
    private int pageNum = 1;

    private String response;

    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "fabflix";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovielistBinding binding = ActivityMovielistBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
//        setContentView(R.layout.activity_movielist);
        setContentView(binding.getRoot());
        pageNumber = binding.pageNumber;
        final Button next = binding.next;
        final Button prev = binding.prev;
        next.setOnClickListener(v -> incrementPage());
        prev.setOnClickListener(v -> decrementPage());
        // TODO: this should be retrieved from the backend server
        response = getIntent().getStringExtra("response");
        updateMovies();
    }
    private void incrementPage() {
        pageNum++;
        displayPageNumber();
    }

    private void decrementPage() {
        if (pageNum > 1) {
            pageNum--;
            displayPageNumber();
        }
    }

    private void displayPageNumber() {
        pageNumber.setText(String.valueOf(pageNum));
        updateMovies();
    }

    private void updateMovies() {
        final ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);

            // Iterate over each item in the JSON array
            for (int i = 10 * (pageNum - 1); i < Math.min(10 * pageNum, jsonArray.length()); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Extract the necessary information from the JSON object
                String movieTitle = jsonObject.getString("movie_title");
                int movieYear = jsonObject.getInt("movie_year");
                String movieRating = jsonObject.getString("movie_rating");
                String movieDirector = jsonObject.getString("movie_director");
                String movieGenres = jsonObject.getString("movie_genres");
                String movieStars = jsonObject.getString("movie_stars");


                movies.add(new Movie(movieTitle, (short) movieYear, movieRating, movieDirector, movieGenres, movieStars));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
//            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            Intent SingleMovieActivity = new Intent(MovieListActivity.this, SingleMovieActivity.class);
            SingleMovieActivity.putExtra("movieName", movie.getName());
            SingleMovieActivity.putExtra("movieYear", movie.getYear());
            SingleMovieActivity.putExtra("movieRating", movie.getRating());
            SingleMovieActivity.putExtra("movieDirector", movie.getDirector());
            SingleMovieActivity.putExtra("movieGenres", movie.getGenres());
            SingleMovieActivity.putExtra("movieStars", movie.getStars());
            startActivity(SingleMovieActivity);
        });
    }
}