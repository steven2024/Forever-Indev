package net.minecraft.game.item;

public final class ItemArmor extends Item {
    // Existing armor reduction values (for helmets, chestplates, leggings, and boots)
    private static final int[] damageReduceAmountArray = new int[] {3, 8, 6, 3};
    private static final int[] maxDamageArray = new int[] {11, 16, 15, 13};

    public final int armorType;
    public final int damageReduceAmount;
    public final int renderIndex;

    public ItemArmor(String name, int index, int var2, int renderIndex, int armorType) {
        super(name, index);
        this.armorType = armorType;
        this.damageReduceAmount = damageReduceAmountArray[armorType];
        this.maxDamage = maxDamageArray[armorType] * 3 << var2; // Item durability calculation
        this.renderIndex = renderIndex;
        this.maxStackSize = 1;
    }
}
