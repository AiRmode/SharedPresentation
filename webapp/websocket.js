/**
 * Created by ashevchuk on 08.06.14.
 */
//var wsURI = "ws://192.168.1.102:8080" + "/sharedpresentation";
var wsURI = "ws://" + document.location.host + document.location.pathname + "sharedpresentation";
var websocket = new WebSocket(wsURI);
var output = document.getElementById("output");

websocket.onopen = function (evt) {
    onOpen(evt);
};

websocket.onerror = function (evt) {
    onError(evt);
};

websocket.onmessage = function (message) {
    onMessage(message);
};

function onMessage(message) {
    if (typeof message.data == "string") {
        var searchResult = message.data.search("data:image/png;base64,|data:image/jpg;base64,");
        if (searchResult == 0) {
            drawImageBinary(message.data);
        } else {
            document.getElementById("chatlog").textContent += message.data + "\n" + "!-----------------------!" + "\n";
        }
    } else {
        console.log("received graphic size: " + message.data.size);
    }
}

function drawImageBinary(imgString) {
    var canvas = document.getElementById("myCanvas");
    var ctx = canvas.getContext("2d");

    var image = new Image();
    image.src = imgString;
    image.onload = function () {
        ctx.drawImage(image, 0, 0);
    };
}

function postTextMessageToServer() {
    websocket.send(document.getElementById("msg").value);
    document.getElementById("msg").value = "";
}

function postBinaryToServer() {
    console.log("postBinaryToServer");
    var c = document.getElementById("myCanvas");
    var ctx = c.getContext("2d");

    var image = ctx.getImageData(10, 10, c.width, c.height);
    console.log("image data length " + image.data.length);
    var buffer = new ArrayBuffer(image.data.length);
    var bytes = new Uint8Array(buffer);
    for (var i = 0; i < bytes.length; i++) {
        bytes[i] = image.data[i];
    }

    sendBinary(buffer);
}

function sendBinary(bytes) {
    websocket.send(bytes);
}

function onOpen(evt) {
    writeToScreen("Connected to: " + wsURI + "\n" + evt.data);
}

function onError(evt) {
    writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
}

function writeToScreen(message) {
    output.innerHTML += message + "<br>";
}