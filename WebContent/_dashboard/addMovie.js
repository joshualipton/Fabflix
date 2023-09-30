let movie_form = $("#add-movie-form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleMovieResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle movie response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        $("#add-movie-text-success").text(resultDataJson["message"]);
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#add-movie-text-error").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitMovieForm(formSubmitEvent) {
    console.log("submit movie form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/add-movie", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: movie_form.serialize(),
            success: handleMovieResult
        }
    );
}

movie_form.submit(submitMovieForm);
console.log(movie_form, movie_form.submit());