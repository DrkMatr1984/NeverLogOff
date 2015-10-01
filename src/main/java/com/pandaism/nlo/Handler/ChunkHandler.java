package com.pandaism.nlo.Handler;

import com.pandaism.nlo.MainHandler.SpawnNPC;
import com.pandaism.nlo.NeverLogOff;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pandaism on 9/12/2015.
 */
public class ChunkHandler implements Listener {

    NeverLogOff plugin;
    SpawnNPC snpc = new SpawnNPC(plugin);

    public ChunkHandler(NeverLogOff plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void chunkCheck(ChunkUnloadEvent e) {
        Chunk chunk = e.getChunk();
        World world = chunk.getWorld();

        if(snpc.chunkList.contains(chunk)) {

            for(Entity entity : world.getEntities()) {
                if((!(entity instanceof NPC)) || (!(entity instanceof Item)) && chunk.getEntities().length > 200) {
                    entity.remove();
                }
            }
            chunk.load();
        } else {
            return;
        }
    }
}
