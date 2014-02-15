package com.boringpeople.werewolf.message;

public enum MessageType {
    None(""),
    DefaultResponse("defaultResponse"),
	SignIn("signIn"), CreateRoom("createRoom"), JoinRoom("joinRoom"), LeaveRoom("leaveRoom"), Ready("ready"), Disready(
			"disready"), GameStart("gameStart"), AssignRoles("assignRoles"), SelectPlayers("selectPlayers");

	private String _type;

	private MessageType(String type) {
		this._type = type;
	}

	@Override
	public String toString() {
		return this._type;
	}

	public static MessageType transform(String type) {
		switch (type) {
		case "signIn":
			return MessageType.SignIn;
		case "createRoom":
			return MessageType.CreateRoom;
		case "joinRoom":
			return MessageType.JoinRoom;
		case "leaveRoom":
			return MessageType.LeaveRoom;
		case "ready":
			return MessageType.Ready;
		case "disready":
			return MessageType.Disready;
		case "gameStart":
			return MessageType.GameStart;
		case "assignRoles":
			return MessageType.AssignRoles;
		case "selectPlayers":
			return MessageType.SelectPlayers;
        default:
            return  MessageType.None;
		}
	}

}
