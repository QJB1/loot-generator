package edu.grinnell.csc207.lootgenerator;

import java.io.*;
import java.util.*;

public class LootGenerator {
    static class Monster {
        String name, treasureClass;
        Monster(String name, String treasureClass) {
            this.name = name;
            this.treasureClass = treasureClass;
        }
    }

    static class Armor {
        String name;
        int minac, maxac;
        Armor(String name, int minac, int maxac) {
            this.name = name;
            this.minac = minac;
            this.maxac = maxac;
        }
    }

    static class Affix {
        String name, stat;
        int min, max;
        Affix(String name, String stat, int min, int max) {
            this.name = name;
            this.stat = stat;
            this.min = min;
            this.max = max;
        }
    }
    // to store text files
    static Random rand = new Random();
    static List<Monster> monsters = new ArrayList<>();
    static Map<String, List<String>> treasureClasses = new HashMap<>();
    static Map<String, Armor> armors = new HashMap<>();
    static List<Affix> prefixes = new ArrayList<>();
    static List<Affix> suffixes = new ArrayList<>();

    // affix strs and new base item name
    static String prefixStr = "";
    static String suffixStr = "";
    static List<String> affixLines = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // loads text files into its classes
        String base = "data/small/";
        loadMonsters(base + "monstats.txt");
        System.out.println("Monsters loaded: " + monsters.size());
        loadTreasureClasses(base + "TreasureClassEx.txt");
        loadArmors(base + "armor.txt");
        loadAffixes(base + "MagicPrefix.txt", prefixes);
        loadAffixes(base + "MagicSuffix.txt", suffixes);

        Scanner input = new Scanner(System.in);
        // enters loop, doesnt exit until prompted
        while (true) {
            // generate rand monster, print info 
            Monster m = pickMonster();
            System.out.println("Fighting " + m.name);
            System.out.println("You have slain " + m.name + "!");
            System.out.println(m.name + " dropped:\n");
            
            // generate info about item
            String baseItemName = generateBaseItem(m.treasureClass); // generate base item using TC
            Armor armor = armors.get(baseItemName); // use base item to get armor 
            int defense = generateBaseStats(armor); // now use armor to get base stat
            
            // print info about item
            generateAffix();
            System.out.println(prefixStr + armor.name + suffixStr); 
            System.out.println("Defense: " + defense);
            for (int i = 0; i < affixLines.size(); i++){
                System.out.println(affixLines.get(i));
            }

            System.out.print("\nFight again [y/n]? ");
            String choice = input.nextLine();
            while (!choice.equalsIgnoreCase("y") && !choice.equalsIgnoreCase("n")) {
                System.out.print("Fight again [y/n]? ");
                choice = input.nextLine();
            }
            if (choice.equalsIgnoreCase("n")){
                break;
            }
        }
        input.close();
    }

    /**
     * Scans a monstats txt file and adds the information into an arraylist
     *
     * @param String the file path
     */
    static void loadMonsters(String path) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(path));
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isBlank()) continue;
            // split into 4 cols - name, type, level, treasureClass
            String[] parts = line.split("\t");
            if (parts.length < 4) continue;
            String name           = parts[0];
            String treasureClass  = parts[3];
            monsters.add(new Monster(name, treasureClass));
        }
        sc.close();
    }
    
    /**
     * Scans a treasureclass txt file and adds the information into a map
     *
     * @param String the file path
     */
    static void loadTreasureClasses(String path) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(path));
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isBlank()) continue;
            // split into 4 cols - TC, item1, item2, item3
            String[] parts = line.split("\t", 4);
            if (parts.length < 4) continue;
            treasureClasses.put(parts[0],
                                 Arrays.asList(parts[1], parts[2], parts[3]));
        }
        sc.close();
    }
    
    /**
     * Scans an armor txt file and adds the information into a map
     *
     * @param String the file path
     */
    static void loadArmors(String path) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(path));
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isBlank()) continue;
            // split into 3 columns - name, minac, maxac
            String[] parts = line.split("\t", 3);
            if (parts.length < 3) continue;
            String name = parts[0];
            int minac   = Integer.parseInt(parts[1]);
            int maxac   = Integer.parseInt(parts[2]);
            armors.put(name, new Armor(name, minac, maxac));
        }
        sc.close();
    }
    
    /**
     * Scans an affix txt file and adds the information into a list
     *
     * @param String the file path
     * @param List<Affix> the list containing the affixes
     */
    static void loadAffixes(String path, List<Affix> list) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(path));
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isBlank()) continue;
            // split into 4 cols - affixName, statText, min, max
            String[] parts = line.split("\t", 4);
            if (parts.length < 4) continue;
            String name = parts[0];
            String stat = parts[1];
            int min     = Integer.parseInt(parts[2]);
            int max     = Integer.parseInt(parts[3]);
            list.add(new Affix(name, stat, min, max));
        }
        sc.close();
    }
    
    /**
     * Randomly chooses a monster from all those available
     * 
     * @return Monster the monster chosen
     */
    static Monster pickMonster(){
        return monsters.get(rand.nextInt(monsters.size()));
    }

    /**
     * Takes a treasureClass and keeps retrieving its items until it gets a 
     * base item
     *
     * @param String the treasure class
     * @return String the base item generated
     */
    static String generateBaseItem(String tc) {
        List<String> choices = treasureClasses.get(tc);
        if (choices == null){ // when it's base item, return the key
            return tc; 
        }
        // when it's not base item, keeps finding a random base item
        String chosen = choices.get(rand.nextInt(3));
        return generateBaseItem(chosen);
    }

    /**
     * Takes an Armor type of base item and calculates its stat
     *
     * @param Armor the base item
     * @return int the base stat
     */
    static int generateBaseStats(Armor armor) {
        return rand.nextInt(armor.maxac - armor.minac + 1) + armor.minac;
    }

    /**
     * Generates the prefix or affix randomly, and updates those variables 
     *
     */
    static void generateAffix() {
        prefixStr = "";
        suffixStr = "";
        affixLines.clear();
        // 50% of generating each, and if generated it calculates a value and adds it on to the base item
        if (rand.nextBoolean() && !prefixes.isEmpty()) {
            Affix p = prefixes.get(rand.nextInt(prefixes.size()));
            int val = rand.nextInt(p.max - p.min + 1) + p.min; // 
            prefixStr = p.name + " ";
            affixLines.add(val + " " + p.stat);
        }
        if (rand.nextBoolean() && !suffixes.isEmpty()) {
            Affix s = suffixes.get(rand.nextInt(suffixes.size()));
            int val = rand.nextInt(s.max - s.min + 1) + s.min;
            suffixStr = " " + s.name;
            affixLines.add(val + " " + s.stat);
        }
    }
}
