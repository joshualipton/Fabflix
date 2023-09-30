package main.java;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DomParser {

    HashMap<String, Star> stars = new HashMap<>();
    HashMap<String, Movie> movies = new HashMap<>();
    Document domStars;
    Document domCast;
    Document domMains;
    Integer numInconsistentMovies = 0;
    Integer numDuplicateMovies = 0;
    Integer numDuplicateStars = 0;
    Integer numMoviesnNotFound = 0;
    Integer numStarsNotFound = 0;
    Integer numEmptyMovies = 0;

    public void runExample() throws ParserConfigurationException {

        // parse the xml file and get the dom object
        parseXmlFile();

        // get each employee element and create a Employee object
        parseDocumentStars();
        parseDocumentMains();
        parseDocumentCast();

        // iterate through the list and print the data
        printData();

    }

    private void parseXmlFile() throws ParserConfigurationException {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/validation", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            domStars = documentBuilder.parse("actors63.xml");
            domMains = documentBuilder.parse("mains243.xml");
            domCast = documentBuilder.parse("casts124.xml");


        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocumentStars() {
        // get the document root Element
        Element documentElement = domStars.getDocumentElement();

        // get a nodelist of star Elements, parse each into Star object
        NodeList nodeList = documentElement.getElementsByTagName("actor");
        for (int i = 0; i < nodeList.getLength(); i++) {

            // get the star element
            Element element = (Element) nodeList.item(i);

            // get the Star object
            Star star = parseStar(element);

            // add it to list

        }
    }

    private void parseDocumentCast() {
        // get the document root Element
        Element documentElement = domCast.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("m");
        for (int i = 0; i < nodeList.getLength(); i++) {

            // get the employee element
            Element element = (Element) nodeList.item(i);

            // get the Employee object
            parseCast(element);

            // add it to list
        }
        Iterator<HashMap.Entry<String, Movie>> iterator = movies.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry<String, Movie> entry = iterator.next();
            String movieId = entry.getKey();
            Movie movie = entry.getValue();

            if (movie.getStars().isEmpty()) {
                iterator.remove();  // Use the iterator's remove() method
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("EmptyMovies.txt", true))) {
                    String line = String.format("Id: %s", movieId);
                    writer.write(line);
                    writer.newLine();
                    numEmptyMovies++;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void parseCast(Element element) {

        // for each <employee> element get text or int values of
        // name ,id, age and name
        String id = getTextValue(element, "f");
        String star = getTextValue(element, "a");

        // create a new Employee with the value read from the xml nodes
        if (stars.containsKey(star)) {
            Star starObj = stars.get(star);
            if (movies.containsKey(id)) {
                movies.get(id).addStar(starObj);
            } else {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("MoviesNotFound.txt", true))) {
                    String line = String.format("Movie Id: %s", id);
                    writer.write(line);
                    writer.newLine();
                    numMoviesnNotFound++;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("StarsNotFound.txt", true))) {
                String line = String.format("Movie Id: %s, Star: %s",
                        id, star);
                writer.write(line);
                writer.newLine();
                numStarsNotFound++;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    private void parseDocumentMains() {
        // get the document root Movie
        Element documentElement = domMains.getDocumentElement();


        // get a nodelist of movie Elements, parse each into Movie object
        NodeList nodeList = documentElement.getElementsByTagName("directorfilms");
        for (int i = 0; i < nodeList.getLength(); i++) {
            // get the movie element
            Element elementDirectorFilms = (Element) nodeList.item(i);
            String director = getTextValue(elementDirectorFilms, "dirname");
            NodeList filmList = elementDirectorFilms.getElementsByTagName("film");
            for (int j = 0; j < filmList.getLength(); j++) {
                Element elementFilm = (Element) filmList.item(j);
                // get the Movie object
                Movie movie = parseMovie(elementFilm, director);
            }
        }
    }

    /**
     * It takes an employee Element, reads the values in, creates
     * an Employee object for return
     */
    private Star parseStar(Element element) {

        // for each <employee> element get text or int values of
        // name ,id, age and name
        String name = getTextValue(element, "stagename");
        String birthYear = getTextValue(element, "dob");

        // create a new Employee with the value read from the xml nodes
        if (!stars.containsKey(name)) {
            stars.put(name, new Star(name, birthYear));
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("StarDuplicates.txt", true))) {
                String line = String.format("Name: %s, Birth Year: %s",
                        name, birthYear);
                writer.write(line);
                writer.newLine();
                numDuplicateStars++;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return new Star(name, birthYear);
    }

    private Movie parseMovie(Element element, String director) {

        // for each <employee> element get text or int values of
        // name ,id, age and name
        String title = null;
        String id = null;
        String year = null;
        List<String> genres = new ArrayList<>();
        List<Star> stars = new ArrayList<>();
        try {
            title = getTextValue(element, "t");
            id = getTextValue(element, "fid");
            year = getTextValue(element, "year");
            Integer yearInt = Integer.parseInt(year);
            NodeList genreList = element.getElementsByTagName("cats");
            for (int j = 0; j < genreList.getLength(); j++) {
                Element elementGenre = (Element) genreList.item(j);
                // get the Movie object
                String genre = getTextValue(elementGenre, "cat");
                genres.add(genre);
            }
            if (genres.get(0).equals("null") || title.equals("null") || id.equals("null") || year.equals("null")) {
                throw new RuntimeException();
            }
            if (!movies.containsKey(id)) {
                movies.put(id, new Movie(id, title, year, director, genres, stars));
            } else {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("MovieDuplicates.txt", true))) {
                    String line = String.format("Id: %s, Title: %s, Year: %s, Director: %s GenreList: %s",
                            id, title, year, director, genres);
                    writer.write(line);
                    writer.newLine();
                    numDuplicateMovies++;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("MoviesInconsistent.txt", true))) {
                String line = String.format("Id: %s, Title: %s, Year: %s, Director: %s GenreList: %s",
                        id, title, year, director, genres);
                writer.write(line);
                writer.newLine();
                numInconsistentMovies++;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // create a new Employee with the value read from the xml nodes
        return new Movie(id, title, year, director, genres, stars);
    }

    /**
     * It takes an XML element and the tag name, look for the tag and get
     * the text content
     * i.e for <Employee><Name>John</Name></Employee> xml snippet if
     * the Element points to employee node and tagName is name it will return John
     */
    private String getTextValue(Element element, String tagName) {
        String textVal = "null";
        NodeList nodeList = element.getElementsByTagName(tagName);
        try {
            if (nodeList.getLength() > 0) {
                // here we expect only one <Name> would present in the <Employee>
                textVal = nodeList.item(0).getFirstChild().getNodeValue();
            }
        } catch (Exception e) {
        }
        return textVal;
    }

    /**
     * Calls getTextValue and returns a int value
     */
    private int getIntValue(Element ele, String tagName) {
        // in production application you would catch the exception
        try {
            return Integer.parseInt(getTextValue(ele, tagName));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {
        System.out.println("Total parsed " + stars.size() + " stars");
        System.out.println("Total parsed " + movies.size() + " movies");
        System.out.println(numInconsistentMovies + " Inconsistent movies");
        System.out.println(numDuplicateMovies + " Duplicate movies");
        System.out.println(numDuplicateStars + " Duplicate stars");
        System.out.println(numMoviesnNotFound + " movies not found");
        System.out.println(numStarsNotFound + " stars not found");
        System.out.println(numEmptyMovies + " movies have no stars");
    }

    public static void main(String[] args) throws ParserConfigurationException {
        // create an instance
        DomParser domParser = new DomParser();

        // call run example
        domParser.runExample();
    }

}