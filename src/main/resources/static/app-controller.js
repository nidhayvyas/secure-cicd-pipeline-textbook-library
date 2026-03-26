console.log('We are inside client.js');

/* on page load  */
window.onload = function() {
    fetch("/os", {
            method: "GET"
        })
        .then(function(res) {
            if (res.ok) {
                return res.json();
            }
            throw new Error('Request failed');
        }).catch(function(error) {
            console.log(error);
        })
        .then(function(data) {
            document.getElementById('hostname').innerHTML = `Pod: ${data.os}`
        });
};

const btn = document.getElementById('submit');
if (btn) {
    btn.addEventListener('click', func);
}

function func() {
    const textbook_id = document.getElementById("textbookID").value
    console.log("onClick Submit - Request Textbook ID - " + textbook_id)

    fetch("/textbook", {
            method: "POST",
            body: JSON.stringify({
                id: document.getElementById("textbookID").value
            }),
            headers: {
                "Content-type": "application/json; charset=UTF-8"
            }
        })
        .then(function(res2) {
            if (res2.ok) {
                return res2.json();
            }
            throw new Error('Request failed.');
        }).catch(function(error) {
            alert("Please enter a valid textbook ID (0-8)")
            console.log(error);
        })
        .then(function(data) {
            if (data) {
                document.getElementById('resultCard').classList.add('active');
                document.getElementById('textbookName').innerHTML = data.name;
                document.getElementById('textbookAuthor').innerHTML = data.author;
                document.getElementById('textbookEdition').innerHTML = data.edition;
                document.getElementById('textbookDescription').innerHTML = data.description;

                const element = document.getElementById("textbookImage");
                element.style.backgroundImage = "url(" + data.image + ")";
            }
        });
}
