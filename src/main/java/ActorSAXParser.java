package main.java;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ActorSAXParser extends DefaultHandler {
    public HashMap<String, Star> stars = new HashMap<>();
    public HashMap<String, Movie> movies = new HashMap<>();
    private String currentStarName;
    private String currentStarYear = "null";
    private String currentMovieId = "null";
    private String currentMovieDirector = "null";
    private String currentMovieTitle = "null";
    private String currentMovieYear = "null";
    private List<String> currentMovieGenres = new ArrayList<>();
    private List<Star> currentMovieStars = new ArrayList<>();
    private Stack<String> elementStack;
    private StringBuilder dataBuffer;
    private Integer numInconsistentMovies = 0;
    private Integer numDuplicateMovies = 0;
    private Integer numDuplicateStars = 0;
    private Integer numMoviesNotFound = 0;
    private Integer numStarsNotFound = 0;
    private Integer numEmptyMovies = 0;

    public void runExample() {
        try {
            // create SAX parser factory
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            // create SAX parser
            javax.xml.parsers.SAXParser parser = factory.newSAXParser();

            // parse XML files
            parser.parse("src/main/java/actors63.xml", this);
            parser.parse("src/main/java/mains243.xml", this);
            parser.parse("src/main/java/casts124.xml", this);

            // iterate through the parsed data
            printData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startDocument() throws SAXException {
        // initialize variables
//        stars.clear();
//        movies.clear();
        elementStack = new Stack<>();
        dataBuffer = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // push current element to stack
        elementStack.push(qName);

        // reset data buffer for new element
        dataBuffer.setLength(0);

        // handle specific elements
        if ("actor".equalsIgnoreCase(qName)) {

        } else if ("m".equalsIgnoreCase(qName)) {
            // parse cast element
            parseCast(attributes);
        } else if ("film".equalsIgnoreCase(qName)) {
            // parse movie element
            parseMovie(attributes);
        } else if ("filmc".equalsIgnoreCase(qName)) {
            currentMovieStars.clear();
        } else if ("cat".equalsIgnoreCase(qName)) {
            currentMovieGenres.clear();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // accumulate character data
        dataBuffer.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // handle specific elements
        if ("actor".equalsIgnoreCase(qName)) {
            // finished parsing star element
            Star currentStar = new Star(currentStarName, currentStarYear);
            if (!stars.containsKey(currentStarName)) {
                stars.put(currentStarName, currentStar);
            } else {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("StarDuplicates.txt", true))) {
                    String line = String.format("Name: %s, Birth Year: %s",
                            currentStarName, currentStarYear);
                    writer.write(line);
                    writer.newLine();
                    numDuplicateStars++;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if ("dirname".equalsIgnoreCase(qName)) {
            currentMovieDirector = dataBuffer.toString().trim();
        } else if ("film".equalsIgnoreCase(qName)) {
            try {
                if (currentMovieTitle.equals("") || currentMovieId.equals("") || currentMovieYear.equals("") || currentMovieDirector.equals("") || currentMovieYear.contains("x") || currentMovieYear.contains("y")) {
                    throw new RuntimeException();
                }
                if (!movies.containsKey(currentMovieId)) {
                    movies.put(currentMovieId, new Movie(currentMovieId, currentMovieTitle, currentMovieYear, currentMovieDirector, new ArrayList<>(currentMovieGenres), new ArrayList<>()));
                } else {
                    if (movies.get(currentMovieId).getGenre().isEmpty() || movies.get(currentMovieId).getGenre().get(0).equals("") || movies.get(currentMovieId).getGenre().get(0) == null) {
                        throw new RuntimeException();
                    }
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("MovieDuplicates.txt", true))) {
                        String line = String.format("Id: %s, Title: %s, Year: %s, Director: %s GenreList: %s",
                                currentMovieId, currentMovieTitle, currentMovieYear, currentMovieDirector, currentMovieGenres);
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
                            currentMovieId, currentMovieTitle, currentMovieYear, currentMovieDirector, currentMovieGenres);
                    writer.write(line);
                    writer.newLine();
                    numInconsistentMovies++;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if ("stagename".equalsIgnoreCase(qName)) {
            // finished parsing star name
            currentStarName = dataBuffer.toString().trim();
        } else if ("dob".equalsIgnoreCase(qName)) {
            currentStarYear = dataBuffer.toString().trim();
        } else if ("fid".equalsIgnoreCase(qName)) {
            // finished parsing movie ID in cast element
            currentMovieId = dataBuffer.toString().trim();
        } else if ("a".equalsIgnoreCase(qName)) {
            // finished parsing star name in cast element
            String starName = dataBuffer.toString().trim();
            if (stars.containsKey(starName)) {
                Star starObj = stars.get(starName);
//                System.out.println(starObj);
                if (movies.containsKey(currentMovieId)) {
                    movies.get(currentMovieId).addStar(starObj);
                } else {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("MoviesNotFound.txt", true))) {
                        String line = String.format("Movie Id: %s", currentMovieId);
                        writer.write(line);
                        writer.newLine();
                        numMoviesNotFound++;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("StarsNotFound.txt", true))) {
                    String line = String.format("Movie Id: %s, Star: %s",
                            currentMovieId, starName);
                    writer.write(line);
                    writer.newLine();
                    numStarsNotFound++;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if ("t".equalsIgnoreCase(qName)) {
            // finished parsing movie title
            currentMovieTitle = dataBuffer.toString().trim();
        } else if ("year".equalsIgnoreCase(qName)) {
            // finished parsing movie year
            currentMovieYear = dataBuffer.toString().trim();
        } else if ("f".equalsIgnoreCase(qName)) {
            currentMovieId = dataBuffer.toString().trim();
//            System.out.println(currentMovieId);
        } else if ("cat".equalsIgnoreCase(qName)) {
            // finished parsing movie genre
            currentMovieGenres.add(dataBuffer.toString().trim());
        }

        // pop current element from stack
        elementStack.pop();
    }

    private void parseStar(Attributes attributes) {
        // extract star attributes if needed
    }

    private void parseCast(Attributes attributes) {
        // extract cast attributes if needed
    }

    private void parseMovie(Attributes attributes) {
        // extract movie attributes if needed
    }

    private void parseStarName(String name) {
        // parse star name
        // add it to the stars map if not duplicate
    }

    private void parseStarBirthYear(String birthYear) {
        // parse star birth year
        // add it to the corresponding Star object if available
    }

    private void parseStarInCast(String star) {
        // parse star in cast
        // add it to the corresponding Movie object if available
    }

    private void parseMovieTitle(String title) {
        // parse movie title
        // add it to the corresponding Movie object if available
    }

    private void parseMovieId(String id) {
        // parse movie ID
        // use it to link stars with movies
    }

    private void parseMovieYear(String year) {
        // parse movie year
        // add it to the corresponding Movie object if available
    }

    private void parseMovieGenre(String genre) {
        // parse movie genre
        // add it to the corresponding Movie object if available
    }

    private void printData() {
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
        System.out.println("Total parsed " + stars.size() + " stars");
        System.out.println("Total parsed " + movies.size() + " movies");
        System.out.println(numInconsistentMovies + " inconsistent movies");
        System.out.println(numDuplicateMovies + " duplicate movies");
        System.out.println(numDuplicateStars + " duplicate stars");
        System.out.println(numMoviesNotFound + " movies not found");
        System.out.println(numStarsNotFound + " stars not found");
        System.out.println(numEmptyMovies + " movies have no stars");
    }

    public static void main(String[] args) {
        ActorSAXParser saxParser = new ActorSAXParser();
        saxParser.runExample();
    }
}


