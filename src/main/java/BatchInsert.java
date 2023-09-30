package main.java;//import jakarta.servlet.ServletConfig;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/*
The SQL command to create the table ft.
DROP TABLE IF EXISTS ft;
CREATE TABLE ft (
entryID INT AUTO_INCREMENT,
entry text,
PRIMARY KEY (entryID),
FULLTEXT (entry)) ENGINE=MyISAM;
*/
/*
Note: Please change the username, password and the name of the datbase.
*/
public class BatchInsert {
    public static void main (String[] args) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException, ParserConfigurationException {
        // create an instance
        ActorSAXParser saxParser = new ActorSAXParser();
        saxParser.runExample();
        Integer numInsertedStars = 0;
        Integer numInsertedMovies = 0;

        Connection conn = null;
//        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        String jdbcURL="jdbc:mysql://localhost:3306/moviedb";
        try {
            conn = DriverManager.getConnection(jdbcURL,"mytestuser",
                    "My6$Password");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("hi 1");
        CallableStatement csInsertRecord = null;
        String sqlInsertRecord=null;
        int[] iNoRows=null;
        sqlInsertRecord="{CALL add_star_no_output(?, ?)}";
        try {
            System.out.println("hi 2");
            conn.setAutoCommit(false);
            csInsertRecord=conn.prepareCall(sqlInsertRecord);
            for (HashMap.Entry<String, Star> entry : saxParser.stars.entrySet()) {
                String starName = entry.getKey();
                String starBirthYear = entry.getValue().getBirthYear();
                Integer birthYear;
                try {
                    birthYear = Integer.parseInt(starBirthYear);
                } catch (Exception e) {
                    birthYear = null;
                }
                csInsertRecord.setString(1, starName);
                try {
                    csInsertRecord.setInt(2, birthYear);
                } catch (Exception e) {
                    csInsertRecord.setObject(2, null);
                }
                csInsertRecord.addBatch();
                numInsertedStars++;
            }
            iNoRows=csInsertRecord.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            System.out.println("hi 4");
            e.printStackTrace();
        }
        sqlInsertRecord="{CALL add_movie_with_genres(?, ?, ?, ?)}";
        try {
            conn.setAutoCommit(false);
            csInsertRecord=conn.prepareCall(sqlInsertRecord);
            for (HashMap.Entry<String, Movie> entry : saxParser.movies.entrySet()) {
                String movieTitle = entry.getValue().getTitle();
                String movieYear = entry.getValue().getYear();
                Integer year = Integer.parseInt(movieYear);
                String director = entry.getValue().getDirector();
                List<String> genres = entry.getValue().getGenre();
                for (int i = 0; i < genres.size(); i++) {
                    String genre = genres.get(i);
                    csInsertRecord.setString(1, movieTitle);
                    csInsertRecord.setInt(2, year);
                    csInsertRecord.setString(3, director);
                    csInsertRecord.setString(4, genre);
                    csInsertRecord.addBatch();
                }
            }
            iNoRows=csInsertRecord.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlInsertRecord="{CALL add_movie_with_stars(?, ?, ?, ?)}";
        try {
            conn.setAutoCommit(false);
            csInsertRecord=conn.prepareCall(sqlInsertRecord);
            for (HashMap.Entry<String, Movie> entry : saxParser.movies.entrySet()) {
                String movieTitle = entry.getValue().getTitle();
                String movieYear = entry.getValue().getYear();
                Integer year = Integer.parseInt(movieYear);
                String director = entry.getValue().getDirector();
                List<Star> stars = entry.getValue().getStars();
                for (int i = 0; i < stars.size(); i++) {
                    Star star = stars.get(i);
                    csInsertRecord.setString(1, movieTitle);
                    csInsertRecord.setInt(2, year);
                    csInsertRecord.setString(3, director);
                    csInsertRecord.setString(4, star.getName());
                    csInsertRecord.addBatch();
                }
            }
            iNoRows=csInsertRecord.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(csInsertRecord!=null) csInsertRecord.close();
            if(conn!=null) conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}