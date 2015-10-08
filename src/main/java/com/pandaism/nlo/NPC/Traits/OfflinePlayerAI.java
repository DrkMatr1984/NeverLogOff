package com.pandaism.nlo.NPC.Traits;

import com.pandaism.nlo.NeverLogOff;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * Created by Pandaism on 9/11/2015.
 */
public class OfflinePlayerAI extends Trait {
    public NeverLogOff plugin;
    Entity damager;

    public OfflinePlayerAI() {
        super("offlineai");
        this.plugin = (NeverLogOff) Bukkit.getServer().getPluginManager().getPlugin("NeverLogOff");
    }


    @EventHandler
    public void onDamage(NPCDamageByEntityEvent e) {
        damager = e.getDamager();
        final NPC npc = e.getNPC();
        EntityType[] passiveProjectiles = {EntityType.EGG, EntityType.SNOWBALL, EntityType.FISHING_HOOK};
        EntityType[] aggressiveProjectiles = {EntityType.ARROW, EntityType.FIREBALL, EntityType.SMALL_FIREBALL, EntityType.WITHER_SKULL};

        if(damager instanceof Projectile) {
            damager = (Entity)((Projectile) damager).getShooter();

            //Passive Projectile Check
            for(int i = 0; i < passiveProjectiles.length; i++) {
                if(e.getDamager().getType() == passiveProjectiles[i]) {
                    npc.faceLocation(damager.getLocation());
                }
            }

            //Aggressive Projectile Check
            for(int i = 0; i < aggressiveProjectiles.length; i++) {
                if(e.getDamager().getType() == aggressiveProjectiles[i]) {
                    if(npc.getTrait(Equipment.class).get(Equipment.EquipmentSlot.HAND).equals(new ItemStack(Material.BOW))) {

                        new BukkitRunnable() {
                            public void run() {
                                npc.faceLocation(damager.getLocation());

                                double npcInitialX = npc.getEntity().getLocation().getX();
                                double npcInitialZ = npc.getEntity().getLocation().getZ();

                                double targetLocationX = damager.getLocation().getX();
                                double targetLocationZ = damager.getLocation().getZ();

                                //Check Line of Sight
                                if(((LivingEntity)npc.getEntity()).hasLineOfSight(damager)) {
                                    //check if damager is dead
                                    if(!damager.isDead()) {
                                        double x = npcInitialX - targetLocationX;
                                        double z = npcInitialZ - targetLocationZ;

                                        //Check 2 circles; inner r=8, outer r=16
                                        if((Math.pow(x, 2) + Math.pow(z, 2) < 256) && (Math.pow(x, 2) + Math.pow(z, 2) > 64)) {
                                            npc.getNavigator().setTarget(damager.getLocation());
                                        } else if(Math.pow(x, 2) + Math.pow(z, 2) <= 64) {
                                            npc.getNavigator().cancelNavigation();
                                            ((Player) npc.getEntity()).launchProjectile(Arrow.class);
                                            ((Player) npc.getEntity()).playSound(npc.getEntity().getLocation(), Sound.SHOOT_ARROW, 1, 0);
                                        } else if(Math.pow(x, 2) + Math.pow(z, 2) >= 256) {
                                            npc.getNavigator().cancelNavigation();
                                            //Check saving and cancel
                                            plugin.getFileStorage().saveLocation(plugin.getFileStorage().retrievePlayerUUID(npc.getName()), npc.getEntity().getLocation());
                                            cancel();
                                        }
                                    } else {
                                        plugin.getFileStorage().saveLocation(plugin.getFileStorage().retrievePlayerUUID(npc.getName()), npc.getEntity().getLocation());
                                        cancel();
                                    }
                                } else {
                                    if(!damager.isDead()) {
                                        double x = npcInitialX - targetLocationX;
                                        double z = npcInitialZ - targetLocationZ;

                                        if((Math.pow(x, 2) + Math.pow(z, 2) < 256) && (Math.pow(x, 2) + Math.pow(z, 2) > 64)) {
                                            npc.getNavigator().setTarget(damager.getLocation());
                                        } else if(Math.pow(x, 2) + Math.pow(z, 2) <= 64) {
                                            npc.getNavigator().setTarget(damager.getLocation());
                                        } else if(Math.pow(x, 2) + Math.pow(z, 2) >= 256) {
                                            npc.getNavigator().cancelNavigation();
                                            //Check saving and cancel
                                            plugin.getFileStorage().saveLocation(plugin.getFileStorage().retrievePlayerUUID(npc.getName()), npc.getEntity().getLocation());
                                            cancel();
                                        }
                                    } else {
                                        //Check saving and cancel
                                        plugin.getFileStorage().saveLocation(plugin.getFileStorage().retrievePlayerUUID(npc.getName()), npc.getEntity().getLocation());
                                        cancel();
                                    }

                                }

                            }
                        }.runTaskTimer(this.plugin, 0L, 15L);
                    } else {
                        npc.getNavigator().setTarget(damager, true);
                        break;
                    }
                    break;
                }
            }

        } else if((damager != npc) && (e.getDamage() > 0.0D)) {
            npc.getNavigator().setTarget(damager, true);

            new BukkitRunnable() {
                public void run() {
                    if(damager.isDead()) {
                        plugin.getFileStorage().saveLocation(plugin.getFileStorage().retrievePlayerUUID(npc.getName()), npc.getEntity().getLocation());
                        cancel();
                    } else {
                        plugin.getFileStorage().saveLocation(plugin.getFileStorage().retrievePlayerUUID(npc.getName()), npc.getEntity().getLocation());
                    }
                }
            }.runTaskTimer(this.plugin, 0L, 5L);
        }
    }
}
