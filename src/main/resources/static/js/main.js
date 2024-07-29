'use strict';

const chatPage = document.querySelector('#chat-page');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');

let stompClient = null;
let name = null;
let selectedUserId = null;

function parseToken(token) {
    try {
        // Tách phần payload từ token
        const base64Url = token.split('.')[1];
        // Giải mã Base64Url thành Base64
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        // Giải mã Base64 thành chuỗi JSON
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error('Lỗi giải mã token:', e);
        return null;
    }
}


function connect() {
    const token = localStorage.getItem('access_token');
    const tokenPayload = parseToken(token);
    name = tokenPayload['sub'];
    chatPage.classList.remove('hidden');

    if (name) {
        let socket = new SockJS('/ws?authentication='+encodeURIComponent(token));
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
    }
}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe(`/user/${name}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/user/public`, onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/user.addUser",
        {},
        JSON.stringify({userName: name, status: 'ONLINE'})
    );

    document.querySelector('#connected-user-fullname').textContent = name;
    findAndDisplayConnectedUsers().then();
}

async function findAndDisplayConnectedUsers() {
    const connectedUsersResponse = await fetch('/user', {
        headers: {Authorization: 'Bearer '+ localStorage.getItem('access_token')}
    });
    let connectedUsers = await connectedUsersResponse.json();
    connectedUsers = connectedUsers.filter(user => user.userName !== name);
    const connectedUsersList = document.getElementById('connectedUsers');
    connectedUsersList.innerHTML = '';

    connectedUsers.forEach((user, index) => {
        appendUserElement(user, connectedUsersList);
        if (index < connectedUsers.length - 1) {
            const separator = document.createElement('li');
            separator.classList.add('separator');
            connectedUsersList.appendChild(separator);
        }
    });
}

function appendUserElement(user, connectedUsersList) {
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = user.userName;

    const userImage = document.createElement('img');
    userImage.src = '/static/image/user_icon.png';
    userImage.alt = user.userName;

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = user.userName;

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
        item.classList.remove('active')
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
    messageContainer.classList.add('message')
    if (senderId === name) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }
    const message = document.createElement('p');
    message.textContent = content;
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}

async function fetchAndDisplayUserChat() {
    const userChatResponse = await fetch(`/messages/${name}/${selectedUserId}`, {
        headers: {Authorization: 'Bearer '+ localStorage.getItem('access_token')}
    });
    const userChat = await userChatResponse.json();
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
            senderId: name,
            recipientId: selectedUserId,
            content: messageContent,
            timestamp: new Date()
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        displayMessage(name, messageContent);
        messageInput.value = '';
    }
    chatArea.scrollTop = chatArea.scrollHeight;
    event.preventDefault();
}


async function onMessageReceived(payload) {
    await findAndDisplayConnectedUsers();
    // console.log('Message received', payload['body']);
    const message = JSON.parse(payload['body']);
    if (selectedUserId && selectedUserId === message.senderId) {
        displayMessage(message.senderId, message.content);
        chatArea.scrollTop = chatArea.scrollHeight;
    }

    if (selectedUserId) {
        document.querySelector(`#${selectedUserId}`).classList.add('active');
    } else {
        messageForm.classList.add('hidden');
    }

    const notifiedUser = document.querySelector(`#${message.senderId}`);
    if (notifiedUser && !notifiedUser.classList.contains('active')) {
        const nbrMsg = notifiedUser.querySelector('.nbr-msg');
        nbrMsg.classList.remove('hidden');
        nbrMsg.textContent = '';
    }
}

function onLogout() {
    stompClient.send("/app/user.disconnectUser", {}, JSON.stringify({userName: name, status: 'OFFLINE'}));
    //window.location.href='/index';
}

window.onload = function () {
    connect();
};


messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);
window.onbeforeunload = () => onLogout();



