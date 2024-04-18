'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
//const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');

let stompClient = null;
let domain = null;
let fullname = null;
let selectedUserId = null;
let Name = null;
let userRole = null;
let userid = null;
Name = document.querySelector('#name').value.trim();
domain = document.querySelector('#domain').value.trim();
userid = document.querySelector('#user').value.trim();
function connect() {
	
	

    if (Name) {
		//if(userRole === "ROLE_USER"){
	       // usernamePage.classList.add('hidden');
	       // chatPage.classList.remove('hidden');
	    //}

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
		//alert(stompClient);
        stompClient.connect({}, onConnected, onError);
    }
    //event.preventDefault();
}


function onConnected() {
    stompClient.subscribe(`/user/${Name}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/user/public`, onMessageReceived);
	
    /* register the connected user
    stompClient.send("/app/user.addUser",
        {},
        JSON.stringify({nickName: nickname, fullName: fullname, status: 'ONLINE'})
    );*/
    document.querySelector('#connected-user-fullname').textContent = Name;
    findAndDisplayConnectedUsers().then();
}

async function findAndDisplayConnectedUsers() {
	//const connectedUsersResponse = null;
	let connectedUsers = null;
    if(userRole === "ROLE_USER"){
	    const connectedUsersResponse = await fetch('/getconsultant');
	    connectedUsers = await connectedUsersResponse.json();
	    connectedUsers = connectedUsers.filter(user => user.domain === domain);
    }
    if(userRole === "ROLE_CONSULTANT"){
		const connectedUsersResponse = await fetch('/getuser');
	    connectedUsers = await connectedUsersResponse.json();
	    alert(userid);
	    connectedUsers = connectedUsers.filter(user => user.email === userid);
	    alert(connectedUsers);
	    
	}
    const connectedUsersList = document.getElementById('connectedUsers');
    connectedUsersList.innerHTML = '';
    connectedUsers.forEach(user => {
		alert(user.email);
        appendUserElement(user, connectedUsersList);
        if (connectedUsers.indexOf(user) < connectedUsers.length - 1) {
            const separator = document.createElement('li');
            separator.classList.add('separator');
            connectedUsersList.appendChild(separator);
        }
    });
}

function appendUserElement(user, connectedUsersList) {
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = user.email;

    const userImage = document.createElement('img');
    userImage.src = '../img/user_icon.png';
    userImage.alt = user.firstName;

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = user.firstName;

    const receivedMsgs = document.createElement('span');
    receivedMsgs.textContent = '0';
    receivedMsgs.classList.add('nbr-msg', 'hidden');

    listItem.appendChild(userImage);
    listItem.appendChild(usernameSpan);
    listItem.appendChild(receivedMsgs);

    listItem.addEventListener('click', userItemClick);
	
    connectedUsersList.appendChild(listItem);
}

function userItemClick(event) {
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    messageForm.classList.remove('hidden');

    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    selectedUserId = clickedUser.getAttribute('id');
    fetchAndDisplayUserChat().then();

    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');
    nbrMsg.textContent = '0';

}

function displayMessage(senderId, content) {
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');
    if (senderId === Name) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }
    const message = document.createElement('p');
    message.textContent = content;
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}

async function checkUserRole(){
	const userResponse = await fetch('/getuserrole');
	userRole = await userResponse.text();
	//usernamePage.classList.add('hidden');
    //chatPage.classList.remove('hidden');
    //document.getElementById('usernameForm').dispatchEvent(new Event('submit'));
    connect();
	
}

async function fetchAndDisplayUserChat() {
    const userChatResponse = await fetch(`/messages/${Name}/${selectedUserId}`);
    const userChat = await userChatResponse.json();
    chatArea.innerHTML = '';
    userChat.forEach(chat => {
        displayMessage(chat.senderId, chat.content);
    });
    chatArea.scrollTop = chatArea.scrollHeight;
}


function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        const chatMessage = {
            senderId: Name,
            recipientId: selectedUserId,
            content: messageInput.value.trim(),
            timestamp: new Date()
        };
        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        displayMessage(Name, messageInput.value.trim());
        messageInput.value = '';
    }
    chatArea.scrollTop = chatArea.scrollHeight;
    event.preventDefault();
}


async function onMessageReceived(payload) {
    //await findAndDisplayConnectedUsers();
    //alert('Message received', payload);
    fetchAndDisplayUserChat().then();
    const message = JSON.parse(payload.body);
    if (selectedUserId && selectedUserId === message.senderId) {
        displayMessage(message.senderId, message.content);
        chatArea.scrollTop = chatArea.scrollHeight;
    }

    if (selectedUserId) {
        document.querySelector(`${selectedUserId}`).classList.add('active');
    } else {
        messageForm.classList.add('hidden');
    }
	//alert("in function");
    const notifiedUser = document.querySelector(`#${message.senderId}`);
    alert(notifiedUser)
    if (notifiedUser && !notifiedUser.classList.contains('active')) {
		alert("in if block");
        const nbrMsg = notifiedUser.querySelector('.nbr-msg');
        nbrMsg.classList.remove('hidden');
        nbrMsg.textContent = '';
    }
    
}

function onLogout() {
    /*stompClient.send("/app/user.disconnectUser",
        {},
        JSON.stringify({nickName: nickname, fullName: fullname, status: 'OFFLINE'})
    );*/
    window.location.reload();
}

//usernameForm.addEventListener('submit', connect, true); // step 1
messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);
window.onbeforeunload = () => onLogout();
window.onload = checkUserRole;