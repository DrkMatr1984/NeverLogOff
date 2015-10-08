package com.pandaism.nlo.NPC.Traits;

import com.pandaism.nlo.MainHandler.SpawnNPC;
import com.pandaism.nlo.NeverLogOff;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDamageEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Pandaism on 8/30/2015.
 */
public class OfflinePlayerTrait extends Trait{
    NeverLogOff plugin;
    SpawnNPC snpc = new SpawnNPC(plugin);
    int task;
    int time = 2;

    public OfflinePlayerTrait() {
        super("offlineplayer");
        this.plugin = (NeverLogOff) Bukkit.getServer().getPluginManager().getPlugin("NeverLogOff");
    }

    @Override
    public void onSpawn(){
        UUID uuid  = this.plugin.getFileStorage().retrievePlayerUUID(this.getNPC().getName());
        Equipment equipment = this.getNPC().getTrait(Equipment.class);
        ItemStack helmet = this.plugin.getFileStorage().retrieveHelmetDroppables(uuid);
        ItemStack chest = this.plugin.getFileStorage().retrieveChestDroppables(uuid);
        ItemStack legging = this.plugin.getFileStorage().retrieveLeggingsDroppables(uuid);
        ItemStack boot = this.plugin.getFileStorage().retrieveBootsDroppables(uuid);
        ItemStack hand = this.plugin.getFileStorage().retrieveItemInHand(uuid);

        equipment.set(Equipment.EquipmentSlot.HELMET, helmet);
        equipment.set(Equipment.EquipmentSlot.CHESTPLATE, chest);
        equipment.set(Equipment.EquipmentSlot.LEGGINGS, legging);
        equipment.set(Equipment.EquipmentSlot.BOOTS, boot);
        equipment.set(Equipment.EquipmentSlot.HAND, hand);

    }

    @EventHandler
    public void deathNPC(NPCDeathEvent event) {
        //Clear inventory and reset everything
        //Drop exp
        NPC npc = event.getNPC();
        UUID uuid = this.plugin.getFileStorage().retrievePlayerUUID(npc.getName());
        World world = npc.getEntity().getWorld();
        final Chunk deathChunk = world.getChunkAt(npc.getEntity().getLocation());

        float expAmount = this.plugin.getFileStorage().retrieveEXP(uuid);
        float accumulatedAmount = 0;

        //Drop Experience
        do {
            Random random = new Random();
            if(expAmount < 100) {
                world.spawn(npc.getEntity().getLocation(), ExperienceOrb.class).setExperience(random.nextInt(5) + 1);
                accumulatedAmount += random.nextInt(5) + 1;
            } else if ((expAmount >= 100) && (expAmount < 1000)) {
                world.spawn(npc.getEntity().getLocation(), ExperienceOrb.class).setExperience(random.nextInt(50) + 20);
                accumulatedAmount += random.nextInt(50) + 20;
            } else if ((expAmount >= 1000) && (expAmount < 10000)) {
                world.spawn(npc.getEntity().getLocation(), ExperienceOrb.class).setExperience(random.nextInt(500) + 100);
                accumulatedAmount += random.nextInt(500) + 100;
            } else if (expAmount >= 10000) {
                world.spawn(npc.getEntity().getLocation(), ExperienceOrb.class).setExperience(random.nextInt(5000) + 1000);
                accumulatedAmount += random.nextInt(5000) + 1000;
            }

        } while(accumulatedAmount < expAmount);
        //Drop Inventory
        this.plugin.getFileStorage().dropItems(uuid, npc);

        //Set Death
        this.plugin.getFileStorage().setDeath(uuid);
        //Clear Inventory
        this.plugin.getFileStorage().clearInventory(uuid);
        //Clear Armor
        this.plugin.getFileStorage().clearArmor(uuid);
        //Reset Experience
        this.plugin.getFileStorage().setExperience(uuid);


        //New Spawn
        Random ran = new Random();
        int x = ran.nextInt(400);
        int z = ran.nextInt(400);

        Location newSpawn = new Location(world, world.getSpawnLocation().getX() + x, world.getHighestBlockYAt((int)world.getSpawnLocation().getX() + x,(int) world.getSpawnLocation().getZ() + z), world.getSpawnLocation().getZ() + z);
        this.plugin.getFileStorage().saveLocation(uuid, newSpawn);
        npc.spawn(newSpawn);
        Chunk respawnChunk = world.getChunkAt(newSpawn);
        snpc.chunkList.add(respawnChunk);

        //SetNPCUUID
        if(npc.getTrait(Equipment.class).get(Equipment.EquipmentSlot.HAND) == this.plugin.cream) {
            this.plugin.getFileStorage().setNPCUUID(npc.getUniqueId());
        }

        //Create a Timer
        task = Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            public void run() {
                time--;
                if(time == 0) {
                    Bukkit.getScheduler().cancelTask(task);
                    snpc.chunkList.remove(deathChunk);
                } else if(time > 0) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "citizens save");
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "citizens reload");
                }
            }
        }, 20);
    }


//    @EventHandler
//    public void picketpocketNPC(NPCRightClickEvent event) {
//        Player player = event.getClicker();
//        NPC npc = event.getNPC();
//        if(player.isSneaking()) {
//            Inventory inventory = Bukkit.createInventory(null, InventoryType.PLAYER, npc.getName() + "'s Inventory");
//            this.plugin.getFileStorage().loadPlayerInv(UUID.fromString(npc.getName()), inventory);
//        }
//    }
}
