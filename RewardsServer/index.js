var express = require('express');
var bodyParser = require('body-parser');

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

var user = {
    totalEarned: 0,
    totalSpent: 0,
    currentPoints: 0,
    newRank: 10000,
    rank: 1
};

/*///////////////////////////
 *   End of dummy users/clubs
 *////////////////////////////

/*
 ************************
 * PUT ROUTE SECTION
 ************************
 */

app.put('/pointsInfo', function (req, res) {
    // If for some reason the JSON isn't parsed, return HTTP error 400
    if (!req.body) return res.sendStatus(400);
    user.totalEarned += req.body.pointsEarned;
    user.currentPoints += req.body.pointsEarned;
    user.totalSpent -+ req.body.totalSpent;
    if(req.body.rankUp === true){
    user.rank++;
    }
    if (user.totalEarned >= user.newRank){
        user.rank++;
        user.newRank += 10000;
    }

    var jsonResponse = {
        id: '123', status: 'updated'
    };
    console.log("\nPoints info in now:\ntotalEarned: " + user.totalEarned + "\ntotalSpent: "
    + user.totalSpent + "\ncurrentPoints: " + user.currentPoints + "\nrank: " + user.rank + "\n");
    res.json(jsonResponse);
});

/*
 ************************
 * GET ROUTE SECTION
 ************************
 */

app.get('/pointsInfo', function (req, res) {
    console.log("\nSending points info:\ntotalEarned: " + user.totalEarned + "\ntotalSpent: "
    + user.totalSpent + "\ncurrentPoints: " + user.currentPoints + "\nrank: " + user.rank + "\n");
    res.send(JSON.stringify(user));
});

app.listen(app.get("port"), function () {
    console.log('RewardsApp listening on port: ', app.get("port"));

});


