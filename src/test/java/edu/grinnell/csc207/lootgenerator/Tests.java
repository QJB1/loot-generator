package edu.grinnell.csc207.lootgenerator;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.FileNotFoundException;
import java.util.Random;

public class Tests {

    /**
     * Helper function to load the small dataset.
     */
    private void loadSmallDataset() throws FileNotFoundException {
        LootGenerator.monsters.clear();
        LootGenerator.treasureClasses.clear();
        LootGenerator.armors.clear();
        LootGenerator.prefixes.clear();
        LootGenerator.suffixes.clear();
        LootGenerator.affixLines.clear();

        String base = "data/small/";
        LootGenerator.loadMonsters(base + "monstats.txt");
        LootGenerator.loadTreasureClasses(base + "TreasureClassEx.txt");
        LootGenerator.loadArmors(base + "armor.txt");
        LootGenerator.loadAffixes(base + "MagicPrefix.txt", LootGenerator.prefixes);
        LootGenerator.loadAffixes(base + "MagicSuffix.txt", LootGenerator.suffixes);

        LootGenerator.rand = new Random(42);
    }

    @Test
    public void testLoadMonsters() throws FileNotFoundException {
        loadSmallDataset();
        assertEquals(1, LootGenerator.monsters.size());
    }

    @Test
    public void testLoadTreasureClasses() throws FileNotFoundException {
        loadSmallDataset();
        assertTrue(LootGenerator.treasureClasses.containsKey("armo3"));
        assertEquals(3, LootGenerator.treasureClasses.get("armo3").size());
    }

    @Test
    public void testLoadArmors() throws FileNotFoundException {
        loadSmallDataset();
        assertTrue(LootGenerator.armors.containsKey("Leather Armor"));
        LootGenerator.Armor a = LootGenerator.armors.get("Leather Armor");
        assertEquals(14, a.minac);
        assertEquals(17, a.maxac);
    }

    @Test
    public void testLoadAffixes() throws FileNotFoundException {
        loadSmallDataset();
        assertEquals(5, LootGenerator.prefixes.size());
        assertEquals(5, LootGenerator.suffixes.size());
    }

    @Test
    public void testMonsterCount() throws FileNotFoundException {
        loadSmallDataset();
        assertEquals(1, LootGenerator.monsters.size());
    }

    @Test
    public void testArmorExists() throws FileNotFoundException {
        loadSmallDataset();
        assertTrue(LootGenerator.armors.containsKey("Leather Armor"));
    }

    @Test
    public void testGenerateBaseItemValid() throws FileNotFoundException {
        loadSmallDataset();
        String item = LootGenerator.generateBaseItem("Cow (H)");
        assertTrue("Unexpected base item: " + item,
                   LootGenerator.armors.containsKey(item));
    }

    @Test
    public void testGenerateBaseItemBaseCase() throws FileNotFoundException {
        loadSmallDataset();
        assertEquals("Leather Armor",
                     LootGenerator.generateBaseItem("Leather Armor"));
    }
    // edge cases
    @Test
    public void testGenerateBaseStatsFixedRange() {
        LootGenerator.Armor a = new LootGenerator.Armor("Fixed", 5, 5);
        for (int i = 0; i < 5; i++) {
            assertEquals(5, LootGenerator.generateBaseStats(a));
        }
    }

    @Test
    public void testGenerateAffixBounds() throws FileNotFoundException {
        loadSmallDataset();
        LootGenerator.generateAffix();
        int cnt = LootGenerator.affixLines.size();
        assertTrue("Affix count out of range: " + cnt,
                   cnt >= 0 && cnt <= 2);
    }

    @Test
    public void testEmptyAffixes() throws FileNotFoundException {
        loadSmallDataset();
        LootGenerator.prefixes.clear();
        LootGenerator.suffixes.clear();
        LootGenerator.affixLines.clear();
        LootGenerator.generateAffix();
        assertTrue(LootGenerator.affixLines.isEmpty());
    }

    @Test
    public void testUnknownTC() throws FileNotFoundException {
        loadSmallDataset();
        assertEquals("UnknownTC", LootGenerator.generateBaseItem("UnknownTC"));
    }
}

