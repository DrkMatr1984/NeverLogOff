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
    NeverLogOff plugin;
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
                                if (damager.isDead()) {
                                    cancel();
                                }

                                npc.faceLocation(damager.getLocation());
                                ((Player) npc.getEntity()).launchProjectile(Arrow.class);
                                ((Player) npc.getEntity()).playSound(npc.getEntity().getLocation(), Sound.SHOOT_ARROW, 1, 0);
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
        }
//          else {
//            if(damager != npc && e.getDamage() > 0) {
//                initalLocation = npc.getStoredLocation();
//
//                //Check code
//                //Arrgo Distance Check
//                Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
//                    public void run() {
//                        if (npc.getNavigator().getEntityTarget() == null) {
//                            npc.getNavigator().getTargetAsLocation().add(initalLocation);
//                        } else if (npc.getNavigator().getTargetAsLocation().distance(initalLocation) > 30) {
//                            npc.getNavigator().getTargetAsLocation().add(initalLocation);
//                        } else {
//                            npc.getNavigator().setTarget(damager, true);
//                        }
//                    }
//                }, 0L, 5L);
//            }
//        }
    }
}
