package net.minecraft.client.gui;

public final class GuiNewLevel extends GuiScreen {
	
	private GuiScreen prevGui;
	private String[] worldType = new String[]{"Inland", "Island", "Floating", "Flat"};
	private String[] worldShape = new String[]{"Square", "Long", "Deep"};
	private String[] worldSize = new String[]{"Small", "Normal", "Huge"};
	private String[] worldTheme = new String[]{"Normal", "Hell", "Paradise", "Woods"};
	private int selectedWorldType = 1;
	private int selectedWorldShape = 0;
	private int selectedWorldSize = 1;
	private int selectedWorldTheme = 0;

	public GuiNewLevel(GuiScreen prevGui) {
		this.prevGui = prevGui;
	}

	public final void initGui() {
		this.controlList.clear();
		this.controlList.add(new GuiButtonText(2, this.width / 2 - 100, this.height / 4, 98, 20, "Size: "));
		this.controlList.add(new GuiButtonText(1, this.width / 2 + 2, this.height / 4, 98, 20, "Shape:"));
		this.controlList.add(new GuiButtonText(0, this.width / 2 - 100, this.height / 4 + 48, "Type: "));
		this.controlList.add(new GuiButtonText(3, this.width / 2 - 100, this.height / 4 + 72, "Theme: "));
		this.controlList.add(new GuiButtonText(4, this.width / 2 - 100, this.height / 4 + 96 + 12, "Create"));
		this.controlList.add(new GuiButtonText(5, this.width / 2 - 100, this.height / 4 + 120 + 12, "Cancel"));
		
		this.refreshWorldOptionsDisplay();
	}

	private void refreshWorldOptionsDisplay() {
		((GuiButtonText) this.controlList.get(0)).displayString = "Size: " + this.worldSize[this.selectedWorldSize];
		((GuiButtonText) this.controlList.get(1)).displayString = "Shape: " + this.worldShape[this.selectedWorldShape];
		((GuiButtonText) this.controlList.get(2)).displayString = "Type: " + this.worldType[this.selectedWorldType];
		((GuiButtonText) this.controlList.get(3)).displayString = "Theme: " + this.worldTheme[this.selectedWorldTheme];
	}

	protected final void actionPerformed(GuiButton button) {
		
		switch (button.id) {
		
			case 0:
				this.selectedWorldType = (this.selectedWorldType + 1) % this.worldType.length;
				break;
				
			case 1:
				this.selectedWorldShape = (this.selectedWorldShape + 1) % this.worldShape.length;
				break;
				
			case 2:
				this.selectedWorldSize = (this.selectedWorldSize + 1) % this.worldSize.length;
				break;
				
			case 3:
				this.selectedWorldTheme = (this.selectedWorldTheme + 1) % this.worldTheme.length;
				break;
				
			case 4:
				this.mc.generateLevel(this.selectedWorldSize, this.selectedWorldShape, this.selectedWorldType, this.selectedWorldTheme);
				this.mc.displayGuiScreen(null);
				break;
				
			case 5:
				this.mc.displayGuiScreen(this.prevGui);
				break;
		}

		this.refreshWorldOptionsDisplay();
	}

	public final void drawScreen(int mouseX, int mouseY) {
		
		this.drawDefaultBackground();
		drawCenteredString(this.fontRenderer, "Generate new level", this.width / 2, 40, 16777215);
		
		// show level dimensions
		short[] dim = this.mc.getLevelDimensions(this.selectedWorldSize, this.selectedWorldShape);
		drawCenteredString(this.fontRenderer, dim[0] + "x" + dim[2] + " (" + dim[1] + " tall)", this.width / 2, this.height / 4 + 24, 8421504);
		
		super.drawScreen(mouseX, mouseY);
	}
}
