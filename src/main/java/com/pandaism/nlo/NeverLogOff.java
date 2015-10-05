package com.pandaism.nlo;

import com.pandaism.nlo.MainHandler.SpawnNPC;
import com.pandaism.nlo.NPC.AI.OfflinePlayerDamageMod;
import com.pandaism.nlo.NPC.Traits.OfflinePlayerAI;
import com.pandaism.nlo.NPC.Traits.OfflinePlayerTrait;
import com.pandaism.nlo.Utility.FileStorage;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Pandaism on 8/26/2015.
 *
 * - Failed -
 * Added Magma Cream Camo
 * Added Colored name to Camo [NeverLogOff.onEnabled line 41]
 * Added new method in NeverLogOff
 * SpawnNPC camo check
 *
 *
 * Changed OfflinePlayerAI.java
 */
public class NeverLogOff extends JavaPlugin {
    FileStorage fs;
    public String creamName = ChatColor.DARK_PURPLE + "Camouflage Cream";
    public ItemStack cream;

    @Override
    public void onEnable() {
        if(getServer().getPluginManager().getPlugin("Citizens") == null || !getServer().getPluginManager().getPlugin("Citizens").isEnabled()) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //Register Events
        this.getServer().getPluginManager().registerEvents(new SpawnNPC(this), this);
        this.getServer().getPluginManager().registerEvents(new OfflinePlayerDamageMod(this), this);

        //Register Traits
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(OfflinePlayerTrait.class).withName("offlineplayer"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(OfflinePlayerAI.class).withName("offlineai"));
        //CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(OfflinePlayerTrait.class).withName("Pickpocketable"));

        //Recipes
        makeRecipe();
        this.fs = new FileStorage(this);
    }

    @Override
    public void onDisable() {

    }

    private void makeRecipe() {
        //CamoCream Meta and Recipe
        List<String> creamLore = new ArrayList<String>();
        creamLore.add(ChatColor.AQUA + "Apply evenly to hide yourself upon logging off");
        cream = new ItemStack(Material.MAGMA_CREAM, 1);
        ItemMeta creamMeta = cream.getItemMeta();
        creamMeta.setDisplayName(creamName);
        creamMeta.setLore(creamLore);
        cream.setItemMeta(creamMeta);

        ShapedRecipe camoCream = new ShapedRecipe(cream);
        camoCream.shape("SSW",
                        "SGB",
                        "FBB");
        camoCream.setIngredient('S', Material.SLIME_BALL);
        camoCream.setIngredient('B', Material.BLAZE_POWDER);
        camoCream.setIngredient('W', Material.NETHER_STALK);
        camoCream.setIngredient('G', Material.GOLDEN_CARROT);
        camoCream.setIngredient('F', Material.FERMENTED_SPIDER_EYE);

        //Add Recipe
        Bukkit.addRecipe(camoCream);
    }

    public FileStorage getFileStorage() {
        return this.fs;
    }
}
