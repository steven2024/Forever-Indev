package net.minecraft.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import org.lwjgl.input.Keyboard;

public final class GameSettings {
	
	private static final String[] RENDER_DISTANCES = new String[]{ "Far", "Normal", "Short", "Tiny" };
	private static final String[] DIFFICULTIES = new String[]{ "Peaceful", "Easy", "Normal", "Hard" };
	
	private Minecraft mc;
	private File optionsFile;
	
	public int numberOfOptions = 9;
	public int difficulty = 2;
	public boolean thirdPersonView = false;
	public boolean music = true;
	public boolean sound = true;
	public boolean invertMouse = false;
	public boolean showFPS = false;
	public int renderDistance = 0;
	public boolean fancyGraphics = true;
	public boolean anaglyph = false;
	public boolean limitFramerate = false;
	
	public KeyBinding keyBindForward = new KeyBinding("Forward", 17);
	public KeyBinding keyBindLeft = new KeyBinding("Left", 30);
	public KeyBinding keyBindBack = new KeyBinding("Back", 31);
	public KeyBinding keyBindRight = new KeyBinding("Right", 32);
	public KeyBinding keyBindJump = new KeyBinding("Jump", 57);
	public KeyBinding keyBindInventory = new KeyBinding("Inventory", 18);
	public KeyBinding keyBindDrop = new KeyBinding("Drop", 16);
	public KeyBinding keyBindChat = new KeyBinding("Chat", 20);
	public KeyBinding keyBindToggleFog = new KeyBinding("Toggle fog", 33);
	public KeyBinding keyBindSave = new KeyBinding("Save location", 28);
	public KeyBinding keyBindLoad = new KeyBinding("Load location", 19);
	
	public KeyBinding[] keyBindings = new KeyBinding[]{this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindToggleFog, this.keyBindSave, this.keyBindLoad};

	public GameSettings(Minecraft mc, File fileRoot) {
		this.mc = mc;
		this.optionsFile = new File(fileRoot, "options.txt");
		this.loadOptions();
	}

	public final String setKeyBindingString(int keyBindID) {
		return this.keyBindings[keyBindID].keyDescription + ": " + Keyboard.getKeyName(this.keyBindings[keyBindID].keyCode);
	}

	public final void setKeyBinding(int keyBindID, int keyCode) {
		this.keyBindings[keyBindID].keyCode = keyCode;
		this.saveOptions();
	}

	public final void setOptionValue(int optionID, int value) {
		
		switch (optionID) {
		
			case 0:
				this.music = !this.music;
				this.mc.sndManager.onSoundOptionsChanged();
				break;
				
			case 1:
				this.sound = !this.sound;
				this.mc.sndManager.onSoundOptionsChanged();
				break;
				
			case 2:
				this.invertMouse = !this.invertMouse;
				break;
				
			case 3:
				this.showFPS = !this.showFPS;
				break;
				
			case 4:
				this.renderDistance = this.renderDistance + value & 3;
				break;
				
			case 5:
				this.fancyGraphics = !this.fancyGraphics;
				break;
				
			case 6:
				this.anaglyph = !this.anaglyph;
				this.mc.renderEngine.refreshTextures();
				break;
				
			case 7:
				this.limitFramerate = !this.limitFramerate;
				break;
				
			case 8:
				this.difficulty = this.difficulty + value & 3;
				break;
		}

		this.saveOptions();
	}

	public final String setOptionString(int optionID) {
		
		switch (optionID) {
			case 0: return "Music: " + (this.music ? "ON" : "OFF");
			case 1: return "Sound: " + (this.sound ? "ON" : "OFF");
			case 2: return "Invert mouse: " + (this.invertMouse ? "ON" : "OFF");
			case 3: return "Show debug: " + (this.showFPS ? "ON" : "OFF");
			case 4: return "Render distance: " + RENDER_DISTANCES[this.renderDistance];
			case 5: return "View bobbing: " + (this.fancyGraphics ? "ON" : "OFF");
			case 6: return "3d anaglyph: " + (this.anaglyph ? "ON" : "OFF");
			case 7: return "Limit framerate: " + (this.limitFramerate ? "ON" : "OFF");
			case 8: return "Difficulty: " + DIFFICULTIES[this.difficulty];
			default: return "";
		}
		
	}

	private void loadOptions() {
		try {
			if(this.optionsFile.exists()) {
				BufferedReader var1 = new BufferedReader(new FileReader(this.optionsFile));

				while(true) {
					String var2 = var1.readLine();
					if(var2 == null) {
						var1.close();
						return;
					}

					String[] var5 = var2.split(":");
					if(var5[0].equals("music")) {
						this.music = var5[1].equals("true");
					}

					if(var5[0].equals("sound")) {
						this.sound = var5[1].equals("true");
					}

					if(var5[0].equals("invertYMouse")) {
						this.invertMouse = var5[1].equals("true");
					}

					if(var5[0].equals("showFrameRate")) {
						this.showFPS = var5[1].equals("true");
					}

					if(var5[0].equals("viewDistance")) {
						this.renderDistance = Integer.parseInt(var5[1]);
					}

					if(var5[0].equals("bobView")) {
						this.fancyGraphics = var5[1].equals("true");
					}

					if(var5[0].equals("anaglyph3d")) {
						this.anaglyph = var5[1].equals("true");
					}

					if(var5[0].equals("limitFramerate")) {
						this.limitFramerate = var5[1].equals("true");
					}

					if(var5[0].equals("difficulty")) {
						this.difficulty = Integer.parseInt(var5[1]);
					}

					for(int var3 = 0; var3 < this.keyBindings.length; ++var3) {
						if(var5[0].equals("key_" + this.keyBindings[var3].keyDescription)) {
							this.keyBindings[var3].keyCode = Integer.parseInt(var5[1]);
						}
					}
				}
			}
		} catch (Exception var4) {
			System.out.println("Failed to load options");
			var4.printStackTrace();
		}
	}

	public final void saveOptions() {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(this.optionsFile));
			out.println("music:" + this.music);
			out.println("sound:" + this.sound);
			out.println("invertYMouse:" + this.invertMouse);
			out.println("showFrameRate:" + this.showFPS);
			out.println("viewDistance:" + this.renderDistance);
			out.println("bobView:" + this.fancyGraphics);
			out.println("anaglyph3d:" + this.anaglyph);
			out.println("limitFramerate:" + this.limitFramerate);
			out.println("difficulty:" + this.difficulty);

			for(int i = 0; i < this.keyBindings.length; i++) {
				out.println("key_" + this.keyBindings[i].keyDescription + ":" + this.keyBindings[i].keyCode);
			}

			out.close();
			
		} catch (Exception e) {
			System.out.println("Failed to save options");
			e.printStackTrace();
		}
	}
}
