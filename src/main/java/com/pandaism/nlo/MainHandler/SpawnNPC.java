package com.pandaism.nlo.MainHandler;

import com.pandaism.nlo.NPC.Traits.OfflinePlayerAI;
import com.pandaism.nlo.NPC.Traits.OfflinePlayerTrait;
import com.pandaism.nlo.NeverLogOff;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Pandaism on 8/26/2015.
 */
public class SpawnNPC implements Listener{
    NeverLogOff plugin;
    public ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
    private NPCRegistry registry;
    public SpawnNPC(NeverLogOff plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerLogOff(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
        UUID NPCUUID;
        NPC npc;
        registry = CitizensAPI.getNPCRegistry();

        if(!chunkList.contains(chunk)) {
            chunkList.add(chunk);
        }

        if(this.plugin.getFileStorage().retrieveNPCUUID(player.getUniqueId()) != null) {
            NPCUUID = this.plugin.getFileStorage().retrieveNPCUUID(player.getUniqueId());
        } else {
            NPCUUID = null;
        }

        if(NPCUUID != null) {
            if(registry.getByUniqueIdGlobal(NPCUUID).isSpawned()) {
                npc = registry.getByUniqueIdGlobal(NPCUUID);
                npc.addTrait(OfflinePlayerTrait.class);
                npc.addTrait(OfflinePlayerAI.class);
                npc.setProtected(false);
                if(player.getItemInHand().equals(this.plugin.cream)) {
                    npc.setName("");
                    npc.teleport(player.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                } else {
                    npc.teleport(player.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
                this.plugin.getFileStorage().savePlayerInv(player.getUniqueId(), npc.getUniqueId().toString(), player, player.getInventory());
                this.plugin.getFileStorage().savePlayerInfo(player);

            }
        } else {
            npc = registry.createNPC(EntityType.PLAYER, player.getName());
            npc.addTrait(OfflinePlayerTrait.class);
            npc.addTrait(OfflinePlayerAI.class);
            npc.getDefaultGoalController();
            npc.setProtected(false);

            if(player.getItemInHand().equals(this.plugin.cream)) {
                npc.setName("");
                npc.spawn(player.getLocation());
            } else {
                npc.spawn(player.getLocation());
            }


            this.plugin.getFileStorage().savePlayerInv(player.getUniqueId(), npc.getUniqueId().toString(), player, player.getInventory());
            this.plugin.getFileStorage().savePlayerInfo(player);
        }
    }

    @EventHandler
    public void playerLogIn(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
        UUID NPCUUID;
        NPC npc;
        registry = CitizensAPI.getNPCRegistry();

        for(Entity entity : chunk.getEntities()) {
            if(entity instanceof NPC) {
                if(((NPC) entity).hasTrait(OfflinePlayerTrait.class)) {
                    break;
                } else {
                    chunkList.remove(chunk);
                }
            }
        }

        if(this.plugin.getFileStorage().retrieveNPCUUID(player.getUniqueId()) != null) {
            NPCUUID = this.plugin.getFileStorage().retrieveNPCUUID(player.getUniqueId());
        } else {
            NPCUUID = null;
        }

        if(NPCUUID != null) {
            if(registry.getByUniqueIdGlobal(NPCUUID).isSpawned()) {

                this.plugin.getFileStorage().loadPlayerInfo(player.getUniqueId(), player, player.getInventory());
                npc = registry.getByUniqueIdGlobal(NPCUUID);
                npc.despawn();

                if(this.plugin.getFileStorage().retrieveItemInHand(player.getUniqueId()).equals(this.plugin.cream)) {
                    if(this.plugin.getFileStorage().retrieveItemInHand(player.getUniqueId()).getAmount() > 1) {
                        player.getInventory().getItemInHand().setAmount(this.plugin.getFileStorage().retrieveItemInHand(player.getUniqueId()).getAmount() - 1);
                    } else {
                        player.setItemInHand(new ItemStack(Material.AIR));
                    }
                }
                registry.deregister(npc);
                this.plugin.getFileStorage().removeFile(player.getUniqueId());
            }
        }

    }
}
