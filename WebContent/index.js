/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleGenreResult(resultDataArray) {
    console.log(resultDataArray);
    let genres_div = jQuery("#genres");
    genres_div.empty();

    console.log("handle movie response");
    console.log(resultDataArray);

    // If login succeeds, it will redirect the user to index.html
    for (let i = 0; i < resultDataArray.length; i++) {
        let genre = resultDataArray[i];
        console.log(genre)
        genres_div.append("<a class=\"browse-genre\" href=\"/fabflix/results.html?genre=" + genre + "\" style=\"\n" +
            "    font-size: 24px;\n" +
            "    color: #30a1ff;\n" +
            "\">" + genre + "</a>");
    }
}

$.ajax(
    "api/genres", {
        method: "GET",
        // Serialize the login form to the data sent by POST request
        success: handleGenreResult
    }
);