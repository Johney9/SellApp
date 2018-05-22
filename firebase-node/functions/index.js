let functions = require('firebase-functions');

let admin = require('firebase-admin');

let _ = require("underscore");

admin.initializeApp(functions.config().firebase);

exports.sendChatNotification = functions.database.ref('/chatrooms/{chatroomId}/chatroom_messages/{chatmessageId}')
.onWrite((snap, context) => {

	console.log("System: starting");
	console.log("snapshot: ", snap);
	console.log("snapshot.after: ", snap.after);
	console.log("snapshot.after.val(): ", snap.after.val());

	//get the message that was written
	let message = snap.after.val().message;
	let messageUserId = snap.after.val().user_id;
	console.log("message: ", message);
	console.log("user_id: ", messageUserId);

	//get the chatroom id
	let chatroomId = context.params.chatroomId;
	console.log("chatroom_id: ", chatroomId);

	return snap.after.ref.parent.parent.once('value').then(snap => {
		let data = snap.child('users').val();
		console.log("data: ", data);

		//get the number of users in the chatroom
		let length = 0;
		data.forEach((value) => {
			length++;
		});
		console.log("data length: ", length);

		//loop through each user currently in the chatroom
		let tokens = [];
		let i = 0;
		data.forEach((user_id) => {
			console.log("user_id: ", user_id);

			//get the token and add it to the array
			let reference = admin.database().ref("/users/" + user_id);
			reference.once('value').then(snap => {
				//get the token
				let token = snap.child('messaging_token').val();
				console.log('token: ', token);
				tokens.push(token);
				i++;

				//also check to see if the user_id we're viewing is the user who posted the message
				//if it is, then save that name so we can pre-pend it to the message
				let messageUserName = "";
				if(snap.child('user_id').val() === messageUserId){
					messageUserName = snap.child('name').val();
					console.log("message user name: " , messageUserName);
					message = messageUserName + ": " + message;
				}

				//Once the last user in the list has been added we can continue
				if(i === length) {

					console.log("Construction the notification message.");
					const payload = {
						data: {
							data_type: "type_chat_message",
							title: "Prodaj!",
							message: message,
							chatroom_id: chatroomId
						}
					};


					return admin.messaging().sendToDevice(tokens, payload)
						.then((response) => {
							// See the MessagingDevicesResponse reference documentation for
							// the contents of response.
							console.log("Successfully sent message:", response);
							return response;
						  })
						  .catch((error) => {
							console.log("Error sending message:", error);
						  });
				}
				throw new Error("Error getting tokens!");
			})
			.catch((error) => {
				console.log(error);
			});
		});
	throw new Error("Fail!");
	});
});

exports.autoAssignId = functions.database.ref("{parentNode}/{topId}")
.onCreate((snap, context) => {
	console.log("ID auto assignment starting");
	let id = context.params.topId;
	console.log("ID being processed: ", id);
	let childNode = snap.val();
	console.log("Node being processed: ", childNode);
	if(!_.has(childNode, "id") && snap.child !== null) {
		childNode.id = id;
		return snap.ref.set(childNode)
			.then(() => {
				console.log("ID auto assignment successful.");
				return 0;
			})
			.catch((error) => {
				console.log("ID auto assignment reject with error: ", error);
			});
	}	
})