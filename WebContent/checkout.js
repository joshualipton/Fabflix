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

function handlePaymentResult(resultDataString) {
    console.log(resultDataString)
    let resultDataJson = JSON.parse(resultDataString);

    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#checkout_error_message").text(resultDataJson["message"]);
    }
}

const form = document.getElementById('payment_form');

form.addEventListener('submit', (event) => {
    event.preventDefault();

    // get the values of the form fields
    const firstName = form.elements['firstName'].value;
    const lastName = form.elements['lastName'].value;
    const cardNumber = form.elements['cardNumber'].value;
    const expDate = form.elements['expDate'].value;

    console.log(firstName, lastName, cardNumber, expDate);
    $.ajax("api/checkout", {
        method: "POST",
        data: {firstName: firstName, lastName: lastName, cardNumber: cardNumber, expDate: expDate},
        success: handlePaymentResult
    });
});

let total = getParameterByName('total');
const totalPriceElement = document.getElementById('total-price');
totalPriceElement.innerText = '$' + total;