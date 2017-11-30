//loads the MongoDB package
var mongojs = require("mongojs");

var url = 'mongodb://127.0.0.1:27017/rewardsDB';

var collections = ['users', 'slots', 'scratches', 'charities'];

var assert = require('assert');

var mongoDBRef = mongojs(url, collections);
console.log("MongoDB is active.")

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

module.exports.updateUser = function(userID, newData) {
    mongoDBRef.users.find({id: userID}).toArray(function(err, docs) {
    	    if(!err){
                var tEarned = docs[0].totalEarned + newData.pointsEarned;
                var cPoints = docs[0].currentPoints + newData.pointsEarned;
                var tSpent = docs[0].totalSpent;
                var nRnk = docs[0].newRank;
                var rnk = docs[0].rank;

                if((cPoints - newData.pointsSpent) >= 0){
                    cPoints -= newData.pointsSpent;
                    tSpent += newData.pointsSpent;
                    }

                if (tEarned >= nRnk){
                    rnk++;
                    nRnk += 10000;
                    }

                var newValues = { $set: { totalEarned: tEarned,
                                            totalSpent: tSpent,
                                            currentPoints: cPoints,
                                            newRank: nRnk,
                                            rank: rnk, } };

                mongoDBRef.users.findAndModify({
                	        query: {id: userID},
                	        update: newValues,
                	        new: true},
                	        function (err, tank) {
                              if (err) throw err;
                            });
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