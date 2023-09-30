let cart = $("#cart");

/**
 * Handle the data returned by CartServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    console.log(resultDataString);
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);
}

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

/**
 * Handle the items in item list
 * @param resultMap jsonObject, needs to be parsed to html
 */
function handleCartArray(resultMap) {
    console.log(resultMap);
    let item_list = $("#movie_table_body_results");
    let cartItems = {};
    item_list.empty();

    for (const key in resultMap) {
        const quantity = resultMap[key];
        console.log(key, quantity);
        const firstColonIndex = key.indexOf(":");
        const movieID = key.substring(0, firstColonIndex);
        const movieTitle = key.substring(firstColonIndex + 1);

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            '<a href="single-movie.html?id=' + movieID + '">'
            + movieTitle +
            '</a>' +
            "</th>";
        rowHTML += '<th class="quantity-field" style="display: flex; gap: 1vw;">'
            + '<button class="minus-btn" data-movie-id="' + movieID + '" style="cursor: pointer;">-</button>'
            + quantity
            + '<button class="plus-btn" data-movie-id="' + movieID + '" style="cursor: pointer;">+</button>'
            + '</th>';
        rowHTML += '<th><button class="delete-btn" data-movie-id="' + movieID + '" style="cursor: pointer; color: white; background: #de3434;">Delete</button></th>';
        rowHTML += '<th class="unit-price-field">' + '$' + convertMovieId(movieID) + '</th>';
        rowHTML += '<th class="total-price-field">$' + (convertMovieId(movieID) * quantity).toFixed(2) + '</th>';
        rowHTML += "</tr>";
        item_list.append(rowHTML);

        let minusBtn = $('.minus-btn[data-movie-id="' + movieID + '"]');
        let plusBtn = $('.plus-btn[data-movie-id="' + movieID + '"]');
        let deleteBtn = $('.delete-btn[data-movie-id="' + movieID + '"]');

        minusBtn.on('click', function() {
            updateTotalPrice();
            // let finalPriceElement = document.getElementById('final-price');
            // let unitPrice = parseFloat(convertMovieId(movieID));
            // let finalPrice = parseFloat(finalPriceElement.innerText.substring(1)) - unitPrice;
            // finalPriceElement.innerText = '$' + finalPrice.toFixed(2);
            $.ajax("api/cart", {
                method: "POST",
                data: {itemId: movieID, itemTitle: movieTitle, minus: true, delete: false},
            });
            $.ajax("api/cart", {
                method: "GET",
                success: handleSessionData
            });
        });

        plusBtn.on('click', function() {
            updateTotalPrice();
            // let finalPriceElement = document.getElementById('final-price');
            // let unitPrice = parseFloat(convertMovieId(movieID));
            // let finalPrice = parseFloat(finalPriceElement.innerText.substring(1)) + unitPrice;
            // finalPriceElement.innerText = '$' + finalPrice.toFixed(2);
            $.ajax("api/cart", {
                method: "POST",
                data: {itemId: movieID, itemTitle: movieTitle, minus: false, delete: false},
            });
            $.ajax("api/cart", {
                method: "GET",
                success: handleSessionData
            });
        });

        deleteBtn.on('click', function() {
            // let finalPriceElement = document.getElementById('final-price');
            // let unitPrice = parseFloat(convertMovieId(movieID));
            // let finalPrice = parseFloat(finalPriceElement.innerText.substring(1)) - unitPrice;
            // finalPriceElement.innerText = '$' + finalPrice.toFixed(2);
            $.ajax("api/cart", {
                method: "POST",
                data: {itemId: movieID, itemTitle: movieTitle, minus: false, delete: true},
            });
            $.ajax("api/cart", {
                method: "GET",
                success: handleSessionData
            });
            updateTotalPrice();
        });
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

const checkoutButton = document.getElementById('checkout-button');
checkoutButton.addEventListener('click', () => {
    let finalPriceElement = document.getElementById('final-price');
    let finalPrice = finalPriceElement.innerText.replace('$', '');
    window.location.href = `checkout.html?total=${finalPrice}`;
});

$.ajax("api/cart", {
    method: "GET",
    success: handleSessionData
});