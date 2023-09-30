CREATE DATABASE moviedb;

USE moviedb;

CREATE TABLE movies (
  id VARCHAR(10) PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  year INTEGER NOT NULL,
  director VARCHAR(100) NOT NULL
);

CREATE TABLE stars (
  id VARCHAR(10) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  birthYear INTEGER
);

CREATE TABLE stars_in_movies (
  starId VARCHAR(10) NOT NULL REFERENCES stars(id),
  movieId VARCHAR(10) NOT NULL REFERENCES movies(id)
);

CREATE TABLE genres (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL
);

CREATE TABLE genres_in_movies (
  genreId INTEGER NOT NULL REFERENCES genres(id),
  movieId VARCHAR(10) NOT NULL REFERENCES movies(id)
);

CREATE TABLE customers (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  firstName VARCHAR(50) NOT NULL,
  lastName VARCHAR(50) NOT NULL,
  ccId VARCHAR(20) NOT NULL REFERENCES creditcards(id),
  address VARCHAR(200) NOT NULL,
  email VARCHAR(50) NOT NULL,
  password VARCHAR(20) NOT NULL
);

CREATE TABLE sales (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  customerId INTEGER NOT NULL REFERENCES customers(id),
  movieId VARCHAR(10) NOT NULL REFERENCES movies(id),
  saleDate DATE NOT NULL
);

CREATE TABLE creditcards (
  id VARCHAR(20) PRIMARY KEY,
  firstName VARCHAR(50) NOT NULL,
  lastName VARCHAR(50) NOT NULL,
  expiration DATE NOT NULL
);

CREATE TABLE ratings (
  movieId VARCHAR(10) NOT NULL REFERENCES movies(id),
  rating FLOAT NOT NULL,
  numVotes INTEGER NOT NULL
);

ALTER TABLE movies ADD INDEX idx_movies_id (id);
ALTER TABLE movies ADD INDEX idx_movies_title_year_director (title, year, director);

ALTER TABLE genres_in_movies ADD INDEX idx_genres_in_movies_movieId_genreId (movieId, genreId);
ALTER TABLE genres_in_movies ADD INDEX idx_genres_in_movies_genreId_movieId (genreId, movieId);

ALTER TABLE genres ADD INDEX idx_genres_id (id);
ALTER TABLE genres ADD INDEX idx_genres_name (name);

ALTER TABLE stars_in_movies ADD INDEX idx_stars_in_movies_movieId_starId (movieId, starId);
ALTER TABLE stars_in_movies ADD INDEX idx_stars_in_movies_starId_movieId (starId, movieId);
ALTER TABLE stars_in_movies ADD INDEX idx_stars_in_movies_movieId (movieId);
ALTER TABLE stars_in_movies ADD INDEX idx_stars_in_movies_starId (starId);


ALTER TABLE stars ADD INDEX idx_stars_id (id);
ALTER TABLE stars ADD INDEX idx_stars_name (name);

ALTER TABLE ratings ADD INDEX idx_ratings_movieId (movieId, rating);



