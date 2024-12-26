document.getElementById('play-button').addEventListener('click', function () {
    var data = { "skin": document.getElementById('skin').value };
    fetch('/play', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('reel1').textContent = data.reel1;
            document.getElementById('reel2').textContent = data.reel2;
            document.getElementById('reel3').textContent = data.reel3;
            document.getElementById('message').textContent = data.message;
        })
        .catch(error => console.error('Error:', error));
});

document.getElementById('show-wins-button').addEventListener('click', function () {
    fetch('/wins', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('message').textContent = "Victorias: " + data;
        })
        .catch(error => console.error('Error:', error));
});
