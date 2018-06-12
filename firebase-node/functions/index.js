let functions = require('firebase-functions');

let admin = require('firebase-admin');

let _ = require("underscore");

admin.initializeApp(functions.config().firebase);

exports.storeNotification = functions.database.ref('/chatrooms/{chatroomId}/chatroomMessages/{chatMesasgeId}')
.onWrite((snapshot, context) => {
	console.log("Store notification: starting");

	//get the message that was written
	let message = snapshot.after.val().message;
	let messageSenderId = snapshot.after.val().senderId;
	console.log("message: ", message);
	console.log("sender_id: ", messageSenderId);
	let timestamp = snapshot.after.val().timestamp;
	let messageSenderUsername = snapshot.after.val().senderUsername;
	return snapshot.after.ref.parent.parent.on('value', (dataSnapshot) => {
		let chatroomName = dataSnapshot.val().chatroomName;
		let offerImageUri = dataSnapshot.val().offerImageUri;
		console.log("Offer image: ", offerImageUri);
		//get the chatroom id
		let chatroomId = context.params.chatroomId;
		console.log("chatroom_id: ", chatroomId);
		users = dataSnapshot.val().users;
		console.log("Users", _.keys(users));
		_.forEach(_.keys(users), uid => {
			let notification = {
				"userId": uid,
				"chatroomId": chatroomId,
				"username": messageSenderUsername,
				"title": chatroomName,
				"description": message,
				"timestamp": timestamp,
				"iconUri": offerImageUri,
			};
			console.log("Storing notification: ", notification);
			return admin.database().ref('notifications/' + uid + '/' + chatroomId).set(notification);
		});
	});
});

exports.sendChatNotification = functions.database.ref('/chatrooms/{chatroomId}/chatroomMessages/{chatmessageId}')
.onWrite((snap, context) => {

	console.log("System: starting");
	console.log("snapshot: ", snap);
	console.log("snapshot.after: ", snap.after);
	console.log("snapshot.after.val(): ", snap.after.val());

	//get the message that was written
	let message = snap.after.val().message;
	let messageSenderId = snap.after.val().senderId;
	console.log("message: ", message);
	console.log("sender_id: ", messageSenderId);

	//get the chatroom id
	let chatroomId = context.params.chatroomId;
	console.log("chatroom_id: ", chatroomId);

	return snap.after.ref.parent.parent.once('value').then(snap => {
		let data = snap.child('users').val();
		console.log("data: ", data);

		//get the number of users in the chatroom
		let length = 0;

		_.each(data, ((value) => {
			length++;
		}));
		console.log("data length: ", length);

		//loop through each user currently in the chatroom
		let tokens = [];
		let i = 0;
		_.forEach(_.keys(data), ((user_id) => {
			console.log("user_id: ", user_id);

			//get the token and add it to the array
			let reference = admin.database().ref("/users/" + user_id);
			reference.once('value').then(snap => {
				//get the token
				let token = snap.child('messagingToken').val();
				let iconUri = snap.child('image').val();
				console.log('token: ', token);
				if(user_id !== messageSenderId) {
					tokens.push(token);
				}
				i++;

				//also check to see if the user_id we're viewing is the user who posted the message
				//if it is, then save that name so we can pre-pend it to the message
				let messageUserName = "";
				if(snap.child('user_id').val() === messageSenderId){
					messageUserName = snap.child('name').val();
					console.log("message user name: " , messageUserName);
					message = messageUserName + ": " + message;
				}

				//Once the last user in the list has been added we can continue
				if(i === length) {

					console.log("Constructing the notification message.");
					const payload = {
						data: {
							message_data_type: "type_chat_message",
							title: "Prodaj!",
							message: message,
							chatroomId: chatroomId,
							icon: iconUri
						}
					};
					console.log("Constructed message: ", payload);


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
				} else {
					throw new Error("Error adding users!");
				}
			})
			.catch((error) => {
				console.log(error);
			});
		}));
	return -1;
	});
});

exports.sendOfferDeletedNotification = functions.database.ref('/offers/{offerId}/isDeleted')
.onUpdate((snapshot, context) => {

	console.log("Offer deleted logic starting...");
	let deletedOfferId = context.params.offerId;
	let message = "This item has been sold / is no longer available."

	let isDeleted = snapshot.after.val();
	if(isDeleted === false) {
		return 0;
	}
	
	admin.database().ref("/chatrooms")
	.orderByChild("offerId").equalTo(deletedOfferId)
	.on("child_added", (chatroomSnapshot) => {

		let chatroomId = chatroomSnapshot.child('id').val();
		console.log("Chatroom found: ", chatroomId)
		let askerId = chatroomSnapshot.child('askerId').val();
		console.log("Asker id: ", askerId);
		let iconUri = chatroomSnapshot.child('offerImageUri').val();

		let users = chatroomSnapshot.child('users').val();

		let recieverId = "";
		_.forEach(users, (user => {
			if(user.id !== askerId) {
				recieverId = user.id;
			}
		}));
		
		return admin.database().ref("/users/" + recieverId).once('value').then(userSnapshot => {
			let queriedUser = userSnapshot.val();
			console.log("User snapshot: ", queriedUser);
			let senderUsername = userSnapshot.child('username').val();

			console.log("Username loaded: ", senderUsername);

			let ref = "/chatrooms/"+chatroomId+"/chatroomMessages";
			console.log("pushing to node: ", ref);
			return admin.database().ref(ref).push({
				message: message,
				senderId: "",
				senderUsername: "",
				timestamp: Date.now()
			});
		});
	});
});

exports.autoAssignId = functions.database.ref("{parentNode}/{topId}")
.onCreate((snap, context) => {
	console.log("ID auto assignment starting");
	let id = context.params.topId;
	let node = context.params.parentNode;
	console.log("ID being processed: ", id);
	let childNode = snap.val();
	console.log("Node being processed: ", childNode);
	if(!_.has(childNode, "id") && snap.child !== null && node !== 'notifications') {
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
	return -1;	
})