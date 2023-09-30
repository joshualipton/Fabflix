/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {
        // Concatenate the html tags with resultData jsonObject
        let starsArr = resultData[i]["movie_stars"].split(",");
        let starRow = "";
        for (let j = 0; j < starsArr.length; j ++) {
            let starRowSplit = starsArr[j].split("(");
            let starName = starRowSplit[0];
            starName = starName.substring(0, starName.length - 1);
            if (j < starsArr.length - 1) {
                starName += ",";
            }
            let starID = starRowSplit[1];
            starID = starID.substring(0, starID.length - 1);
            starRow += '<a href="single-star.html?id=' + starID + '">'
                + starName +     // display movie_stars for the link text
                '</a>';
        }
        starRow = starRow.substring(0, starRow.length - 1);

        let genreArr = resultData[i]["movie_genres"].split(",");
        let genreRow = "";
        for (let j = 0; j < genreArr.length; j++) {
            let genre = genreArr[j];
            if (j < genreArr.length - 1) {
                genre += ",";
                genreRow += '<a href="/fabflix/results.html?genre=' + genre.substring(0, genre.length - 1) + '">'
                    + genre +     // display movie_stars for the link text
                    '</a>';
            } else {
                genreRow += '<a href="/fabflix/results.html?genre=' + genre + '">'
                    + genre +     // display movie_stars for the link text
                    '</a>';
            }
        }
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display movie_title for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + genreRow  + "</th>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            starRow +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += `<th><button class='cart-button' id='cart-button-${resultData[i]['movie_id']}' data-movie-id='${resultData[i]['movie_id']}' data-movie-title='${resultData[i]['movie_title']}' style='cursor: pointer;'>Cart</button></th>`;
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    let cartButtons = document.querySelectorAll(".cart-button");
    cartButtons.forEach(function(button) {
        button.addEventListener("click", function(event) {
            console.log(`Button with id "${event.target.id}" was clicked`);
            let movieID = button.dataset.movieId;
            let movieTitle = button.dataset.movieTitle;

            $.ajax("api/cart", {
                method: "POST",
                data: {itemId: movieID, itemTitle: movieTitle, minus: false, delete: false},
                success: function(resultDataString) {
                    alert(movieTitle + " added to cart");
                }
            });
        });
    });
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleMovieResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by MoviesServlet in Movies.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MoviesServlet
});