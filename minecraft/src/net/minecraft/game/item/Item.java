package net.minecraft.game.item;

import java.util.Random;
import net.minecraft.game.entity.player.EntityPlayer;
import net.minecraft.game.level.World;
import net.minecraft.game.level.block.Block;

public class Item {
    
    // Random number generator for item operations
    protected static Random rand = new Random();
    // List of all registered items
    public static Item[] itemsList = new Item[1024];

 // Wood tools and weapons
    public static Item swordWood;
    public static Item pickaxeWood;
    public static Item axeWood;
    public static Item shovelWood;
    public static Item hoeWood;
    public static Item battleAxeWood;

 // Stone tools and weapons

    public static Item swordStone;
    
 // Stone tools and weapons
    public static Item pickaxeStone;
    public static Item axeStone;
    public static Item shovelStone;
    public static Item hoeStone;
    public static Item battleAxeStone;
    
    // Iron tools and weapons
    public static Item swordIron;
    public static Item pickaxeIron;
    public static Item axeIron;
    public static Item shovelIron;
    public static Item hoeIron;
    public static Item battleAxeIron;

    // Gold tools and weapons
    public static Item swordGold;
    public static Item pickaxeGold;
    public static Item axeGold;
    public static Item shovelGold;
    public static Item hoeGold;
    public static Item battleAxeGold;

    // Diamond tools and weapons
    public static Item swordDiamond;
    public static Item pickaxeDiamond;
    public static Item axeDiamond;
    public static Item shovelDiamond;
    public static Item hoeDiamond;
    public static Item battleAxeDiamond;

 // Iron armor pieces
    public static Item helmetIron;
    public static Item chestplateIron;
    public static Item leggingsIron;
    public static Item bootsIron;

 // Gold armor pieces
    public static Item helmetGold;
    public static Item chestplateGold;
    public static Item leggingsGold;
    public static Item bootsGold;

 // Diamond armor pieces
    public static Item helmetDiamond;
    public static Item chestplateDiamond;
    public static Item leggingsDiamond;
    public static Item bootsDiamond;

    // Shields
       public static Item ironShield;
       public static Item redClothCoveredShield;
       public static Item orangeClothCoveredShield;
       public static Item yellowClothCoveredShield;
       public static Item chartreuseClothCoveredShield;
       public static Item greenClothCoveredShield;
       public static Item springGreenClothCoveredShield;
       public static Item cyanClothCoveredShield;
       public static Item capriClothCoveredShield;
       public static Item ultramarineClothCoveredShield;
       public static Item violetClothCoveredShield;
       public static Item purpleClothCoveredShield;
       public static Item magentaClothCoveredShield;
       public static Item roseClothCoveredShield;
       public static Item darkGrayClothCoveredShield;
       public static Item grayClothCoveredShield;
       public static Item whiteClothCoveredShield;
    
 // Resource items
    public static Item coal;
    public static Item ingotIron;
    public static Item ingotGold;
    public static Item diamond;
    public static Item flintAndSteel;
    public static Item bow;
    public static Item arrow;
    public static ItemQuiver quiver;
    public static Item stick;
    public static Item bowlEmpty;
    public static Item bowlSoup;
    public static Item silk;
    public static Item feather;
    public static Item gunpowder;
    public static Item seeds;
    public static Item wheat;
    public static Item bread;
    public static Item sweetBread;
    public static Item flint;
    public static Item porkRaw;
    public static Item porkCooked;
    public static Item painting;
    public static Item apple;
    public static Item sugarBeet;
    public static Item sugar;
    public static Item applePie;
    public static Item rupeePlain;
    public static Item rupeeDark;
    public static Item snowball;

    public final int shiftedIndex;
    protected int maxStackSize = 64;
    public int maxDamage = 32;
    protected int iconIndex;
    protected String name;
    protected Rarity rarity;

    protected Item(String name, Rarity rarity, int index) {
        this.name = name;
        this.rarity = rarity;

        this.shiftedIndex = index + 256;

        if(itemsList[index + 256] != null)
            System.out.println("ITEM CONFLICT @ " + index);

        itemsList[index + 256] = this;
    }

    protected Item(String name, int index) {
        this(name, Rarity.COMMON, index);
    }

    public final Item setIconIndex(int iconIndex) {
        this.iconIndex = iconIndex;
        return this;
    }

    public int getIconIndex() {
        return this.iconIndex;
    }

    public final String getName() {
        return name;
    }

    public final Rarity getRarity() {
        return rarity;
    }

    public boolean onItemUse(ItemStack item, World world, int var3, int var4, int var5, int var6) {
        return false;
    }

    public float getStrVsBlock(Block block) {
        return 1.0F;
    }

    public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player) {
        return item;
    }

    public int getItemStackLimit() {
        return this.maxStackSize;
    }

    public final int getMaxDamage() {
        return this.maxDamage;
    }

    public void hitEntity(ItemStack item) {}

    public void onBlockDestroyed(ItemStack item) {}

    public int getDamageVsEntity() {
        return 1;
    }

    public boolean canHarvestBlock(Block block) {
        return false;
    }

    static {
        // Swords and Tools
        swordWood = new ItemSword("Wood Sword", 0, 0);
        swordWood.setIconIndex(64);

        pickaxeWood = new ItemPickaxe("Wood Pickaxe", 1, 0);
        pickaxeWood.setIconIndex(96);

        axeWood = new ItemAxe("Wood Axe", 2, 0);
        axeWood.setIconIndex(112);

        shovelWood = new ItemSpade("Wood Shovel", 3, 0);
        shovelWood.setIconIndex(80);

        hoeWood = new ItemHoe("Wood Hoe", 4, 0);
        hoeWood.setIconIndex(128);

        battleAxeWood = new ItemAxe("Wood Battle Axe", 5, 0);
        battleAxeWood.setIconIndex(117);

        swordStone = new ItemSword("Stone Sword", 6, 1);
        swordStone.setIconIndex(65);

        pickaxeStone = new ItemPickaxe("Stone Pickaxe", 7, 1);
        pickaxeStone.setIconIndex(97);

        axeStone = new ItemAxe("Stone Axe", 8, 1);
        axeStone.setIconIndex(113);

        shovelStone = new ItemSpade("Stone Shovel", 9, 1);
        shovelStone.setIconIndex(81);

        hoeStone = new ItemHoe("Stone Hoe", 10, 1);
        hoeStone.setIconIndex(129);

        battleAxeStone = new ItemAxe("Stone Battle Axe", 11, 1);
        battleAxeStone.setIconIndex(118);

        swordIron = new ItemSword("Iron Sword", 12, 2);
        swordIron.setIconIndex(66);

        pickaxeIron = new ItemPickaxe("Iron Pickaxe", 13, 2);
        pickaxeIron.setIconIndex(98);

        axeIron = new ItemAxe("Iron Axe", 14, 2);
        axeIron.setIconIndex(114);

        shovelIron = new ItemSpade("Iron Shovel", 15, 2);
        shovelIron.setIconIndex(82);

        hoeIron = new ItemHoe("Iron Hoe", 16, 2);
        hoeIron.setIconIndex(130);

        battleAxeIron = new ItemAxe("Iron Battle Axe", 17, 2);
        battleAxeIron.setIconIndex(119);

        swordGold = new ItemSword("Gold Sword", 18, 0);
        swordGold.setIconIndex(68);

        pickaxeGold = new ItemPickaxe("Gold Pickaxe", 19, 0);
        pickaxeGold.setIconIndex(100);

        axeGold = new ItemAxe("Gold Axe", 20, 0);
        axeGold.setIconIndex(116);

        shovelGold = new ItemSpade("Gold Shovel", 21, 0);
        shovelGold.setIconIndex(84);

        hoeGold = new ItemHoe("Gold Hoe", 22, 4);
        hoeGold.setIconIndex(132);

        battleAxeGold = new ItemAxe("Gold Battle Axe", 23, 0);
        battleAxeGold.setIconIndex(121);

        swordDiamond = new ItemSword("Diamond Sword", 24, 3);
        swordDiamond.setIconIndex(67);
        swordDiamond.rarity = Rarity.RARE;

        pickaxeDiamond = new ItemPickaxe("Diamond Pickaxe", 25, 3);
        pickaxeDiamond.setIconIndex(99);
        pickaxeDiamond.rarity = Rarity.RARE;

        axeDiamond = new ItemAxe("Diamond Axe", 26, 3);
        axeDiamond.setIconIndex(115);
        axeDiamond.rarity = Rarity.RARE;

        shovelDiamond = new ItemSpade("Diamond Shovel", 27, 3);
        shovelDiamond.setIconIndex(83);
        shovelDiamond.rarity = Rarity.RARE;

        hoeDiamond = new ItemHoe("Diamond Hoe", 28, 3);
        hoeDiamond.setIconIndex(131);
        hoeDiamond.rarity = Rarity.RARE;

        battleAxeDiamond = new ItemAxe("Diamond Battle Axe", 29, 3);
        battleAxeDiamond.setIconIndex(120);
        battleAxeDiamond.rarity = Rarity.RARE;

        // Armor
        helmetIron = new ItemArmor("Iron Helmet", 33, 2, 2, 0);
        helmetIron.setIconIndex(2);

        chestplateIron = new ItemArmor("Iron Chestplate", 34, 2, 2, 1);
        chestplateIron.setIconIndex(18);

        leggingsIron = new ItemArmor("Iron Leggings", 35, 2, 2, 2);
        leggingsIron.setIconIndex(34);

        bootsIron = new ItemArmor("Iron Boots", 36, 2, 2, 3);
        bootsIron.setIconIndex(50);

        helmetGold = new ItemArmor("Gold Helmet", 37, 1, 4, 0);
        helmetGold.setIconIndex(4);

        chestplateGold = new ItemArmor("Gold Chestplate", 38, 1, 4, 1);
        chestplateGold.setIconIndex(20);

        leggingsGold = new ItemArmor("Gold Leggings", 39, 1, 4, 2);
        leggingsGold.setIconIndex(36);

        bootsGold = new ItemArmor("Gold Boots", 40, 1, 4, 3);
        bootsGold.setIconIndex(52);

        helmetDiamond = new ItemArmor("Diamond Helmet", 41, 3, 3, 0);
        helmetDiamond.setIconIndex(3);
        helmetDiamond.rarity = Rarity.RARE;

        chestplateDiamond = new ItemArmor("Diamond Chestplate", 42, 3, 3, 1);
        chestplateDiamond.setIconIndex(19);
        chestplateDiamond.rarity = Rarity.RARE;

        leggingsDiamond = new ItemArmor("Diamond Leggings", 43, 3, 3, 2);
        leggingsDiamond.setIconIndex(35);
        leggingsDiamond.rarity = Rarity.RARE;

        bootsDiamond = new ItemArmor("Diamond Boots", 44, 3, 3, 3);
        bootsDiamond.setIconIndex(51);
        bootsDiamond.rarity = Rarity.RARE;
        
        bootsIron = new ItemArmor("Progrmmer Socks", 36, 2, 2, 3);
        bootsIron.setIconIndex(50);

     // Shields
        
        ironShield = new ItemShield("Iron Shield", 73, 1).setIconIndex(160); // First shield type
        redClothCoveredShield = new ItemShield("Red Cloth-Covered Shield", 74, 1).setIconIndex(161);
        orangeClothCoveredShield = new ItemShield("Orange Cloth-Covered Shield", 75, 1).setIconIndex(162);
        yellowClothCoveredShield = new ItemShield("Yellow Cloth-Covered Shield", 76, 1).setIconIndex(163);
        chartreuseClothCoveredShield = new ItemShield("Chartreuse Cloth-Covered Shield", 77, 1).setIconIndex(164);
        greenClothCoveredShield = new ItemShield("Green Cloth-Covered Shield", 78, 1).setIconIndex(165);
        springGreenClothCoveredShield = new ItemShield("Spring Green Cloth-Covered Shield", 79, 1).setIconIndex(166);
        cyanClothCoveredShield = new ItemShield("Cyan Cloth-Covered Shield", 80, 1).setIconIndex(167);
        capriClothCoveredShield = new ItemShield("Capri Cloth-Covered Shield", 81, 1).setIconIndex(168);
        ultramarineClothCoveredShield = new ItemShield("Ultramarine Cloth-Covered Shield", 82, 1).setIconIndex(169);
        violetClothCoveredShield = new ItemShield("Violet Cloth-Covered Shield", 83, 1).setIconIndex(170);
        purpleClothCoveredShield = new ItemShield("Purple Cloth-Covered Shield", 84, 1).setIconIndex(171);
        magentaClothCoveredShield = new ItemShield("Magenta Cloth-Covered Shield", 85, 1).setIconIndex(172);
        roseClothCoveredShield = new ItemShield("Rose Cloth-Covered Shield", 86, 1).setIconIndex(173);
        darkGrayClothCoveredShield = new ItemShield("Dark Gray Cloth-Covered Shield", 87, 1).setIconIndex(174);
        grayClothCoveredShield = new ItemShield("Gray Cloth-Covered Shield", 88, 1).setIconIndex(175); // Gray shield after dark gray
        whiteClothCoveredShield = new ItemShield("White Cloth-Covered Shield", 89, 1).setIconIndex(176); // White shield starting the new row

            // Tools
            flintAndSteel = new ItemFlintAndSteel("Flint and Steel", 45);
            flintAndSteel.setIconIndex(5);

            bow = new ItemBow("Bow", 46);
            bow.setIconIndex(21);

            arrow = new Item("Arrow", 47);
            arrow.setIconIndex(37);

            // Quiver should come right after the arrow
            quiver = new ItemQuiver("Quiver", 48);
            quiver.setIconIndex(54);

            // Food and Crops
            apple = new ItemFood("Apple", 49, 4);
            apple.setIconIndex(10);

            applePie = new ItemFood("Apple Pie", 50, 8);
            applePie.setIconIndex(11);

            bowlEmpty = new Item("Bowl", 51);
            bowlEmpty.setIconIndex(71);

            bowlSoup = new ItemSoup("Mushroom Stew", 52, 10);
            bowlSoup.setIconIndex(72);

            seeds = new ItemSeeds("Wheat Seeds", 53, Block.crops.blockID);
            seeds.setIconIndex(9);

            wheat = new Item("Wheat", 54);
            wheat.setIconIndex(25);

            bread = new ItemFood("Bread", 55, 5);
            bread.setIconIndex(41);

            sweetBread = new ItemFood("Sweet Bread", 56, 7);
            sweetBread.setIconIndex(57);

            porkRaw = new ItemFood("Raw Porkchop", 57, 3);
            porkRaw.setIconIndex(87);

            porkCooked = new ItemFood("Cooked Porkchop", 58, 8);
            porkCooked.setIconIndex(88);

            sugarBeet = new Item("Sugar Beet", 59);
            sugarBeet.setIconIndex(103);

            sugar = new Item("Sugar", 60);
            sugar.setIconIndex(104);

            // Miscellaneous
            coal = new Item("Coal", 63); // Coal is now at index 63
            coal.setIconIndex(7);

            ingotIron = new Item("Iron Ingot", 64); // Iron Ingot is now at index 64
            ingotIron.setIconIndex(23);

            rupeePlain = new Item("Rupee", 61); // Rupee now at index 61
            rupeePlain.setIconIndex(144);

            rupeeDark = new Item("Dark Rupee", 62); // Dark Rupee now at index 62
            rupeeDark.setIconIndex(146);

            ingotGold = new Item("Gold Ingot", 65); // Gold Ingot is now at index 65
            ingotGold.setIconIndex(39);

            diamond = new Item("Diamond", 66); // Diamond is now at index 66
            diamond.setIconIndex(55);
            diamond.rarity = Rarity.RARE;

            feather = new Item("Feather", 67);
            feather.setIconIndex(24);

            gunpowder = new Item("Sulphur", 68);
            gunpowder.setIconIndex(40);

            flint = new Item("Flint", 69);
            flint.setIconIndex(6);

            painting = new ItemPainting("Painting", 70);
            painting.setIconIndex(26);
            
            stick = new Item("Stick", 71);
            stick.setIconIndex(53);

            silk = new Item("Silk", 72);
            silk.setIconIndex(8);

            snowball = new Item("Snowball", 74);
            snowball.setIconIndex(56);
    }
}