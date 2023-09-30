package main.java;
import java.util.ArrayList;
import java.util.List;

public class Movie {

    private final String id;

    private final String title;

    private final String year;

    private final String director;
    private final List<String> genres;

    private final List<Star> stars;

    public Movie(String id, String title, String year, String director, List<String> genres, List<Star> stars) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getYear() {
        return year;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public List<String> getGenre() {
        return genres;
    }

    public List<Star> getStars() {
        return stars;
    }

    public void addStar(Star star) {
        stars.add(star);
    }

    public void addGenre(String genre) {
        genres.add(genre);
    }

    public String toString() {

        return "Id:" + getId() + ", " +
                "Title:" + getTitle() + ", " +
                "Year:" + getYear() + ", " +
                "Genre:" + getGenre() + ", " +
                "Stars:" + getStars() + ", " +
                "Director:" + getDirector();
    }
}