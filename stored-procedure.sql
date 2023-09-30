DELIMITER $$
CREATE PROCEDURE add_star(
  IN star_name VARCHAR(100),
  IN star_birth_year INTEGER,
  OUT new_id VARCHAR(10)
)
BEGIN
  DECLARE max_id VARCHAR(10);

  -- Get the current maximum ID in the "stars" table
  SELECT MAX(id) INTO max_id FROM stars;

  -- Generate a new ID by incrementing the current maximum
  SET new_id = CONCAT('nm', LPAD(SUBSTRING(max_id, 3) + 1, 7, '0'));

  -- Insert the new star into the "stars" table
  INSERT INTO stars (id, name, birthYear)
  VALUES (new_id, star_name, star_birth_year);
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE add_star_no_output(
  IN star_name VARCHAR(100),
  IN star_birth_year INTEGER
)
BEGIN
  DECLARE max_id VARCHAR(10);
  DECLARE new_id VARCHAR(10);

  -- Get the current maximum ID in the "stars" table
  SELECT MAX(id) INTO max_id FROM stars;

  -- Generate a new ID by incrementing the current maximum
  SET new_id = CONCAT('nm', LPAD(SUBSTRING(max_id, 3) + 1, 7, '0'));

  -- Insert the new star into the "stars" table
  INSERT INTO stars (id, name, birthYear)
  VALUES (new_id, star_name, star_birth_year);
END $$
DELIMITER ;

DELIMITER $$

CREATE PROCEDURE add_movie(
  IN new_title VARCHAR(100),
  IN new_year INTEGER,
  IN new_director VARCHAR(100),
  IN star_name VARCHAR(100),
  IN star_birth_year INTEGER,
  IN genre_name VARCHAR(100),
  OUT new_movie_id VARCHAR(10),
  OUT star_id VARCHAR(10),
  OUT genre_id VARCHAR(10)
)
BEGIN
  DECLARE max_id VARCHAR(10);
  DECLARE movie_id VARCHAR(10);

  -- Check if the star exists; if not, add the star
  SELECT id INTO star_id FROM stars WHERE name = star_name LIMIT 1;
  IF star_id IS NULL THEN
    SELECT MAX(id) INTO max_id FROM stars;
    SET star_id = CONCAT('nm', LPAD(SUBSTRING(max_id, 3) + 1, 7, '0'));
    INSERT INTO stars (id, name, birthYear) VALUES (star_id, star_name, star_birth_year);
  END IF;

  -- Check if the genre exists; if not, add the genre
  SELECT id INTO genre_id FROM genres WHERE name = genre_name LIMIT 1;
  IF genre_id IS NULL THEN
    SELECT MAX(id) + 1 INTO genre_id FROM genres;
    INSERT INTO genres (id, name) VALUES (genre_id, genre_name);
  END IF;

  -- Check if the movie exists; if not, add the movie
  SELECT id INTO movie_id FROM movies WHERE title = new_title AND year = new_year AND director = new_director LIMIT 1;
  IF movie_id IS NULL THEN
    SELECT MAX(id) INTO max_id FROM movies;
    SET movie_id = CONCAT('tt', LPAD(SUBSTRING(max_id, 3) + 1, 7, '0'));
    SET new_movie_id = movie_id;
    INSERT INTO movies (id, title, year, director) VALUES (movie_id, new_title, new_year, new_director);
    INSERT INTO stars_in_movies (starId, movieId) VALUES (star_id, movie_id);
    INSERT INTO genres_in_movies (genreId, movieId) VALUES (genre_id, movie_id);
  ELSE
    SELECT 'Movie already exists' AS message;
  END IF;
END $$

DELIMITER ;

DELIMITER $$
CREATE PROCEDURE add_movie_with_genres(
  IN new_title VARCHAR(100),
  IN new_year INTEGER,
  IN new_director VARCHAR(100),
  IN genre_name VARCHAR(100)
)
BEGIN
  DECLARE genre_id VARCHAR(10);
  DECLARE movie_id VARCHAR(10);
  DECLARE new_movie_id VARCHAR(10);
  DECLARE max_id VARCHAR(10);

  -- Check if the genre exists; if not, add the genre
  SELECT id INTO genre_id FROM genres WHERE name = genre_name LIMIT 1;
  IF genre_id IS NULL THEN
	SELECT MAX(id) + 1 INTO genre_id FROM genres;
    INSERT INTO genres (id, name) VALUES (genre_id, genre_name);
  END IF;

  -- Check if the movie exists; if not, add the movie
  SELECT id INTO movie_id FROM movies WHERE title = new_title AND year = new_year AND director = new_director LIMIT 1;
  IF movie_id IS NULL THEN
	SELECT MAX(id) INTO max_id FROM movies;
	SET movie_id = CONCAT('tt', LPAD(SUBSTRING(max_id, 3) + 1, 7, '0'));
    SET new_movie_id = movie_id;
    INSERT INTO movies (id, title, year, director) VALUES (movie_id, new_title, new_year, new_director);
    INSERT INTO genres_in_movies (genreId, movieId) VALUES (genre_id, movie_id);
  ELSE
  -- Movie already exists, only add star and genre
    INSERT INTO genres_in_movies (genreId, movieId) VALUES (genre_id, movie_id);
  END IF;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE add_movie_with_stars(
  IN new_title VARCHAR(100),
  IN new_year INTEGER,
  IN new_director VARCHAR(100),
  IN star_name VARCHAR(100)
)
BEGIN
  DECLARE star_id VARCHAR(10);
  DECLARE movie_id VARCHAR(10);
  DECLARE new_movie_id VARCHAR(10);
  DECLARE max_id VARCHAR(10);
  
  SELECT id INTO star_id FROM stars WHERE name = star_name LIMIT 1;

  -- Check if the movie exists; if not, add the movie
  SELECT id INTO movie_id FROM movies WHERE title = new_title AND year = new_year AND director = new_director LIMIT 1;
  SELECT MAX(id) INTO max_id FROM movies;
  SET movie_id = CONCAT('tt', LPAD(SUBSTRING(max_id, 3) + 1, 7, '0'));
  SET new_movie_id = movie_id;
  INSERT INTO stars_in_movies (starId, movieId) VALUES (star_id, movie_id);
END $$

DELIMITER ;