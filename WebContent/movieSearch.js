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

let sessionParams;

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleMovieResultSearch(resultData) {
    sessionParams = resultData[resultData.length - 1];

    if (resultData.length === 1) {
        let prefix = sessionParams['prefix'];
        let genre = sessionParams['genre'];
        let movieTitle = sessionParams['searchTitle'];
        let movieYear = sessionParams['searchYear'];
        let movieDirector = sessionParams['searchDirector'];
        let movieStar = sessionParams['searchStar'];
        let limit = sessionParams['limit'];
        let offset = sessionParams['offset'];
        let sort = sessionParams['sort'];
        let newUrl = "results.html?prefix=" + prefix + "&genre=" + genre + "&title=" + movieTitle + "&year=" + movieYear + "&director=" + movieDirector + "&star=" + movieStar + "&limit=" + limit + "&offset=" + offset  + "&sort=" + sort;
        history.pushState(null, null, newUrl);
        // window.location.replace("results.html?prefix=" + prefix + "&genre=" + genre + "&title=" + movieTitle + "&year=" + movieYear + "&director=" + movieDirector + "&star=" + movieStar + "&limit=" + limit + "&offset=" + offset  + "&sort=" + sort);
        return
    }

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body_results");
    movieTableBodyElement.empty();

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < resultData.length - 1; i++) {
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
    let prefix = sessionParams['prefix'];
    let genre = sessionParams['genre'];
    let movieTitle = sessionParams['searchTitle'];
    let movieYear = sessionParams['searchYear'];
    let movieDirector = sessionParams['searchDirector'];
    let movieStar = sessionParams['searchStar'];
    let limit = sessionParams['limit'];
    let offset = sessionParams['offset'];
    let sort = sessionParams['sort'];
    let newUrl = "results.html?prefix=" + prefix + "&genre=" + genre + "&title=" + movieTitle + "&year=" + movieYear + "&director=" + movieDirector + "&star=" + movieStar + "&limit=" + limit + "&offset=" + offset  + "&sort=" + sort;
    history.pushState(null, null, newUrl);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */


let search_form = $("#search-form");
let search_limit = $("#limit");
/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitSearchForm(formSubmitEvent) {
    movieTitle = document.getElementById("title-input").value;
    movieYear = document.getElementById("year-input").value;
    movieDirector = document.getElementById("director-input").value;
    movieStar = document.getElementById("star-input").value;
    sort = sortDropDown.value;
    limit = 10;
    offset = 0;
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
    window.location.replace("results.html?title=" + movieTitle + "&year=" + movieYear + "&director=" + movieDirector + "&star=" + movieStar + "&limit=" + limit + "&offset=" + offset + "&sort=" + sort);
    $.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/search?title=" + movieTitle + "&year=" + movieYear + "&director=" + movieDirector + "&star=" + movieStar + "&limit=" + limit + "&offset=" + offset + "&sort=" + sort,
        success: (resultData) => handleMovieResultSearch(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
    });
}

function updateSearchForm(formSubmitEvent) {
    let prefix = getParameterByName('prefix');
    let genre = getParameterByName('genre');
    let movieTitle = getParameterByName('title');
    let movieYear = getParameterByName('year');
    let movieDirector = getParameterByName('director');
    let movieStar = getParameterByName('star');
    // let limit = getParameterByName('limit');
    // let offset = getParameterByName('offset');
    // let sort = getParameterByName('sort');

    limit = document.getElementById("limit").value;
    sort = sortDropDown.value;

    $.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/search?prefix=" + prefix + "&genre=" + genre + "&title=" + movieTitle + "&year=" + movieYear + "&director=" + movieDirector + "&star=" + movieStar + "&limit=" + limit + "&offset=" + offset + "&sort=" + sort,
        success: (resultData) => handleMovieResultSearch(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
    });
    let newUrl = "results.html?prefix=" + prefix + "&genre=" + genre + "&title=" + movieTitle + "&year=" + movieYear + "&director=" + movieDirector + "&star=" + movieStar + "&limit=" + limit + "&offset=" + offset  + "&sort=" + sort;
    history.pushState(null, null, newUrl);
}

// Bind the submit action of the form to a handler function
let prefix = getParameterByName('prefix');
if (prefix === null) {
    prefix = "";
}

let genre = getParameterByName('genre');
if (genre === null) {
    genre = "";
}

let movieTitle = getParameterByName('title');
if (movieTitle === null) {
    movieTitle = "";
}

let movieYear = getParameterByName('year');
if (movieYear === null) {
    movieYear = null;
}

let movieDirector = getParameterByName('director');
if (movieDirector === null) {
    movieDirector = "";
}

let movieStar = getParameterByName('star');
if (movieStar === null) {
    movieStar = "";
}

let limit = getParameterByName('limit');
if (limit === null) {
    limit = 10;
}
limit = eval(limit);

let useSession = getParameterByName('useSession');
if (useSession === null) {
    useSession = false;
}

search_form.submit(submitSearchForm)
search_limit.change(updateSearchForm)

const leftArrow = document.getElementById("left");
const rightArrow = document.getElementById("right");
let offset = getParameterByName('offset');
if (offset === null) {
    offset = 0;
}
offset = eval(offset);
leftArrow.addEventListener("click", function() {
    if (offset >= eval(limit)) {
        offset -= eval(limit);
    }
    else {
        offset = 0;
    }
    updateSearchForm()
});

rightArrow.addEventListener("click", function() {
    offset += eval(limit);
    updateSearchForm()
});

const sortDropDown = document.getElementById("sort-dropdown");
let sort = 0;
sortDropDown.addEventListener("change", function() {
    updateSearchForm();
});

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/search?prefix=" + prefix + "&genre=" + genre + "&title=" + movieTitle + "&year=" + movieYear + "&director=" + movieDirector + "&star=" + movieStar + "&limit=" + limit + "&offset=" + offset + "&sort=" + sort + "&useSession=" + useSession,
    success: (resultData) => handleMovieResultSearch(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});

function handleCartInfo(cartEvent) {
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/cart", {
        method: "POST",
        data: cart.serialize(),
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });

    // clear input form
    cart[0].reset();
}

/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON
    let jsonData = JSON.parse(data);
    console.log(jsonData)

    // TODO: if you want to cache the result into a global variable you can do it here
    if (!sessionStorage.getItem(query)) {
        sessionStorage.setItem(query, data);
    }

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
    let autocompleteSuggestions = document.getElementsByClassName('autocomplete-suggestions')[0];
    autocompleteSuggestions.style.backgroundColor = 'white';
}

function handleCacheSuccess(jsonData, query, doneCallback) {
    console.log("query found in cache")
    jsonData = JSON.parse(jsonData);
    console.log(jsonData)
    doneCallback( { suggestions: jsonData } );
    let autocompleteSuggestions = document.getElementsByClassName('autocomplete-suggestions')[0];
    autocompleteSuggestions.style.backgroundColor = 'white';
}

/*
 * This function is called by the library when it needs to lookup a query.
 *
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")

    // TODO: if you want to check past query results first, you can do it here
    if (sessionStorage.getItem(query)) {
        // Item exists in session storage
        handleCacheSuccess(sessionStorage.getItem(query), query, doneCallback)
    } else {
        // Item does not exist in session storage
        console.log("sending AJAX request to backend Java Servlet")
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "api/movie-suggestion?query=" + escape(query),
            "success": function(data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function(errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
        })
    }

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data

}

/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(formSubmitEvent) {
    let query = document.getElementById("title-input-fulltext").value;
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    try {
        formSubmitEvent.preventDefault();
    } catch (e) {

    }
    // TODO: you should do normal search here
    // window.location.replace("results.html?title=" + query);
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/search-fulltext?query=" + query,
        success: (resultData) => handleMovieResultSearch(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
    });
}

/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    window.location.replace('single-movie.html?id=' + suggestion["data"]["id"]);

}


$('#title-input-fulltext').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
    minChars: 3,
});

// bind pressing enter key to a handler function
$('#title-input-fulltext').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        search_form_fulltext.submit(handleNormalSearch)
    }
})

let search_form_fulltext = $("#search-form-fulltext");
// search_form_fulltext.submit(handleNormalSearch)