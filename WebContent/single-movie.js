/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {
    console.log("handleResult: populating movie info from resultData");
    let moviePageTitle = jQuery("#single_movie_page_title");
    moviePageTitle.append(`${resultData[0]["movie_title"]} - Fabflix`);

    let movieTitleNavbar = jQuery("#movie_title_navbar");
    movieTitleNavbar.append(`${resultData[0]["movie_title"]}`);


    // populate the movie info h3
    // find the empty h3 body by id "movie_info"

    let movieInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>" + resultData[0]["movie_title"] + "</p>" +
        "<p>(" + resultData[0]["movie_year"] + ")</p>");
    movieInfoElement.append(movieInfoElement);
    console.log("handleResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let starsArr = resultData[i]["movie_stars"].split(",");
        let starRow = "";
        for (let j = 0; j < starsArr.length; j++) {
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
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + genreRow  + "</th>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            starRow +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += `<th><button class='cart-button' id='cart-button-${movieId}' data-movie-id='${movieId}' data-movie-title='${resultData[i]['movie_title']}' style='cursor: pointer;'>Cart</button></th>`;
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    let cartButtons = document.querySelectorAll(".cart-button");
    cartButtons.forEach(function(button) {
        button.addEventListener("click", function(event) {
            console.log(`Button with id "${event.target.id}" was clicked`);
            let movieID = movieId;
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
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by MoviesServlet in Movies.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});