package net.minecraft.game.item.enchant;

public enum EnchantType {
    
    fiery("Fiery"),
    detonation("Detonation"),
    quickshot("Quickshot");

    private final String name;

    EnchantType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
