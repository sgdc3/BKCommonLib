package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;

public class EntityTrackerEntryHook extends ClassHook<EntityTrackerEntryHook> {
    private EntityNetworkController<?> controller;

    public EntityNetworkController<?> getController() {
        return controller;
    }

    public void setController(EntityNetworkController<?> controller) {
        this.controller = controller;
    }

    @HookMethod("public void scanPlayers(List<EntityHuman> list)")
    public void scanPlayers(List<?> list) {
        base.scanPlayers(list);
    }

    @HookMethod("public void track(List<EntityHuman> list)")
    public void track(List<?> list) {
        EntityTrackerEntryHandle handle = EntityTrackerEntryHandle.createHandle(instance());
        updateTrackers(handle, list);
        handle.setTimeSinceLocationSync(handle.getTimeSinceLocationSync() + 1);
        try {
            controller.onTick();
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to synchronize:");
            t.printStackTrace();
        }
        handle.setTickCounter(handle.getTickCounter() + 1);
    }

    @HookMethod("public void hideForAll:???()")
    public void hideForAll() {
        try {
            controller.makeHiddenForAll();

            // This is usually only called when the entity tracker entry is removed from the mapping
            // Detect that it is, and if it is, call onDetached() on the network controller
            CommonEntity<?> entity = controller.getEntity();
            if (entity != null) {
                World world = entity.getWorld();
                EntityTrackerEntryHandle eh = EntityTrackerEntryHandle.createHandle(instance());
                if (world != null && !WorldUtil.getTracker(world).containsEntry(eh)) {
                    controller.bind(null, null);
                }
            }
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to hide for all viewers:");
            t.printStackTrace();
        }
    }

    @HookMethod("public void clear(EntityPlayer entityplayer)")
    public void clear(Object entityplayer) {
        try {
            controller.removeViewer(Conversion.toPlayer.convert(entityplayer));
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to remove viewer:");
            t.printStackTrace();
        }
    }

    @HookMethod("public void removeViewer:???(EntityPlayer entityplayer)")
    public void removeViewer(Object entityplayer) {
        try {
            controller.removeViewer(Conversion.toPlayer.convert(entityplayer));
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to remove viewer:");
            t.printStackTrace();
        }
    }

    @HookMethod("public void updatePlayer(EntityPlayer entityplayer)")
    public void updatePlayer(Object entityplayer) {
        if (entityplayer != controller.getEntity().getHandle()) {
            try {
                controller.updateViewer(Conversion.toPlayer.convert(entityplayer));
            } catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to update viewer:");
                t.printStackTrace();
            }
        }
    }

    private void updateTrackers(EntityTrackerEntryHandle handle, List<?> list) {
        EntityHandle entityHandle = handle.getTracker();
        if (entityHandle != null) {
            if (handle.isSynched()) {
                double lastSyncX = handle.getPrevX();
                double lastSyncY = handle.getPrevY();
                double lastSyncZ = handle.getPrevZ();
                double distance = entityHandle.calculateDistance(lastSyncX, lastSyncY, lastSyncZ);
                if (distance <= 16.0) {
                    return;
                }
            }

            // Update tracking data
            handle.setPrevX(entityHandle.getLocX());
            handle.setPrevY(entityHandle.getLocY());
            handle.setPrevZ(entityHandle.getLocZ());
        }

        handle.setSynched(true);
        scanPlayers(list);
    }
}
