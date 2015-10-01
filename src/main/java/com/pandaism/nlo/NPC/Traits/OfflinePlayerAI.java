package com.pandaism.nlo.NPC.Traits;

import com.pandaism.nlo.NeverLogOff;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;

/**
 * Created by Pandaism on 9/11/2015.
 */
public class OfflinePlayerAI extends Trait {
    NeverLogOff plugin;
    Location initalLocation;
    Entity damager;

    public OfflinePlayerAI() {
        super("offlineai");
        this.plugin = (NeverLogOff) Bukkit.getServer().getPluginManager().getPlugin("NeverLogOff");
    }


    @EventHandler
    public void onDamage(NPCDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        NPC npc = e.getNPC();

        if ((damager instanceof Projectile)) {
            damager = (Entity)((Projectile)damager).getShooter();
        }

        if ((damager != npc) && (e.getDamage() > 0.0D))
            npc.getNavigator().setTarget(damager, true);

//        damager = e.getDamager();
//        final NPC npc = e.getNPC();
//        EntityType[] passiveProjectiles = {EntityType.EGG, EntityType.SNOWBALL, EntityType.FISHING_HOOK};

//        if(damager instanceof Projectile) {
//            damager = (Entity)((Projectile) damager).getShooter();
//
//            //Check code
//            //Passive Projectile Check
//            for(int i = 0; i < passiveProjectiles.length; i++) {
//                if(e.getDamager().getType() == passiveProjectiles[i]) {
//                    e.setCancelled(true);
//                }
//            }
//
//
//        } else {
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
