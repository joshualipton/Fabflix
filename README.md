DEMO: https://www.youtube.com/watch?v=IzTlKUTL2ks&ab_channel=JoshuaLipton

I worked on this project individually

Optimizations:
Hashmaps for parsed file data for O(1) lookup
Batch Inserts for each table
SAX Parser to save memory

Files with prepared statement:
BrowseServlet.java
CheckoutServlet.java
ConfirmationServlet.java
EmployeeLoginServlet.java
GenreServlet.java
LoginServlet.java
MoviesSearchServlet.java
MoviesServlet.java
SingleMovieServlet.java
SingleStarServlet.java

Inconsistency Report:
EmptyMovies.txt:
Id: DeJ30
Id: StN10
Id: MMr10
Id: TG42
Id: WRe40
Id: BSc11
Id: SE1
Id: HBn5
Id: SE3
Id: KvB10
Id: SE4
Id: SE6
Id: SE8
Id: DeJ20
Id: GTi10
Id: FNK2
Id: LVa12
Id: FNK6
Id: Z9845
Id: Z9840
Id: MBr14
Id: CBl73
Id: CBl70

MovieDuplicates.txt:
Id: RM13, Title: Summer Holiday, Year: 1948, Director: Mamoulian GenreList: [Musc]
Id: RM13, Title: Silk Stockings, Year: 1957, Director: Mamoulian GenreList: [Musc]
Id: RM13, Title: Menschen am Sonntag, Year: 1929, Director: R.Siodmak GenreList: [Docu]
Id: RM13, Title: Pi\`eges, Year: 1939, Director: R.Siodmak GenreList: [Docu]
Id: RM13, Title: Son of Dracula, Year: 1943, Director: R.Siodmak GenreList: [Horr]
Id: RM13, Title: Cobra Woman, Year: 1944, Director: R.Siodmak GenreList: [Camp]
Id: RM13, Title: Phantom Lady, Year: 1944, Director: R.Siodmak GenreList: [Noir]
Id: RM13, Title: The Spiral Staircase, Year: 1945, Director: R.Siodmak GenreList: [Susp]
Id: RM13, Title: Nocturne, Year: 1946, Director: R.Siodmak GenreList: [Myst]
Id: RM13, Title: The Killers, Year: 1946, Director: R.Siodmak GenreList: [Noir]
Id: RM13, Title: The Dark Mirror, Year: 1946, Director: R.Siodmak GenreList: [Noir]
Id: RM13, Title: Criss Cross, Year: 1948, Director: R.Siodmak GenreList: [Dram]
Id: RM13, Title: The Spiral Staircase, Year: 1948, Director: R.Siodmak GenreList: [Dram]
Id: RM13, Title: The Crimson Pirate, Year: 1952, Director: R.Siodmak GenreList: [Actn]

MovieInconsistency.txt:
Id: LB9, Title: El grand cavalcodos, Year: 19yy, Director: Bunuel GenreList: [Dram]
Id: JD7, Title: , Year: 1947, Director: Dassin GenreList: [Actn]
Id: WS45, Title: Catherine of Russia, Year: 19yy, Director: W.Staudte GenreList: [Ctxx]
Id: WlJ10, Title: , Year: 1949, Director: W.Jackson GenreList: [Comd]
Id: AS22, Title: Morgan Stewart's Coming Home, Year: 199x, Director: Alan~Smithee GenreList: [TV]
Id: AS32, Title: Raging Angels, Year: 199x, Director: Alan~Smithee GenreList: [TV]
Id: GeP45, Title: , Year: 1999, Director: Pollock GenreList: [West]
Id: GxR10, Title: , Year: 1954, Director: G.Reinhardt GenreList: [Comd]
Id: GxR10, Title: , Year: 1954, Director: G.Reinhardt GenreList: [Comd]
Id: RHd2, Title: Stop Train 349, Year: 199x, Director: Haedrich GenreList: [Susp]
Id: IAv12, Title: Karate Kid, Year: 199x, Director: J.G.Avildsen GenreList: [Romt]
Id: MS0, Title: The Eternal City, Year: 196x, Director: Scorsese GenreList: [Dram]
Id: WiW30, Title: Buena Vista Social Club, Year: 19yy, Director: Wenders GenreList: [Musc]
Id: DLy25, Title: Mulholland Drive, Year: 19yy, Director: D.Lynch GenreList: [Ctxx]
Id: MLt15, Title: , Year: 1984, Director: M.Lester GenreList: [Horr]
Id: AEy13, Title: Sarabande, Year: 19yy, Director: Egoyan GenreList: [Dram]
Id: BSi3, Title: , Year: 1988, Director: Silberling GenreList: [Susp]
Id: CsS15, Title: Fairy Tale: A True Story, Year: 199x, Director: Sturridge GenreList: [Fant]
Id: , Title: Cradle will Rock, Year: 2000, Director: T.Robbins GenreList: [Dram]
Id: KDo1x, Title: , Year: 199x, Director: Dowling GenreList: [Dram]
Id: TdC13, Title: Jerry and Tom, Year: 19yy, Director: diCillo GenreList: [Dram]
Id: Z9590, Title: The Shanghai Triad, Year: 19yy, Director: UnYear95 GenreList: [CnRb]
Id: AbK15, Title: Taste of Cherry, Year: 19yy, Director: Kiarostami GenreList: [Dram]
Id: MJu15, Title: All is Routine, Year: 19yy, Director: M.Judge GenreList: [Comd]
Id: LB9, Title: El grand cavalcodos, Year: 19yy, Director: Bunuel GenreList: [Dram]
Id: JD7, Title: , Year: 1947, Director: Dassin GenreList: [Actn]
Id: WS45, Title: Catherine of Russia, Year: 19yy, Director: W.Staudte GenreList: [Ctxx]
Id: WlJ10, Title: , Year: 1949, Director: W.Jackson GenreList: [Comd]
Id: AS22, Title: Morgan Stewart's Coming Home, Year: 199x, Director: Alan~Smithee GenreList: [TV]
Id: AS33, Title: Raging Angels, Year: 199x, Director: Alan~Smithee GenreList: [TV]
Id: GeP45, Title: , Year: 1999, Director: Pollock GenreList: [West]
Id: GxR10, Title: , Year: 1954, Director: G.Reinhardt GenreList: [Comd]
Id: GxR10, Title: , Year: 1954, Director: G.Reinhardt GenreList: [Comd]
Id: RHd2, Title: Stop Train 349, Year: 199x, Director: Haedrich GenreList: [Susp]
Id: IAv12, Title: Karate Kid, Year: 199x, Director: J.G.Avildsen GenreList: [Romt]
Id: MS0, Title: The Eternal City, Year: 196x, Director: Scorsese GenreList: [Dram]
Id: WiW30, Title: Buena Vista Social Club, Year: 19yy, Director: Wenders GenreList: [Musc]
Id: DLy25, Title: Mulholland Drive, Year: 19yy, Director: D.Lynch GenreList: [Ctxx]
Id: MLt15, Title: , Year: 1984, Director: M.Lester GenreList: [Horr]
Id: AEy13, Title: Sarabande, Year: 19yy, Director: Egoyan GenreList: [Dram]
Id: BSi3, Title: , Year: 1988, Director: Silberling GenreList: [Susp]
Id: CsS15, Title: Fairy Tale: A True Story, Year: 199x, Director: Sturridge GenreList: [Fant]
Id: , Title: Cradle will Rock, Year: 2000, Director: T.Robbins GenreList: [Dram]
Id: KDo1x, Title: , Year: 199x, Director: Dowling GenreList: [Dram]
Id: TdC13, Title: Jerry and Tom, Year: 19yy, Director: diCillo GenreList: [Dram]
Id: Z9590, Title: The Shanghai Triad, Year: 19yy, Director: UnYear95 GenreList: [CnRb]
Id: AbK15, Title: Taste of Cherry, Year: 19yy, Director: Kiarostami GenreList: [Dram]
Id: MJu15, Title: All is Routine, Year: 19yy, Director: M.Judge GenreList: [Comd]

MoviesNotFound.txt:
See File

StarDuplicates.txt:
Name: Kenneth Branagh, Birth Year:
Name: Wilford Brimley, Birth Year:
Name: Colin Campbell, Birth Year: 1937
Name: John Cassavetes, Birth Year: 1929
Name: William Devane, Birth Year:
Name: Derek Farr, Birth Year:
Name: Charles Farrell, Birth Year: 1901
Name: Frances Fisher, Birth Year:
Name: Peter Graves, Birth Year: 1911
Name: Laurence Harvey, Birth Year: 1928
Name: Nicky Henson, Birth Year: 1945
Name: William Holden, Birth Year: 1918
Name: Rex Ingram, Birth Year: 1895
Name: Charles King, Birth Year: 1899
Name: Jack Lambert, Birth Year: 1920
Name: Jane March, Birth Year: 1973
Name: Spanky McFarland, Birth Year:
Name: Mary Morris, Birth Year: 1915
Name: Jennifer ONeill, Birth Year: 1947
Name: Raymond Pellegrin, Birth Year:
Name: Harvey Stephens, Birth Year: 1970
Name: Sting, Birth Year: n.a.
Name: Ann Todd, Birth Year: 1932
Name: Beverly Tyler, Birth Year: 1924
Name: Vanessa Williams, Birth Year: 1963
Name: Kenneth Branagh, Birth Year:
Name: Wilford Brimley, Birth Year:
Name: Colin Campbell, Birth Year: 1937
Name: John Cassavetes, Birth Year: 1929
Name: William Devane, Birth Year:
Name: Derek Farr, Birth Year:
Name: Charles Farrell, Birth Year: 1901
Name: Frances Fisher, Birth Year:
Name: Peter Graves, Birth Year: 1911
Name: Laurence Harvey, Birth Year: 1928
Name: Nicky Henson, Birth Year: 1945
Name: William Holden, Birth Year: 1918
Name: Rex Ingram, Birth Year: 1895
Name: Charles King, Birth Year: 1899
Name: Jack Lambert, Birth Year: 1920
Name: Jane March, Birth Year: 1973
Name: Spanky McFarland, Birth Year:
Name: Mary Morris, Birth Year: 1915
Name: Jennifer ONeill, Birth Year: 1947
Name: Raymond Pellegrin, Birth Year:
Name: Harvey Stephens, Birth Year: 1970
Name: Sting, Birth Year: n.a.
Name: Ann Todd, Birth Year: 1932
Name: Beverly Tyler, Birth Year: 1924
Name: Vanessa Williams, Birth Year: 1963

StarsNotFound.txt:
See file
                                                              28,1          78%

                                                              1,1           Top

                                                              1156,1        Bot



I used LIKE with Lower() to make my search feature so that it can work with capitalized or non capitalized letters.

## CS 122B Project 1 API example

This example shows how frontend and backend are separated by implementing a star list page, and a single star page with movie list.

### Before running the example

#### If you do not have USER `mytestuser` setup in MySQL, follow the below steps to create it:

 - login to mysql as a root user 
    ```
    local> mysql -u root -p
    ```

 - create a test user and grant privileges:
    ```
    mysql> CREATE USER 'mytestuser'@'localhost' IDENTIFIED BY 'My6$Password';
    mysql> GRANT ALL PRIVILEGES ON * . * TO 'mytestuser'@'localhost';
    mysql> quit;
    ```

#### prepare the database `moviedbexample`
 

```
local> mysql -u mytestuser -p
mysql> CREATE DATABASE IF NOT EXISTS moviedbexample;
mysql> USE moviedbexample;
mysql> CREATE TABLE IF NOT EXISTS stars(
               id varchar(10) primary key,
               name varchar(100) not null,
               birthYear integer
           );

mysql> INSERT IGNORE INTO stars VALUES('755011', 'Arnold Schwarzeneggar', 1947);
mysql> INSERT IGNORE INTO stars VALUES('755017', 'Eddie Murphy', 1961);

mysql> CREATE TABLE if not exists movies(
       	    id VARCHAR(10) DEFAULT '',
       	    title VARCHAR(100) DEFAULT '',
       	    year INTEGER NOT NULL,
       	    director VARCHAR(100) DEFAULT '',
       	    PRIMARY KEY (id)
       );

mysql> INSERT IGNORE INTO movies VALUES('1111', 'The Terminator', 1984, 'James Cameron');
mysql> INSERT IGNORE INTO movies VALUES('2222', 'Coming To America', 1988, 'John Landis');

mysql> CREATE TABLE IF NOT EXISTS stars_in_movies(
       	    starId VARCHAR(10) DEFAULT '',
       	    movieId VARCHAR(10) DEFAULT '',
       	    FOREIGN KEY (starId) REFERENCES stars(id),
       	    FOREIGN KEY (movieId) REFERENCES movies(id)
       );

mysql> INSERT IGNORE INTO stars_in_movies VALUES('755017', '2222');
mysql> INSERT IGNORE INTO stars_in_movies VALUES('755011', '1111');
mysql> quit;
```

### To run this example: 
1. Clone this repository using `git clone`
2. Open IntelliJ -> Import Project -> Choose the project you just cloned (The root path must contain the pom.xml!) -> Choose Import project from external model -> choose Maven -> Click on Finish -> The IntelliJ will load automatically
3. For "Root Directory", right click "cs122b-project1-api-example" -> Mark Directory as -> sources root
4. In `WebContent/META-INF/context.xml`, make sure the mysql username is `mytestuser` and password is `mypassword`.
5. Also make sure you have the `moviedbexample` database.
6. To run the example, follow the instructions in canvas.

### Brief Explanation
- `StarsServlet.java` is a Java servlet that talks to the database and get the stars. It returns a list of stars in the JSON format. 
The name of star is generated as a link to Single Star page.

- `top-20.js` is the main Javascript file that initiates an HTTP GET request to the `StarsServlet`. After the response is returned, `top-20.js` populates the table using the data it gets.

- `index.html` is the main HTML file that imports jQuery, Bootstrap, and `top-20.js`. It also contains the initial skeleton for the table.

- `SingleStarServlet.java` is a Java servlet that talks to the database and get information about one Star and all the movie this Star performed. It returns a list of Movies in the JSON format. 

- `single-star.js` is the Javascript file that initiates an HTTP GET request to the `SingleStarServlet`. After the response is returned, `single-star.js` populates the table using the data it gets.

- `single-star.html` is the HTML file that imports jQuery, Bootstrap, and `single-star.js`. It also contains the initial skeleton for the movies table.

### Separating frontend and backend
- For project 1, you are recommended to separate frontend and backend. Backend Java Servlet only provides API in JSON format. Frontend Javascript code fetches the data through HTTP (ajax) requests and then display the data on the webpage. 

- This example uses `jQuery` for making HTTP requests and manipulate DOM. jQuery is relatively easy to learn compared to other frameworks. This example also includes `Bootstrap`, a popular UI framework to let you easily make your webpage look fancy. 


### DataSource
- For project 1, you are recommended to use tomcat to manage your DataSource instead of manually define MySQL connection in each of the servlet.

- `WebContent/META-INF/context.xml` contains a DataSource, with database information stored in it.
`WEB-INF/web.xml` registers the DataSource to name jdbc/moviedbexample, which could be referred to anywhere in the project.

- In both `SingleStarServlet.java` and `StarsServlet.java`, a private DataSource reference dataSource is created with `@Resource` annotation. It is a reference to the DataSource `jdbc/moviedbexample` we registered in `web.xml`

- To use DataSource, you can create a new connection to it by `dataSource.getConnection()`, and you can use the connection as previous examples.
