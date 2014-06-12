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
        drawImageBinary2(message.data);
    }
}

function drawImageBinary2(imgString) {
    var canvas = document.getElementById("myCanvas2");
    var ctx = canvas.getContext("2d");

    var image = new Image();
    image.src = imgString;
    image.onload = function () {
        ctx.drawImage(image, 10, 10);
    };
}

function drawImageBinary(imgString) {
    imgString = 'data:image/gif;base64,R0lGODlhDAAMAOYAANPe5Pz//4KkutDb4szY3/b+/5u5z/3//3KWrfn//8rk8naasYGkuszY4Mbg8qG+0dzv9tXg5sTg8t/o7vP8/4iqv9ft9NPe5qfD1Mfc56O/0YKlu+Lr8M3Z4JCwxuj2/Of0+eDz9+rw9Z68z8/n8sHe8sbT3Ju6zuDv96nE1Onw9Nbh6cvX39Hq89Hq8u77/srW3tbh54Kku8ba56TD1u37/vL8/vL8/9ft9ebu8+Ps8bzM1Ymsw7XR4Nnj6Yanvsnj8qrI2Or2/NTf5tvl68vY3+r3/HqdtNji6OXt8eDz+dLc477c7bDO3t7n7d7v9s3Z4dbs9N/y98Pd6PX+/8/b4f7//+Hp7tDo8vv//+fu84GjunKWro6uxHqctOfu9P///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAAAAAAALAAAAAAMAAwAAAeEgCJfg4RfWlo5KlpgjI2OOklWBwcBAVmXCQlXHAUFVBkGBjMUNzZOEy81IF2sXUZCH0QrDyhPGzICAkohUj4XHhoQKQsLGDgWUTFIJxUjUy0uWNIkQxE9W9gMDD9BCgpLAEBNXl5H5F40DlUDEkxc71wICDwlDQBQHQ0EBEUsJjswBgQCADs=';
    var canvas = document.getElementById("myCanvas3");
    var ctx = canvas.getContext("2d");

    var image = new Image();
    image.src = imgString;
    image.onload = function () {
        ctx.drawImage(image, 10, 10);
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
    var image2 = 'data:image/gif;base64,R0lGODlhDAAMAOYAANPe5Pz//4KkutDb4szY3/b+/5u5z/3//3KWrfn//8rk8naasYGkuszY4Mbg8qG+0dzv9tXg5sTg8t/o7vP8/4iqv9ft9NPe5qfD1Mfc56O/0YKlu+Lr8M3Z4JCwxuj2/Of0+eDz9+rw9Z68z8/n8sHe8sbT3Ju6zuDv96nE1Onw9Nbh6cvX39Hq89Hq8u77/srW3tbh54Kku8ba56TD1u37/vL8/vL8/9ft9ebu8+Ps8bzM1Ymsw7XR4Nnj6Yanvsnj8qrI2Or2/NTf5tvl68vY3+r3/HqdtNji6OXt8eDz+dLc477c7bDO3t7n7d7v9s3Z4dbs9N/y98Pd6PX+/8/b4f7//+Hp7tDo8vv//+fu84GjunKWro6uxHqctOfu9P///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAAAAAAALAAAAAAMAAwAAAeEgCJfg4RfWlo5KlpgjI2OOklWBwcBAVmXCQlXHAUFVBkGBjMUNzZOEy81IF2sXUZCH0QrDyhPGzICAkohUj4XHhoQKQsLGDgWUTFIJxUjUy0uWNIkQxE9W9gMDD9BCgpLAEBNXl5H5F40DlUDEkxc71wICDwlDQBQHQ0EBEUsJjswBgQCADs=';
    console.log("image data length " + image.data.length);
    var buffer = new ArrayBuffer(image.data.length);
    var bytes = new Uint8Array(buffer);
    for (var i = 0; i < bytes.length; i++) {
        bytes[i] = image.data[i];
    }

    drawImageBinary(bytes);
//    ddddd(bytes);

//    sendBinary(bytes);
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