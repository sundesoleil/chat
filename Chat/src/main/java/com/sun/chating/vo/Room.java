package com.sun.chating.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Room {
	int roomNumber;
	String roomName;
	
	@Override
	public String toString() {
		return "Room [roomNumber=" + roomNumber + ", roomName=" + roomName + "]";
	}
}
