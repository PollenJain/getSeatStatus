const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp()


// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

/*name of the function is hello*/
exports.hello = functions.https.onRequest((req, res)=>{
    /*req contains the query parameters that we receive through the URL*/

    console.log(req.query.name)
	console.log(req.query.stop)

    /*res is for the response that we send back to the client*/
    res.send("response: Hello Sambha");

})


exports.DriverStartedJourney = functions.https.onRequest((req, res)=>{

    const stopName  = req.query.stopName;
    const topicName = "/topics/"+stopName;
    console.log("topic Name:" + topicName);
    const dateAndTime = req.query.startTime;
    console.log("dateAndTime: " + dateAndTime);
    const startTime = dateAndTime.split(" ")[3];
	const payload = {
		notification:
		{
			title : stopName,
			body  : "Started moving at " + startTime
		}
	};
	
	
	const options = {
		priority : "high",
		timeToLive : 60*60*24
	}
    
    res.send("topicName: " + topicName);
    return admin.messaging().sendToTopic(topicName, payload, options);
    
	
})