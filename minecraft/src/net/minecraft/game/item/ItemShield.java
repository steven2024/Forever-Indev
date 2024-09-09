package net.minecraft.game.item;

public final class ItemShield extends Item {

    public final int renderIndex;

    public ItemShield(String name, int index, int renderIndex) {
        super(name, index);

        this.renderIndex = renderIndex;
        this.maxStackSize = 1;
    }
}
