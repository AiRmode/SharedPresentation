/**
 * Created by ashevchuk on 08.06.14.
 */
//var wsURI = "ws://192.168.1.102:8080" + "/sharedpresentation";
var wsURI = "ws://" + document.location.host + document.location.pathname + "sharedpresentation";
var websocket = new WebSocket(wsURI);
var output = document.getElementById("output");
var canvas = document.getElementById("myCanvas");
var ctx = canvas.getContext("2d");
var savedImage = "";

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
            savedImage = message.data;
            drawImageBinary(message.data);
        } else {
            document.getElementById("chatlog").textContent += message.data + "\n" + "!-----------------------!" + "\n";
        }
    } else {
        console.log("received graphic size: " + message.data.size);
    }
}

function drawImageBinary(imgString) {
    var image = new Image();
    image.src = imgString;

    // Store the current transformation matrix
    ctx.save();
    // Use the identity matrix while clearing the canvas
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    // Restore the transform
    ctx.restore();

    image.onload = function () {
        // resizes image to a target size of 500x500 using letterbox mode
        var result = scaleImage(image.width, image.height, canvas.width, canvas.height, true);
        ctx.drawImage(image, result.targetleft, result.targettop, result.width, result.height);
    };

}

function showOriginaImage() {
    console.log("You have clicked on image");
    var cnv = document.getElementById('myCanvas');

    if (!cnv || !cnv.getContext) {
        return;
    }

    if (cnv && cnv.getContext) {
        var scr = cnv.getContext('2d');
        if (!scr) {
            return;
        }
    }

    var img = new Image();
    img.src = savedImage;

    canvas.width = img.width;
    canvas.height = img.height;

    scr.fillStyle = '#00';
    scr.fillRect(0, 0, cnv.width, cnv.height);

    img.onload = function() {
        scr.drawImage(img, 0, 0, cnv.width, cnv.height);
    }

    console.log("finish You have clicked on image");
}

function postTextMessageToServer() {
    websocket.send(document.getElementById("msg").value);
    document.getElementById("msg").value = "";
}

function postBinaryToServer() {
    console.log("postBinaryToServer");
    var c = document.getElementById("myCanvas");
    var ctx1 = c.getContext("2d");

    var image = ctx1.getImageData(10, 10, c.width, c.height);
    console.log("image data length " + image.data.length);
    var buffer = new ArrayBuffer(image.data.length);
    var bytes = new Uint8Array(buffer);
    for (var i = 0; i < bytes.length; i++) {
        bytes[i] = image.data[i];
    }

    sendBinary(buffer);
}

function getFullPicture(){

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

function scaleImage(srcwidth, srcheight, targetwidth, targetheight, fLetterBox) {

    var result = { width: 0, height: 0, fScaleToTargetWidth: true };

    if ((srcwidth <= 0) || (srcheight <= 0) || (targetwidth <= 0) || (targetheight <= 0)) {
        return result;
    }

    // scale to the target width
    var scaleX1 = targetwidth;
    var scaleY1 = (srcheight * targetwidth) / srcwidth;

    // scale to the target height
    var scaleX2 = (srcwidth * targetheight) / srcheight;
    var scaleY2 = targetheight;

    // now figure out which one we should use
    var fScaleOnWidth = (scaleX2 > targetwidth);
    if (fScaleOnWidth) {
        fScaleOnWidth = fLetterBox;
    }
    else {
        fScaleOnWidth = !fLetterBox;
    }

    if (fScaleOnWidth) {
        result.width = Math.floor(scaleX1);
        result.height = Math.floor(scaleY1);
        result.fScaleToTargetWidth = true;
    }
    else {
        result.width = Math.floor(scaleX2);
        result.height = Math.floor(scaleY2);
        result.fScaleToTargetWidth = false;
    }
    result.targetleft = Math.floor((targetwidth - result.width) / 2);
    result.targettop = Math.floor((targetheight - result.height) / 2);

    return result;
}