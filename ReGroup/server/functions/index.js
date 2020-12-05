const functions = require('firebase-functions');
const admin = require('firebase-admin');        //access firestore
const { v4: uuidv4 } = require('uuid');
const Promise = require("promise");
const { Message } = require('firebase-functions/lib/providers/pubsub');
const { reject, resolve } = require('promise');
const { database } = require('firebase-admin');
const { user } = require('firebase-functions/lib/providers/auth');
admin.initializeApp(); 



//chat group creation
/**
 * TODO - Add authentication to requet
 * Parameters - 
 * userID - user id to add to a group or create a new group with
 */
exports.joinGroup = functions.https.onRequest(async (request, response) => {


  functions.logger.info("User requested new group" + JSON.stringify(request.body));
  var database = admin.database();
  //check if a group already exists first before creating a new one
  var groups = database.ref('groups/');
  var groupResponse = await groups.orderByChild('size').startAt(1).endAt(3).limitToFirst(1).once('value').then((value) => {
    if(value.val() === null){
      reject("no results");
    }
    return value.val();
  }, (reason) => {
    functions.logger.error(reason);
    //else create a new group
    var groupName = uuidv4();          //group has unique identifier using uuid
    var groupData = {
      name: groupName,
      size: 0,
      members: [
      ]
    };
    return groupData
  }).catch(error => {
        functions.logger.error(error);
        //else create a new group
        var groupName = uuidv4();          //group has unique identifier using uuid
        var groupData = {
          name: groupName,
          size: 0,
          members: [
          ]
        };
        return groupData
  });
  
  if(groupResponse === null){
    var groupName = uuidv4();          //group has unique identifier using uuid
    group = {
      name: groupName,
      size: 0,
      members: [
      ]
    };
  } else {
    group = Object.values(groupResponse)[0];
  }


  functions.logger.info("groupdata" + JSON.stringify(group));
  group.members.push(request.body.data.userID);
  group.size++;
  //const res = database.ref('groups/' + groupName).set(groupData);

  const res = database.ref('groups/' + group.name).set(group);
  functions.logger.info("Returning group: " + JSON.stringify(group));
  response.send({data: group});

  return

});

/**
 * @argument groupID, 
 * @argument userID
 */
exports.leaveGroup = functions.https.onRequest((request, response) => {
  const groupID = request.body.data.groupID;
  const userID = request.body.data.userID;
  functions.logger.info("User requeted to leave group" + groupID);

  var groupRef = admin.database().ref('groups/' + groupID);
  return groupRef.once('value', (snapshot) => {
  
    
    var data = snapshot.val();
    if(data === null){
      response.send("done");
      return null;
    }
    var newMembers = []
    data.members.forEach(member => {
      if(member !== userID){
        newMembers.push(member);
      }
    });
    if(newMembers.length > 0){
      data.members = newMembers;
      data.size = newMembers.length;
      response.send("done");
      return groupRef.set(data)
    } else{
      response.send("done");
      return groupRef.remove()
    }
  },(error) =>{
    functions.logger(error);
  }).catch((error)=>{
    response.send("done");
    return;
  })
});

// /**
//  * TODO: if I get to individual chats, this will make that possible
//  */
// exports.joinIndividualChat = functions.https.onRequest((request, response) => {
//   functions.logger.info("User requested new group");

// });




//database functions TODO Make this use user groups for notifications rather then topics
exports.groupChatNewUserAdded = functions.database.ref('/groups/{groupName}').onWrite((change, context) =>{
  const groupName = context.params.groupName;
  if(change.after.exists()){
    group = change.after;
    functions.logger.info("group:" + JSON.stringify(group));
    var topic = groupName;
    var message = {
      data: {
        group: JSON.stringify(group)
      },
      topic: topic
    };

    admin.messaging().send(message).then((response) => {
      functions.logger.info("updated chat group" + group);
      return response;
    }).catch((error)=>{
      functions.logger.error("Could not send message:" + error);
    });
    
  }
});