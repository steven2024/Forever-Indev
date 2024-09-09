package net.minecraft.client;

import java.util.ArrayList;
import java.util.List;

public final class ChatLine {
	
	public static List<ChatLine> chatLines = new ArrayList<>();
	
	public int updateCounter;
	public String message;
	
	public ChatLine(String message) {
		this.message = message;
		// Add this instance to the static list
		chatLines.add(this);
	}
	
	// Optionally, you might want to add a method to retrieve all messages
	public static List<String> getAllMessages() {
		List<String> messages = new ArrayList<>();
		for (ChatLine chatLine : chatLines) {
			messages.add(chatLine.message);
		}
		return messages;
	}
}
