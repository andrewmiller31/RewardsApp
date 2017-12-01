var express = require('express');
var bodyParser = require('body-parser');
var GoogleAuth = require('google-auth-library');
var mongodb = require('./mongoDBFunctions.js');

// Initialize main instanced class
var app = express();

// Set the port
app.set("port", 5000);

// Support encoded bodies
app.use(bodyParser.urlencoded({
    extended: true
}));

// Support JSON-encoded bodies
app.use(bodyParser.json());
//loads the mongo functions in this file
//var mongodb = require('./mongoDBFunctions.js');
//console.log(mongodb);

var CLIENT_ID = '82658629626-fpsre8l5v7orb9ngqaogjnos9usvr1pc.apps.googleusercontent.com';

var user = {
    totalEarned: 0,
    totalSpent: 0,
    currentPoints: 0,
    newRank: 10000,
    rank: 1
};

var votes = {
    charity1: 0,
    charity2: 0,
    charity3: 0,
    charity4: 0,
    charity5: 0,
    winning: 0
}

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
    // If for some reason the JSON isn't parsed, return HTTP error 400
    if (!req.body) return res.sendStatus(400);

    mongodb.updateUser(req.body.id, req.body);

    var jsonResponse = {
                id: '123', status: 'updated'
            };
    res.json(jsonResponse);
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

    console.log("\nAdding vote for: Charity " + req.body.vote + "\nCurrent winner: " + winning
    + "\nCharity 1 votes: " + votes.charity1
    + "\nCharity 2 votes: " + votes.charity2
    + "\nCharity 3 votes: " + votes.charity3
    + "\nCharity 4 votes: " + votes.charity4
    + "\nCharity 5 votes: " + votes.charity5);
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

app.get('/pointsInfo', function (req, res) {
    console.log("\nSending points info:\ntotalEarned: " + user.totalEarned + "\ntotalSpent: "
    + user.totalSpent + "\ncurrentPoints: " + user.currentPoints + "\nrank: " + user.rank + "\n");
    res.send(JSON.stringify(user));
});

app.get('/charityVotes', function (req, res) {
    console.log("\nSending votes info.");
    res.send(JSON.stringify(votes));
});

app.listen(app.get("port"), function () {
    console.log('RewardsApp listening on port: ', app.get("port"));

});


