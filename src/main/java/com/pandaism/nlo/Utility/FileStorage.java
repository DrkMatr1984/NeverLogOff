package com.pandaism.nlo.Utility;

import com.pandaism.nlo.NeverLogOff;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Pandaism on 8/30/2015.
 */
public class FileStorage {

    NeverLogOff plugin;
    private File invDataFile;
    private File playerNametoUUID;

    public FileStorage(NeverLogOff plugin) {this.plugin = plugin;}

    public boolean hasInvFile(UUID uuid) {
        return new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + uuid + ".yml").exists();
    }

    public boolean hasUUIDFile(String playername) {
        return new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "PlayerData" + System.getProperty("file.separator") + playername + ".yml").exists();
    }

    public boolean createInfoDataFile(UUID uuid, Player player) {
        try {
            File invFolder = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator"));
            this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + uuid + ".yml");
            if(!invFolder.exists()) {
                invFolder.mkdirs();
            }
            if(!this.invDataFile.exists()) {
                this.invDataFile.createNewFile();
                FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);
                String playerName = player.getName();
                ymlFormatting.set("Player.Name", playerName);

                ymlFormatting.save(this.invDataFile);
                return true;
            }
        }catch(Exception e) {
            this.plugin.getLogger().severe("Unable to create inventory data file for " + uuid + "!!!");
        }
        return false;
    }

    public boolean createPlayerUUIDFile(String playername, Player player) {
        try {
            File playerFolder = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "PlayerData" + System.getProperty("file.separator"));
            this.playerNametoUUID = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "PlayerData" + System.getProperty("file.separator") + playername + ".yml");

            if(!playerFolder.exists()) {
                playerFolder.mkdirs();
            }
            if(!this.playerNametoUUID.exists()) {
                this.playerNametoUUID.createNewFile();
                FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.playerNametoUUID);
                ymlFormatting.set("Player.Name", player.getName());
                ymlFormatting.set("Player.UUID", player.getUniqueId().toString());

                ymlFormatting.save(this.playerNametoUUID);
                return true;
            }
        } catch (Exception e) {
            this.plugin.getLogger().severe("Unable to create UUID File for " + playername + "!!!");
        }
        return false;
    }

    public boolean savePlayerInfo(Player player) {
        if(!hasUUIDFile(player.getName())) {
            createPlayerUUIDFile(player.getName(), player);
        }
        try {
            this.playerNametoUUID = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "PlayerData" + System.getProperty("file.separator") + player.getName() + ".yml");
            FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.playerNametoUUID);
            ymlFormatting.set("Player.Name", player.getName());
            ymlFormatting.set("Player.UUID", player.getUniqueId().toString());

            ymlFormatting.save(this.playerNametoUUID);
            return true;

        }catch(Exception e) {
            e.printStackTrace();
            this.plugin.getLogger().severe("Could not save player UUID of " + player.getName() + "!!!");
        }
        return false;
    }

    public boolean saveInfo(UUID uuid, String npcUUID, Player player, Integer size, ItemStack inventory) {
        if(!hasInvFile(uuid)) {
            createInfoDataFile(uuid, player);
        }
        try {
            this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + uuid + ".yml");
            FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);
            String playerName = player.getName();
            ymlFormatting.set("RelatedOfflineUUID", npcUUID);

            ymlFormatting.set("Player.Name", playerName);
            ymlFormatting.set("Player.EXP", player.getTotalExperience());
            ymlFormatting.set("Player.Health", player.getHealth());
            ymlFormatting.set("Player.Death", false);
            ymlFormatting.set("Coordinate.X", player.getLocation().getX());
            ymlFormatting.set("Coordinate.Y", player.getLocation().getY());
            ymlFormatting.set("Coordinate.Z", player.getLocation().getZ());

            ymlFormatting.set("InventorySlot." + size, inventory);
            ymlFormatting.set("ArmorSlots.Helmet", player.getEquipment().getHelmet());
            ymlFormatting.set("ArmorSlots.Chest", player.getEquipment().getChestplate());
            ymlFormatting.set("ArmorSlots.Leggings", player.getEquipment().getLeggings());
            ymlFormatting.set("ArmorSlots.Boot", player.getEquipment().getBoots());

            ymlFormatting.set("ItemInHand", player.getItemInHand());
            ymlFormatting.save(this.invDataFile);
            return true;
        }catch (Exception e) {
            this.plugin.getLogger().severe("Could not save player inventory of " + uuid + "!!!");
        }
        return false;
    }

    public boolean savePlayerInv(UUID uuid, String npcUUID, Player player, Inventory inv) {
        for(int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getContents()[i];
            saveInfo(uuid, npcUUID, player, Integer.valueOf(i), item);
        }
        return true;
    }

//    public boolean loadPlayerInv(UUID uuid, Inventory inventory) {
//        this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + uuid + ".yml");
//        ArrayList items = new ArrayList();
//        FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);
//        //Set Inventory
//        for (int i = 0; i < inventory.getSize(); i++) {
//            ItemStack item = ymlFormatting.getItemStack("InventorySlot." + i);
//            items.add(item);
//        }
//        ItemStack helmet = ymlFormatting.getItemStack("ArmorSlots.Helmet");
//        ItemStack chest = ymlFormatting.getItemStack("ArmorSlots.Chest");
//        ItemStack leggings = ymlFormatting.getItemStack("ArmorSlots.Leggings");
//        ItemStack boot = ymlFormatting.getItemStack("ArmorSlots.Boot");
//
//        ItemStack[] itemList = (ItemStack[]) items.toArray(new ItemStack[items.size()]);
//        inventory.setContents(itemList);
//        inventory.addItem(helmet);
//        inventory.addItem(chest);
//        inventory.addItem(leggings);
//        inventory.addItem(boot);
//        return true;
//    }

    public boolean loadPlayerInfo(UUID uuid, Player player, Inventory inventory) {
        this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + uuid + ".yml");
        ArrayList items = new ArrayList();
        FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);
        //Set Inventory
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = ymlFormatting.getItemStack("InventorySlot." + i);
            items.add(item);
        }
        if(ymlFormatting.getBoolean("Player.Death") == false) {
            ItemStack helmet = ymlFormatting.getItemStack("ArmorSlots.Helmet");
            ItemStack chest = ymlFormatting.getItemStack("ArmorSlots.Chest");
            ItemStack leggings = ymlFormatting.getItemStack("ArmorSlots.Leggings");
            ItemStack boot = ymlFormatting.getItemStack("ArmorSlots.Boot");

            ItemStack[] itemList = (ItemStack[]) items.toArray(new ItemStack[items.size()]);
            inventory.setContents(itemList);
            //Set Armor
            player.getEquipment().setHelmet(helmet);
            player.getEquipment().setChestplate(chest);
            player.getEquipment().setLeggings(leggings);
            player.getEquipment().setBoots(boot);
            items.clear();
        } else {
            inventory.clear();
            player.getEquipment().setHelmet(new ItemStack(Material.AIR));
            player.getEquipment().setChestplate(new ItemStack(Material.AIR));
            player.getEquipment().setLeggings(new ItemStack(Material.AIR));
            player.getEquipment().setBoots(new ItemStack(Material.AIR));


        }

        //SetEXP
        player.setTotalExperience(ymlFormatting.getInt("Player.EXP"));

        //SetHealth
        player.setHealth(ymlFormatting.getDouble("Player.Health"));

        //SetCoordinates
        Location newLocation = new Location(player.getWorld(), ymlFormatting.getDouble("Coordinate.X"), ymlFormatting.getDouble("Coordinate.Y"), ymlFormatting.getDouble("Coordinate.Z"));
        player.teleport(newLocation);

        return true;
    }

    public boolean clearInventory(UUID uuid) {
        this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + uuid + ".yml");
        try {
            FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);

            for (int i = 0; i < 36; i++) {
                ymlFormatting.set("InventorySlot." + i, null);
            }
            ymlFormatting.save(this.invDataFile);
        } catch(Exception e) {
            this.plugin.getLogger().severe("Could not clear inventory of " + uuid + "!!!");
        }
        return true;
    }

    public boolean setDeath(UUID uuid) {
        try {
            this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + uuid + ".yml");
            FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);

            ymlFormatting.set("Player.Death", true);
            ymlFormatting.save(this.invDataFile);
            return true;
        }catch (Exception e) {
            this.plugin.getLogger().severe("Could not set the death of " + uuid + "!!!");
        }
        return false;
    }

    public boolean setExperience(UUID uuid) {
        try {
            this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + uuid + ".yml");
            FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);

            ymlFormatting.set("Player.EXP", 0);
            ymlFormatting.save(this.invDataFile);
            return true;
        }catch (Exception e) {
            this.plugin.getLogger().severe("Could not set the death of " + uuid + "!!!");
        }
        return false;
    }

    public boolean saveLocation(UUID uuid, Location location) {
        try {
            this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + uuid + ".yml");
            FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();

            ymlFormatting.set("Coordinate.X", x);
            ymlFormatting.set("Coordinate.Y", y);
            ymlFormatting.set("Coordinate.Z", z);
            ymlFormatting.save(this.invDataFile);
            return true;
        }catch (Exception e) {
            this.plugin.getLogger().severe("Could not set the death of " + uuid + "!!!");
        }
        return false;
    }

    public ItemStack retrieveItemInHand(UUID playerUUID) {
        this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + playerUUID + ".yml");

        FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);
        ItemStack inHand = ymlFormatting.getItemStack("ItemInHand");
        if(inHand != null) {
            return inHand;
        } else {
            ItemStack nullHand = new ItemStack(Material.WOOD_SWORD);
            return nullHand;
        }
    }

    public UUID retrieveNPCUUID(UUID playerUUID) {
        this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + playerUUID + ".yml");
        if(this.invDataFile.exists()) {
            FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);
            String NPCUUIDString = (String) ymlFormatting.get("RelatedOfflineUUID");

            return UUID.fromString(NPCUUIDString);
        } else return null;
    }

    public UUID retrievePlayerUUID(String playername) {
        this.playerNametoUUID = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "PlayerData" + System.getProperty("file.separator") + playername + ".yml");

        if(this.playerNametoUUID.exists()) {
            FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.playerNametoUUID);
            String playerUUID = (String)ymlFormatting.get("Player.UUID");

            return UUID.fromString(playerUUID);
        } else return null;
    }

    public float retrieveEXP(UUID playerUUID) {
        this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + playerUUID + ".yml");
        if (this.invDataFile.exists()) {
            FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);
            float exp = (float)ymlFormatting.getDouble("Player.EXP");
            return exp;
        }
        Random random = new Random();
        return random.nextInt(3) + 1;
    }

    public void dropItems(UUID playerUUID, NPC npc) {
        this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + playerUUID + ".yml");
        FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);
        World world = npc.getEntity().getWorld();

        for(int i = 0; i < 36; i++) {
            ItemStack item = ymlFormatting.getItemStack("InventorySlot." + i);
            if(item != null) {
                world.dropItemNaturally(npc.getEntity().getLocation(), item);
            }
        }
    }

    public ItemStack retrieveHelmetDroppables(UUID playerUUID) {
        this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + playerUUID + ".yml");
        FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);

        ItemStack helmet = ymlFormatting.getItemStack("ArmorSlots.Helmet");
        if(helmet != null) {
            return helmet;
        } else {
            ItemStack nullMet = new ItemStack(Material.LEATHER_HELMET);
            return nullMet;
        }
    }

    public ItemStack retrieveChestDroppables(UUID playerUUID) {
        this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + playerUUID + ".yml");
        FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);

        ItemStack chest = ymlFormatting.getItemStack("ArmorSlots.Chest");
        if(chest != null) {
            return chest;
        } else {
            ItemStack nullChest = new ItemStack(Material.LEATHER_CHESTPLATE);
            return nullChest;
        }
    }

    public ItemStack retrieveLeggingsDroppables(UUID playerUUID) {
        this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + playerUUID + ".yml");
        FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);

        ItemStack leggings = ymlFormatting.getItemStack("ArmorSlots.Leggings");
        if(leggings != null) {
            return leggings;
        } else {
            ItemStack nullLegs = new ItemStack(Material.LEATHER_LEGGINGS);
            return nullLegs;
        }
    }

    public ItemStack retrieveBootsDroppables(UUID playerUUID) {
        this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + playerUUID + ".yml");
        FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);

        ItemStack boot = ymlFormatting.getItemStack("ArmorSlots.Boot");
        if(boot != null) {
            return boot;
        } else {
            ItemStack nullBoot = new ItemStack(Material.LEATHER_BOOTS);
            return nullBoot;
        }
    }

    public boolean clearArmor(UUID uuid) {
        this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + uuid + ".yml");
        try {
            FileConfiguration ymlFormatting = YamlConfiguration.loadConfiguration(this.invDataFile);

            ymlFormatting.set("ArmorSlots.Helmet", null);
            ymlFormatting.set("ArmorSlots.Chest", null);
            ymlFormatting.set("ArmorSlots.Leggings", null);
            ymlFormatting.set("ArmorSlots.Boot", null);

            ymlFormatting.save(this.invDataFile);
        } catch(Exception e) {
            this.plugin.getLogger().severe("Could not clear inventory of " + uuid + "!!!");
        }
        return true;
    }

    public boolean removeFile(UUID playerUUID) {
        this.invDataFile = new File("plugins" + System.getProperty("file.separator") + "NeverLogOff" + System.getProperty("file.separator") + "InventoryData" + System.getProperty("file.separator") + playerUUID + ".yml");

        if(this.invDataFile.exists()) {
            this.invDataFile.delete();
            return true;
        }

        return false;
    }
}
