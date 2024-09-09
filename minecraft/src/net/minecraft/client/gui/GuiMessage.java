package net.minecraft.client.gui;

import net.minecraft.client.commands.Command;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Clipboard;
import java.util.ArrayList;
import java.util.List;

public class GuiMessage extends GuiScreen {

    private static final String ALLOWED_CHARS = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()-=_+,.<>;':\"/?[{]}\\|`~";
    
    private String message; // The current message being typed
    private boolean sendMessage = false;
    private long lastBlinkTime = 0;
    private boolean showCursor = true; // Controls blinking cursor visibility
    private static final long BLINK_INTERVAL = 250L; // Faster blinking interval
    private int cursorPosition; // Tracks the position of the cursor in the string
    private boolean isSelectingAll = false; // Tracks if all text is selected

    private static List<String> messageHistory = new ArrayList<>();
    private int historyIndex = -1; // Tracks the current position in the message history
    private String originalMessage; // Tracks the original message before navigating history
    private boolean hasNavigatedUp = false; // Flag to track if user has navigated up

    private List<String> suggestions = new ArrayList<>(); // List of autocomplete suggestions
    private int suggestionIndex = -1; // Index of the currently selected suggestion

    public GuiMessage(boolean isCommand) {
        message = isCommand ? "/" : "";
        cursorPosition = message.length(); // Initially, the cursor is at the end of the message
        originalMessage = message; // Initialize original message
    }

    @Override
    public final void drawScreen(int mouseX, int mouseY) {
        long currentTime = System.currentTimeMillis();

        // Blinking cursor logic
        if (currentTime - lastBlinkTime > BLINK_INTERVAL) {
            showCursor = !showCursor;
            lastBlinkTime = currentTime;
        }

        // Determine the position where the text starts
        int textX = this.width / 2 - this.fontRenderer.getStringWidth(message) / 2;
        int textY = this.height - 80;

        // Draw the input box background
        int inputBoxTop = this.height - 82; // Adjust this to be right above the chat bar
        int inputBoxBottom = this.height - 70; // Adjust this to be right above the chat bar

        // Ensure background height is sufficient to cover the text and padding
        int textHeight = 10; // Estimate height of the text
        int padding = 4; // Additional padding
        inputBoxBottom = inputBoxTop + textHeight + padding; // Adjust height based on text height and padding

        drawRect(0, inputBoxTop, this.width, inputBoxBottom, -1442840576);

        // Adjusted highlight color (slightly lighter blue)
        int highlightColor = 0xFF0066FF;

        // If selecting all, draw a blue rectangle behind the text
        if (isSelectingAll) {
            drawRect(textX - 2, textY - 2, textX + this.fontRenderer.getStringWidth(message) + 2, textY + 10, highlightColor);
        }

        // Draw the message text
        drawString(this.fontRenderer, message, textX, textY, isSelectingAll ? 0xFFFFFFFF : 16777215);

        // Draw custom caret or underscore
        if (!isSelectingAll) {
            int cursorX = textX + this.fontRenderer.getStringWidth(message.substring(0, cursorPosition));

            // Show blinking underscore if at the end, custom thin caret otherwise
            if (cursorPosition == message.length() && showCursor) {
                drawString(this.fontRenderer, "_", cursorX, textY, 16777215);
            } else if (cursorPosition < message.length() && showCursor) {
                // Custom thin caret as a vertical line (1 pixel wide)
                drawRect(cursorX, textY - 2, cursorX + 1, textY + 10, 0xFFFFFFFF);
            }
        } else {
            // When selecting all, show blinking underscore at the end
            if (showCursor) {
                int cursorX = textX + this.fontRenderer.getStringWidth(message);
                drawString(this.fontRenderer, "_", cursorX, textY, 16777215);
            }
        }

        // Handle mouse clicks to reposition the cursor
        if (Mouse.isButtonDown(0)) {
            int clickX = mouseX - (this.width / 2 - this.fontRenderer.getStringWidth(message) / 2);
            cursorPosition = getCursorIndexFromClick(clickX);
            isSelectingAll = false; // Clear selection when clicking
        }
        
        super.drawScreen(mouseX, mouseY);
    }


    @Override
    protected void keyTyped(char character, int keycode) {
        // Handle sending the message (Enter key)
        if (keycode == Keyboard.KEY_RETURN) {
            if (!message.trim().isEmpty()) {
                sendMessage = true;
                if (message.charAt(0) != '/') {
                    messageHistory.add(message);
                    historyIndex = messageHistory.size(); // Set index to the latest message
                }
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
            }
            return;
        }

        // Handle closing the GUI (Escape key)
        if (keycode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(null); // Close the GUI without sending the message
            return;
        }

        // Handle Ctrl+A (Select All)
        if (isCtrlPressed() && keycode == Keyboard.KEY_A) {
            cursorPosition = message.length(); // Move cursor to the end of the message
            isSelectingAll = true; // Set selection mode to true
            return;
        }

        // Handle backspace (delete character to the left of the cursor)
        if (keycode == Keyboard.KEY_BACK) {
            if (isSelectingAll) {
                message = ""; // Clear all text if all is selected
                cursorPosition = 0; // Move cursor to start
                isSelectingAll = false;
            } else if (cursorPosition > 0 && message.length() > 0) {
                message = message.substring(0, cursorPosition - 1) + message.substring(cursorPosition);
                cursorPosition--; // Move the cursor one position to the left
            }
            return;
        }

        // Handle left arrow key (move cursor left)
        if (keycode == Keyboard.KEY_LEFT) {
            if (cursorPosition > 0) {
                cursorPosition--;
            }
            isSelectingAll = false; // Clear selection when moving the cursor
            return;
        }

        // Handle right arrow key (move cursor right)
        if (keycode == Keyboard.KEY_RIGHT) {
            if (cursorPosition < message.length()) {
                cursorPosition++;
            }
            isSelectingAll = false; // Clear selection when moving the cursor
            return;
        }

        // Handle up arrow key (navigate message history up)
        if (keycode == Keyboard.KEY_UP) {
            if (historyIndex == -1) {
                originalMessage = message;
                if (!messageHistory.isEmpty()) {
                    historyIndex = messageHistory.size() - 1;
                    message = messageHistory.get(historyIndex);
                    cursorPosition = message.length(); // Move cursor to the end of the retrieved message
                    hasNavigatedUp = true; // Mark that the user has navigated up
                }
            } else if (historyIndex > 0) {
                historyIndex--;
                message = messageHistory.get(historyIndex);
                cursorPosition = message.length(); // Move cursor to the end of the retrieved message
                hasNavigatedUp = true; // Mark that the user has navigated up
            }
            isSelectingAll = false; // Clear selection when navigating history
            return;
        }

        // Handle down arrow key (navigate message history down)
        if (keycode == Keyboard.KEY_DOWN) {
            if (hasNavigatedUp) {
                if (historyIndex < messageHistory.size() - 1) {
                    historyIndex++;
                    message = messageHistory.get(historyIndex);
                    cursorPosition = message.length(); // Move cursor to the end of the retrieved message
                } else {
                    message = originalMessage;
                    cursorPosition = message.length(); // Move cursor to the end of the original message
                    historyIndex = -1; // Reset index
                    hasNavigatedUp = false; // Reset navigation state
                }
            }
            isSelectingAll = false; // Clear selection when navigating history
            return;
        }

        // Handle character input
        if (ALLOWED_CHARS.indexOf(character) != -1) {
            if (isSelectingAll) {
                message = String.valueOf(character); // Replace all text if all is selected
                cursorPosition = message.length(); // Move cursor to the end
                isSelectingAll = false;
            } else {
                message = message.substring(0, cursorPosition) + character + message.substring(cursorPosition);
                cursorPosition++; // Move cursor to the right after adding the character
            }
            return;
        }

        // Handle clipboard paste (Ctrl+V)
        if (isCtrlPressed() && keycode == Keyboard.KEY_V) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            try {
                String pasteText = (String) clipboard.getData(DataFlavor.stringFlavor);
                if (pasteText != null) {
                    if (isSelectingAll) {
                        message = pasteText; // Replace all text if all is selected
                        cursorPosition = message.length(); // Move cursor to the end
                        isSelectingAll = false;
                    } else {
                        message = message.substring(0, cursorPosition) + pasteText + message.substring(cursorPosition);
                        cursorPosition += pasteText.length(); // Move cursor to the right after pasting
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // Handle clipboard copy (Ctrl+C)
        if (isCtrlPressed() && keycode == Keyboard.KEY_C) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection stringSelection = new StringSelection(message);
            clipboard.setContents(stringSelection, null);
            return;
        }

        // Handle suggestion navigation
        if (keycode == Keyboard.KEY_UP || keycode == Keyboard.KEY_DOWN) {
            if (suggestions.isEmpty()) return;
            
            if (keycode == Keyboard.KEY_UP) {
                if (suggestionIndex > 0) {
                    suggestionIndex--;
                }
            } else if (keycode == Keyboard.KEY_DOWN) {
                if (suggestionIndex < suggestions.size() - 1) {
                    suggestionIndex++;
                }
            }

            if (suggestionIndex >= 0 && suggestionIndex < suggestions.size()) {
                message = suggestions.get(suggestionIndex);
                cursorPosition = message.length(); // Move cursor to the end of the retrieved suggestion
            }
            return;
        }
    }
        
    // Check if Control key is pressed
    private boolean isCtrlPressed() {
        return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
    }

    // Calculate cursor index from the mouse click position
    private int getCursorIndexFromClick(int clickX) {
        int cumulativeWidth = 0;
        for (int i = 0; i < message.length(); i++) {
            cumulativeWidth += this.fontRenderer.getStringWidth(String.valueOf(message.charAt(i)));
            if (clickX < cumulativeWidth) {
                return i;
            }
        }
        return message.length(); // Clicked beyond the text, so place the cursor at the end
    }

    @Override
    public void onGuiClosed() {
        if (!sendMessage || message == null || message.trim().isEmpty())
            return;

        if (message.charAt(0) == '/') {
            String[] parts = message.split(" ");
            for (Command command : Command.COMMANDS) {
                if (command.getName().equals(parts[0])) {
                    command.runCommand(mc, parts);
                    return;
                }
            }
            this.mc.ingameGUI.addChatMessage("Unknown command. Use /help for help.");
        } else {
            this.mc.ingameGUI.addChatMessage(message);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
