// global Phaser

var game;
var style;
var carModel;
var carModelText;

function setBackgroundColor(color){

  game.stage.backgroundColor = color;
}

function setCarModel(carModel){

  this.carModel = carModel;
  drawCarModel();
}

function equipItem(equipItem){
 
  var btnWdth = launchButtonProperties.width;
  var btnHite = launchButtonProperties.height;
  var screenWidth = game.width;
  var screenHeight = game.height;
  var btnX = screenWidth/2 - btnWdth/2;
  var btnY = screenHeight/2 - btnHite/2;

  launchButton = game.add.sprite(btnX, btnY, 'launchButton');
  launchButton.inputEnabled = true;
}

function drawCarModel(){

  carModelText.destroy();

  carModelText = game.add.text(game.world.centerX, 0, carModel, style);

  carModelText.anchor.set(0, -1);
}

(function (Phaser) {
  'use strict';

  var isLeftUp = false;
  var isLeftDown = false;
  var isRightUp = false;
  var isRightDown = false;
  var isThrottleUp = false;
  var isThrottleDown = false;

  var throttleId = 1;
  var rightArrowId = 2;
  var leftArrowId = 3;

  var arrowImageScale = 0.25;
  var originalArrowImageWidth = 200;
  var originalArrowImageHeight = 250;
  var arrowSpriteWidth = originalArrowImageWidth * arrowImageScale;
  var arrowSpriteHeight = originalArrowImageHeight * arrowImageScale;

  var leftArrowArea;
  var rightArrowArea;
  var arrowsBackground;
  var redButtons;
  var tools_button;

  var lightGray = "0xb3b3b3"
  var offWHITE = '0xEFEFEF';

  var buttonDown = function(buttonID){};
  var buttonUp = function(buttonID){};

  var usernamePrompt = function(){  

    var newUsername = prompt("Enter your name! (q to quit)", username);

    if (newUsername == "" || newUsername == username || newUsername == null) {
        return;
    }else if(newUsername == "q"){
        sendToBackend("disconnect");
        return;
    }

    setUsername(newUsername);
    sendToBackend("set username", username);
  };

  var leftDown = function() { 
    if(!isLeftDown){ 
      isLeftDown = true; 
      isLeftUp = false; 
      buttonDown(leftArrowId);
    }
  };

  var rightDown = function(){ 
    if(!isRightDown){ 
      isRightDown = true; 
      isRightUp = false;
      buttonDown(rightArrowId);
    }
  };

  var leftUp = function(){ 
    if(!isLeftUp){ 
      isLeftUp = true; 
      isLeftDown = false; 
      buttonUp(leftArrowId);
    }
  };
  
  var rightUp = function(){ 
    if(!isRightUp){ 
      isRightUp = true; 
      isRightDown = false; 
      buttonUp(rightArrowId);
    }
  };

  var throttleDown = function(){ 
    if(!isThrottleDown){ 
      isThrottleDown = true; 
      isThrottleUp = false;
      buttonDown(throttleId);
      redButtons.frame = 1;
    }
  };

  var throttleUp = function(){ 
    if(!isThrottleUp){ 
      isThrottleUp = true; 
      isThrottleDown = false;
      buttonUp(throttleId);
      redButtons.frame = 0;
    }
  };

  function createThrottleButton(){

    redButtons = game.add.sprite(game.width/1.4, game.height/4, 'redButtons');
    redButtons.frame = 0;
    redButtons.scale.setTo(1.5,1.8);
    redButtons.inputEnabled = true;
  }

  function createArrowKeys(){

    var arrowsBackgroundX = 0;
    var arrowsBackgroundY = 0;
    var arrowsBackgroundWidth = game.stage.width / 2.5;
    var arrowsBackgroundHeight = game.stage.height;

    var margin = arrowsBackgroundHeight / 200;

    var leftArrowAreaX = arrowsBackgroundX + margin;
    var leftArrowAreaY = arrowsBackgroundY + margin;
    var amountOfMarginsWidth = 3;
    var leftArrowAreaWidth = (arrowsBackgroundWidth - amountOfMarginsWidth * margin) / 2;
    var leftArrowAreaHeight = (arrowsBackgroundHeight - 2 * margin);
    var leftArrowAreaEndX = leftArrowAreaX + leftArrowAreaWidth;
    var leftArrowAreaEndY = leftArrowAreaY + leftArrowAreaHeight;

    var rightArrowAreaX = leftArrowAreaEndX + margin;
    var rightArrowAreaY = leftArrowAreaY;
    var rightArrowAreaWidth = leftArrowAreaWidth;
    var rightArrowAreaHeight = leftArrowAreaHeight;
    var rightArrowAreaEndX = rightArrowAreaX + rightArrowAreaWidth;
    var rightArrowAreaEndY = rightArrowAreaY + rightArrowAreaHeight; 

    var middleOfLeftArrowAreaX = (leftArrowAreaX + leftArrowAreaEndX) / 2;
    var middleOfLeftArrowAreaY = (leftArrowAreaY + leftArrowAreaEndY) / 2;

    var middleOfRightArrowAreaX = (rightArrowAreaX + rightArrowAreaEndX) / 2;
    var middleOfRightArrowAreaY = (rightArrowAreaY + rightArrowAreaEndY) / 2;

    drawLeftArrowArea(leftArrowAreaX, leftArrowAreaY, leftArrowAreaWidth, leftArrowAreaHeight);
    drawRightArrowArea(rightArrowAreaX, rightArrowAreaY, rightArrowAreaWidth, rightArrowAreaHeight);

    addLeftArrowImage(middleOfLeftArrowAreaX, middleOfLeftArrowAreaY);
    addRightArrowImage(middleOfRightArrowAreaX, middleOfRightArrowAreaY);

  }

  function drawLeftArrowArea(leftArrowAreaX, leftArrowAreaY, leftArrowAreaWidth, leftArrowAreaHeight) {

    leftArrowArea = game.add.graphics(0,0);
    leftArrowArea.beginFill(lightGray,1);
    leftArrowArea.drawRect(
      leftArrowAreaX,
      leftArrowAreaY,
      leftArrowAreaWidth,
      leftArrowAreaHeight);
    leftArrowArea.inputEnabled = true;
  }

  function drawRightArrowArea(rightArrowAreaX, rightArrowAreaY, rightArrowAreaWidth, rightArrowAreaHeight) {

    rightArrowArea = game.add.graphics(0,0);
    rightArrowArea.beginFill(lightGray,1);
    rightArrowArea.drawRect(
      rightArrowAreaX,
      rightArrowAreaY,
      rightArrowAreaWidth,
      rightArrowAreaHeight);
    rightArrowArea.inputEnabled = true;
  }

   function addLeftArrowImage(middleOfLeftArrowAreaX, middleOfLeftArrowAreaY){

    var leftArrowImage;
    leftArrowImage = game.add.sprite(middleOfLeftArrowAreaX - arrowSpriteWidth, 
                                    middleOfLeftArrowAreaY - arrowSpriteHeight, 'arrows');
    leftArrowImage.frame = 0;
    leftArrowImage.scale.setTo(0.5, 0.5);
  }

  function addRightArrowImage(middleOfRightArrowAreaX, middleOfRightArrowAreaY){

    var rightArrowImage;
    rightArrowImage = game.add.sprite(middleOfRightArrowAreaX - arrowSpriteWidth, 
                                      middleOfRightArrowAreaY - arrowSpriteHeight, 'arrows');
    rightArrowImage.frame = 1;
    rightArrowImage.scale.setTo(0.5, 0.5);
  }

  function createToolsButton(){

    var screenWidth = game.width;
    var screenHeight = game.height;
    var toolsButtonY = screenHeight / 90; 
    var toolsButtonX = screenWidth - screenWidth / 15;

    tools_button = game.add.sprite(toolsButtonX, toolsButtonY, 'tools_button');
    tools_button.scale.setTo(0.02, 0.025);
    tools_button.inputEnabled = true;
  }

  function createButtonEvents(game){

    tools_button.events.onInputDown.add(usernamePrompt, game);

    leftArrowArea.events.onInputOver.add(leftDown, game);
    leftArrowArea.events.onInputDown.add(leftDown, game);

    rightArrowArea.events.onInputOver.add(rightDown, game);
    rightArrowArea.events.onInputDown.add(rightDown, game);

    leftArrowArea.events.onInputOut.add(leftUp, game);
    leftArrowArea.events.onInputUp.add(leftUp, game);

    rightArrowArea.events.onInputOut.add(rightUp, game);
    rightArrowArea.events.onInputUp.add(rightUp, game);

    redButtons.events.onInputDown.add(throttleDown, game);
    redButtons.events.onInputOver.add(throttleDown, game);

    redButtons.events.onInputOut.add(throttleUp, game);
    redButtons.events.onInputUp.add(throttleUp, game);
  }

  function setFullscreen(){

    var iOS = /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream;

    if(!iOS){

      game.input.onDown.add(function(pointer) {
        if (!game.scale.isFullScreen) {
            game.scale.startFullScreen(false);
        }
      }, this);
    }
  }

  game = new Phaser.Game(800, 600, Phaser.AUTO, 'controller', {

    preload: function preload() {

      addMessageHandler(function(msg){
        if(msg == "identified"){

          buttonDown = function(buttonID) {
            sendToGame("buttonDown", buttonID);
          }

          buttonUp = function(buttonID){
            sendToGame("buttonUp", buttonID);
          }
        }
      });

      game.load.image('tools_button', 'assets/Tools_button.png');
      game.load.spritesheet('redButtons', 'assets/redButtons.png', 150, 149);
      game.load.spritesheet('arrows', 'assets/arrows-2x.png', 200, 250);
    },

    create: function create(){

      game.scale.fullScreenScaleMode = Phaser.ScaleManager.EXACT_FIT;
      game.scale.scaleMode = Phaser.ScaleManager.EXACT_FIT;
      game.scale.refresh();

      style = { font: ' 20pt Ariel', fill: 'black', align: 'left', wordWrap: true, wordWrapWidth: 450 };
     
      carModelText = game.add.text(game.world.centerX, game.world.centerY, carModel, style);

      createToolsButton();
      createThrottleButton();
      createArrowKeys();
      createButtonEvents(this);

      equipItem("lame");

      setBackgroundColor(offWHITE);

      setFullscreen();
    }
  });

}.call(this, Phaser));
