package net.minecraft.game.item.enchant;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Enchant implements Serializable {
    private static final long serialVersionUID = 1L; // Ensure version compatibility

    private final EnchantType type;
    private final int level;
    
    private static final Map<Integer, String> ROMAN_NUMERALS = createRomanNumeralsMap();

    public Enchant(EnchantType type, int level) {
        this.type = type;
        this.level = level;
    }
    
    public EnchantType getType() {
        return type;
    }
    
    public int getLevel() {
        return level;
    }

    private static Map<Integer, String> createRomanNumeralsMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "I");
        map.put(2, "II");
        map.put(3, "III");
        map.put(4, "IV");
        map.put(5, "V");
        map.put(6, "VI");
        map.put(7, "VII");
        map.put(8, "VIII");
        map.put(9, "IX");
        map.put(10, "X");
        return map;
    }

    @Override
    public String toString() {
        String levelString = ROMAN_NUMERALS.getOrDefault(level, String.valueOf(level));
        return type + " " + levelString;
    }
}
