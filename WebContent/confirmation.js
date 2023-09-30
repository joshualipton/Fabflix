function convertMovieId(movieId) {
    try {
        const last3Chars = movieId.slice(-3);
        const value = Number(last3Chars) / 100;
        let formattedValue = value.toFixed(2);
        if (formattedValue === '0.00') {
            formattedValue = '3.00';
        }
        return formattedValue;
    } catch (e) {
        return '3.00';
    }
}

function handleConfirmationResults(resultData) {
    console.log(resultData);
    let movieTableBodyElement = jQuery("#movie_table_body_confirmation");
    movieTableBodyElement.empty();

    console.log(resultData['previousItems']);
    for (const key in resultData['previousItems']) {
       let [movieId,movieTitle] = key.split(":", 2);
       let quantity = resultData['previousItems'][key];
       let salesId = resultData[movieId]['sale_id'];

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + salesId + "</th>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + movieId + '">'
            + movieTitle +     // display movie_title for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + quantity + "</th>";
        rowHTML += "<th>" + '$' + convertMovieId(movieId) + "</th>";
        rowHTML += '<th class="total-price-field">$' + (convertMovieId(movieId) * quantity).toFixed(2) + '</th>';
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    updateTotalPrice()
}

function updateTotalPrice() {
    // get all elements with class "total-price-field"
    let totalFields = document.querySelectorAll('.total-price-field');

    // initialize the total price variable
    let totalPrice = 0;

    // loop through each total price field and add up the price in that row
    totalFields.forEach(function(totalField) {
        // parse the total price as a float
        let total = parseFloat(totalField.innerText.replace('$', ''));

        // add it to the running total
        totalPrice += total;
    });

    // update the final price element with the total price
    let finalPriceElement = document.getElementById('final-price');
    finalPriceElement.innerText = '$' + totalPrice.toFixed(2);
}

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/confirmation",
    success: (resultData) => handleConfirmationResults(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});