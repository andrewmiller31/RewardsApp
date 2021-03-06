var express = require('express');
var bodyParser = require('body-parser');
var GoogleAuth = require('google-auth-library');
var mongodb = require('./mongoDBFunctions.js');

// Initialize main instanced class
var app = express();

// Set the port
app.set("port", 5000);

// Support encoded bodies
app.use(bodyParser.json({limit: '50mb'}));
app.use(bodyParser.urlencoded({
    limit: '50mb',
    extended: true
}));

// Support JSON-encoded bodies
//app.use(bodyParser.json());

//loads the mongo functions in this file
//var mongodb = require('./mongoDBFunctions.js');
//console.log(mongodb);

var CLIENT_ID = '82658629626-fpsre8l5v7orb9ngqaogjnos9usvr1pc.apps.googleusercontent.com';

var votes = {
    charity1: 0,
    charity2: 0,
    charity3: 0,
    charity4: 0,
    charity5: 0,
    winning: 0
}

var slotsIDs = {
    idArray: []
};

var scratchIDs = {
    idArray: []
};

var slotsInitialized = false;
var scratchInitialized = false;

 app.get('/scratch/:mongoID', function (req,res) {
    mongodb.findGame(req.params.mongoID, 'scratch', function(result){
        res.body = JSON.stringify(result[0]);
        res.send(res.body);
    });
 });

 app.get('/slots/:mongoID', function (req,res) {
    mongodb.findGame(req.params.mongoID, 'slots', function(result){
        res.body = JSON.stringify(result[0]);
        res.send(res.body);
    });
 });

 app.get('/scratchGameIDs', function(req, res) {
    if(!scratchInitialized){
        console.log('Fetching scratch IDs');
        mongodb.getCollection('scratches', function(result){
            result.forEach(function(game){
                scratchIDs.idArray.push(game.id);
            });
            console.log('Finished fetching scratch IDs');
            scratchInitialized = true;
            res.json(scratchIDs);
        });
    } else res.json(scratchIDs);
 });


 app.get('/slotsGameIDs', function(req, res) {
     if(!slotsInitialized){
         console.log('Fetching slots IDs');
             mongodb.getCollection('slots', function(result){
             result.forEach(function(game){
                 slotsIDs.idArray.push(game.id);
             });
             console.log('Finished fetching slots IDs');
             slotsInitialized = true;
             res.json(slotsIDs);
             });
     } else res.json(slotsIDs);
 });

 app.get('/scratch/card/:mongoID', function(req, res){
    mongodb.findGame(req.params.mongoID, 'scratch', function(result){
        var game = result[0];
        var cardInfo = {
            type: 'scratch',
            title: game.title,
            winMessage: game.winMessage,
            background: game.background,
            cost: 0,
            id: req.params.mongoID
        }
        res.body = JSON.stringify(cardInfo);
        res.send(res.body);
    });
 });

 app.get('/slots/card/:mongoID', function(req, res){
    mongodb.findGame(req.params.mongoID, 'slots', function(result){
        var game = result[0];
        var cardInfo = {
            type: 'slots',
            title: game.title,
            winMessage: game.winMessage,
            background: game.background,
            cost: game.cost,
            id: req.params.mongoID
        }
        res.body = JSON.stringify(cardInfo);
        res.send(res.body);
    });
 });

 app.put('/scratch', function(req, res){
     if (!req.body) return res.sendStatus(400);
     mongodb.addGame('scratch', req.body, function(gameData){
     });
     scratchIDs.idArray.push(req.body.id);
     var updatedResponse = {
             id: '123', status: 'updated'
         };
     res.json(updatedResponse);
 });

app.put('/slots', function(req, res){
    if (!req.body) return res.sendStatus(400);
    mongodb.addGame('slots', req.body, function(gameData){
    });
    slotsIDs.idArray.push(req.body.id);
    var updatedResponse = {
            id: '123', status: 'updated'
        };
    res.json(updatedResponse);
 });

 app.get('/slotsJackpot/:mongoID', function(req, res){
     if (!req.body) return res.sendStatus(400);
     mongodb.updateJackpot(req.params.mongoID, function(newJackpot){
        var updatedJackpot = {
            jackpot: newJackpot
        };
        res.json(updatedJackpot);
     });
  });
/*
 ************************
 * POST ROUTE SECTION
 ************************
 */

 app.post('/verifySignIn', function(req, res) {
    if (!req.body) return res.sendStatus(400);
    var auth = new GoogleAuth;
    var client = new auth.OAuth2(CLIENT_ID, '', '');
    client.verifyIdToken(
        req.body.token,
        CLIENT_ID,
        function(e, login) {
          var payload = login.getPayload();
          var userid = payload['sub'];
          mongodb.findUser(userid, function(result, newUser){
                if(newUser === false){
                   var user = result[0];
                   res.body = JSON.stringify(user);
                   res.send(res.body);
                   }
                else{
                    var user = result;
                    res.body = JSON.stringify(user);
                    res.send(res.body);
                }
               });

        });
 });


/*
 ************************
 * PUT ROUTE SECTION
 ************************
 */

app.put('/pointsInfo', function (req, res) {
    if (!req.body) return res.sendStatus(400);

    var negResponse = {
        id: '444', status: 'negative'
    };
    var updatedResponse = {
        id: '123', status: 'updated'
    };

    mongodb.updateUser(req.body.id, req.body, function(updated){
        if(!updated){
            res.json(negResponse);
        } else{
            res.json(updatedResponse);
        }
    });
});


app.put('/charityVotes', function (req, res) {
    // If for some reason the JSON isn't parsed, return HTTP error 400
    if (!req.body) return res.sendStatus(400);

    if(req.body.vote === "Charity 1"){
        votes.charity1++;
        if(votes.charity1 > votes.charity2 && votes.charity1 > votes.charity3 && votes.charity1 > votes.charity4 && votes.charity1 > votes.charity5){
            votes.winning = 1;
        }
    }
    else if(req.body.vote === "Charity 2"){
        votes.charity2++;
        if(votes.charity2 > votes.charity1 && votes.charity2 > votes.charity3 && votes.charity2 > votes.charity4 && votes.charity2 > votes.charity5){
            votes.winning = 2;
        }
    }
    else if(req.body.vote === "Charity 3"){
        votes.charity3++;
        if(votes.charity3 > votes.charity1 && votes.charity3 > votes.charity2 && votes.charity3 > votes.charity4 && votes.charity3 > votes.charity5){
            votes.winning = 3;
        }
    }
    else if(req.body.vote === "Charity 4"){
        votes.charity4++;
        if(votes.charity4 > votes.charity1 && votes.charity4 > votes.charity2 && votes.charity4 > votes.charity3 && votes.charity4 > votes.charity5){
            votes.winning = 4;
        }
    }
    else if(req.body.vote === "Charity 5"){
        votes.charity5++;
        if(votes.charity5 > votes.charity1 && votes.charity5 > votes.charity2 && votes.charity5 > votes.charity3 && votes.charity5 > votes.charity4){
            votes.winning = 5;
        }
    }

    var winning = "Charity " + votes.winning;

    var jsonResponse = {
        id: '333', status: 'voteSuccess', winner: winning
    };

    res.json(jsonResponse);
});

/*
 ************************
 * GET ROUTE SECTION
 ************************
 */

app.get('/users/:userID', function (req,res) {
     mongodb.findUser(req.params.userID, function(result){
         var user = result[0];
         res.body = JSON.stringify(user);
         res.send(res.body);
     });
});

app.get('/charityVotes', function (req, res) {
    res.send(JSON.stringify(votes));
});

app.listen(app.get("port"), function () {
    console.log('RewardsApp listening on port: ', app.get("port"));
});


