package com.pandaism.nlo.NPC.AI;

import com.pandaism.nlo.NeverLogOff;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pandaism on 9/10/2015.
 */
public class OfflinePlayerDamageMod implements Listener{
    NeverLogOff plugin;
    Map<Enchantment, Integer> enchantMap;

    public OfflinePlayerDamageMod(NeverLogOff plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNPCDamage(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player) {
            Entity beingAttacked = e.getEntity();
            Entity attacker = e.getDamager();
            enchantMap = new HashMap<Enchantment, Integer>();

            if((!beingAttacked.hasMetadata("NPC")) && attacker.hasMetadata("NPC")) {
                ItemStack sword = ((Player)attacker).getItemInHand();
                if(sword.getType() == Material.DIAMOND_SWORD) {
                    enchantMap = sword.getEnchantments();

                    if(!enchantMap.containsKey(Enchantment.DAMAGE_ALL)) {
                        e.setDamage(8);
                    }else if(enchantMap.get(Enchantment.DAMAGE_ALL) == 1) {
                        e.setDamage(9.25);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) == 2) {
                        e.setDamage(10.5);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) == 3) {
                        e.setDamage(11.75);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) == 4) {
                        e.setDamage(13);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) >= 5) {
                        e.setDamage(14.25);
                    }
                } else if (sword.getType() == Material.IRON_SWORD) {
                    enchantMap = sword.getEnchantments();

                    if(!enchantMap.containsKey(Enchantment.DAMAGE_ALL)) {
                        e.setDamage(7);
                    } else if(enchantMap.get(Enchantment.DAMAGE_ALL) == 1) {
                        e.setDamage(8.25);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) == 2) {
                        e.setDamage(9.5);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) == 3) {
                        e.setDamage(10.75);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) == 4) {
                        e.setDamage(12);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) >= 5) {
                        e.setDamage(13.25);
                    }

                } else if (sword.getType() == Material.STONE_SWORD) {
                    enchantMap = sword.getEnchantments();

                    if(!enchantMap.containsKey(Enchantment.DAMAGE_ALL)) {
                        e.setDamage(6);
                    } else if(enchantMap.get(Enchantment.DAMAGE_ALL) == 1) {
                        e.setDamage(7.25);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) == 2) {
                        e.setDamage(8.5);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) == 3) {
                        e.setDamage(9.75);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) == 4) {
                        e.setDamage(10);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) >= 5) {
                        e.setDamage(11.25);
                    }

                } else if (sword.getType() == Material.GOLD_SWORD || sword.getType() == Material.WOOD_SWORD) {
                    enchantMap = sword.getEnchantments();

                    if(!enchantMap.containsKey(Enchantment.DAMAGE_ALL)) {
                        e.setDamage(5);
                    } else if(enchantMap.get(Enchantment.DAMAGE_ALL) == 1) {
                        e.setDamage(6.25);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) == 2) {
                        e.setDamage(7.5);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) == 3) {
                        e.setDamage(8.75);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) == 4) {
                        e.setDamage(9);
                    } else if (enchantMap.get(Enchantment.DAMAGE_ALL) >= 5) {
                        e.setDamage(10.25);
                    }
                } else {
                    e.setDamage(1);
                }
            } else {
                return;
            }
        }
    }
}
