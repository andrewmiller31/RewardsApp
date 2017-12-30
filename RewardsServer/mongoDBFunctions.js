//loads the MongoDB package
var mongojs = require("mongojs");

var url = 'mongodb://Admin:<password>@ds135747.mlab.com:35747/rewards';

var collections = ['users', 'slots', 'scratches'];

var assert = require('assert');

var mongoDBRef = mongojs(url, collections);
console.log("MongoDB is active.")

module.exports.addGame = function(type, gameData, callback){
    if(type === 'scratch'){
        mongoDBRef.collection('scratches').save(gameData, function(err, result){
            if(err || !result) console.log("Scratch game failed to save in database.");
            else {
                console.log("Scratch game inserted into the collection in MongoDB.");
                callback(result);
            }
        });
    } else if( type === 'slots'){
        mongoDBRef.collection('slots').save(gameData, function(err, result){
            if(err || !result) console.log("Slots game failed to save in database.");
            else {
                console.log("Slots game inserted into the collection in MongoDB.");
                callback(result);
            }
        });
    }
};

module.exports.findGame = function(gameID, type, callback){
    if(type === 'scratch'){
        mongoDBRef.scratches.find({id: gameID}).toArray(function(err, docs) {
            callback(docs);
        });
    } else if(type === "slots"){
        mongoDBRef.slots.find({id: gameID}).toArray(function(err, docs) {
               callback(docs);
        });
    }
};

module.exports.updateJackpot = function(gameID, callback){
    mongoDBRef.slots.find({id: gameID}).toArray(function(err, docs) {
        var newJackpot = docs[0].jackpot + docs[0].cost;
        var updateJackpot = {$set: {jackpot: newJackpot}};
        mongoDBRef.slots.findAndModify({query: {id: gameID}, update: updateJackpot, new: true},
            function (err, tank) { if (err) throw err; });
        callback(newJackpot);
    });
};

/**
* Finds a specific user. If the user hasn't initiated an account before, it is created.
* @param userID - string of user's oauth ID
* @param callback - the function that the result is sent to
*/

module.exports.findUser = function(userID, callback) {
    mongoDBRef.users.find({id: userID}).toArray(function(err, docs) {
	    if (docs.length === 0 && !err) {
	        console.log("User does not exist. Creating new profile...")
	        var userData = {
	            id: userID,
                totalEarned: 0,
                totalSpent: 0,
                currentPoints: 0,
                currentTokens: 0,
                newRank: 10000,
                rank: 1,
                role: 'User'
            };
	        mongoDBRef.collection('users').save(userData, function(err, result){
                if(err || !result) console.log("User failed to save in database.");
                else {
                    console.log("User inserted into users collection in MongoDB.");
                    callback(result, true);
                }
            });
	    }
	    else if(!err){
            callback(docs, false);
        }
    });
};

module.exports.updateUser = function(userID, newData, callback) {
    mongoDBRef.users.find({id: userID}).toArray(function(err, docs) {
    	    if(!err){
                var tEarned = docs[0].totalEarned + newData.pointsEarned;
                var cPoints = docs[0].currentPoints + newData.pointsEarned;
                var cTokens = docs[0].currentTokens + newData.tokensEarned;
                var tSpent = docs[0].totalSpent;
                var tokSpent = docs[0].tokensSpent;
                var nRnk = docs[0].newRank;
                var rnk = docs[0].rank;

                cPoints -= newData.pointsSpent;
                tSpent += newData.pointsSpent;
                cTokens -= newData.tokensSpent;

                if (tEarned >= nRnk){
                    rnk++;
                    nRnk += 10000;
                    }

                if(cPoints >= 0 && cTokens >= 0){
                    var newValues = { $set: { totalEarned: tEarned,
                                            totalSpent: tSpent,
                                            currentPoints: cPoints,
                                            currentTokens: cTokens,
                                            newRank: nRnk,
                                            rank: rnk, } };

                    mongoDBRef.users.findAndModify({
                	            query: {id: userID},
                	            update: newValues,
                	            new: true},
                	            function (err, tank) {
                                if (err) throw err;
                    });
                    callback(true);
                }
                else {
                    callback(false);
                }
            }
        });

};


module.exports.getCollection = function(collectionName, callback) {
    var cursor = mongoDBRef.collection(collectionName).find(function(err, docs) {
            if(err || !docs) {
    	    console.log("Error in retrieving collection\n");
    	}
            else {
    	    callback(docs);
    	}
        });
};