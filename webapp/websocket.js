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
        console.log("received text : " + message);
        document.getElementById("chatlog").textContent += message.data + "\n" + "!-----------------------!" + "\n";
    } else {
        console.log("received graphic size: " + message.data.size);
        ddddd(message.data);
    }
}

function ddddd(blob) {
    var canvas = document.getElementById("myCanvas2");
    var context = canvas.getContext("2d");
    var buffer = new ArrayBuffer(blob.size);
    var bytes = new Uint8Array(buffer);
    for (var i = 0; i < blob.size; i++) {
        bytes[i] = blob[i];
    }
    console.log('drawImageBinary (bytes.length): ' + bytes.length);

    var imageData = context.createImageData(canvas.width, canvas.height);

    var stop = 1;
    for (var i = 8; i < imageData.data.length; i++) {
        imageData.data[i] = bytes[i];
        if (bytes[i] != 0 && bytes[i] != 255 && stop != 100) {
            stop++;
            console.log('byte = ' + i + " " + bytes[i]);
        }
    }
    context.putImageData(imageData, 0, 0);

    var img = document.createElement('img');
    img.height = canvas.height;
    img.width = canvas.width;
    img.src = canvas.toDataURL();
}

function drawImageBinary(imgString) {
    var canvas = document.getElementById("myCanvas2");
    var ctx = canvas.getContext("2d");

    var image = new Image();
    image.src = imgString;
    image.onload = function () {
        ctx.drawImage(image, 0, 0);
    };
}

function postToServer() {
    websocket.send(document.getElementById("msg").value);
    document.getElementById("msg").value = "";
}

function postBinaryToServer() {
    console.log("postBinaryToServer");
    var c = document.getElementById("myCanvas");
    var ctx = c.getContext("2d");
    var img = document.getElementById("testImg");
    ctx.drawImage(img, 0, 0);


    var image = ctx.getImageData(0, 0, c.width, c.height);
    console.log("image data length " + image.data.length);
    var buffer = new ArrayBuffer(image.data.length);
    var bytes = new Uint8Array(buffer);
    for (var i = 0; i < bytes.length; i++) {
        bytes[i] = image.data[i];
    }
    sendBinary(bytes);
}

function sendBinary(bytes) {
//    var buffer = new ArrayBuffer(2500);
//    var bytes = new Uint8Array(buffer);
//    for (var i = 0; i < bytes.length; i++) {
//        bytes[i] = 255;
//    }
    websocket.send(bytes);

    //websocket.send(bytes.value);
    console.log("sending binary_finish.");
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