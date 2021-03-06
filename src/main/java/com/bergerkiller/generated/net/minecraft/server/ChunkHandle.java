package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.HeightMap;
import com.bergerkiller.bukkit.common.wrappers.HeightMap.Type;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import java.util.List;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.Chunk</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class ChunkHandle extends Template.Handle {
    /** @See {@link ChunkClass} */
    public static final ChunkClass T = new ChunkClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ChunkHandle.class, "net.minecraft.server.Chunk");

    /* ============================================================================== */

    public static ChunkHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract BlockData getBlockData(IntVector3 blockposition);
    public abstract BlockData getBlockDataAtCoord(int x, int y, int z);
    public abstract BlockData setBlockData(IntVector3 blockposition, BlockData iblockdata, int updateFlags);
    public abstract void addEntity(EntityHandle entity);
    public abstract HeightMap getHeightMap(Type type);
    public abstract int getHeight(Type type, int x, int z);
    public abstract int getBrightness(EnumSkyBlockHandle enumskyblock, IntVector3 position);
    public abstract int getTopSliceY();
    public abstract void addEntities();
    public abstract boolean checkCanSave(boolean isNotAutosave);
    public abstract void markDirty();

    public static ChunkHandle fromBukkit(org.bukkit.Chunk chunk) {
        if (chunk != null) {
            return createHandle(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toChunkHandle(chunk));
        } else {
            return null;
        }
    }
    public abstract ChunkSectionHandle[] getSections();
    public abstract void setSections(ChunkSectionHandle[] value);
    public abstract WorldHandle getWorld();
    public abstract void setWorld(WorldHandle value);
    public abstract int getLocX();
    public abstract void setLocX(int value);
    public abstract int getLocZ();
    public abstract void setLocZ(int value);
    public abstract Map<IntVector3, BlockState> getTileEntities();
    public abstract void setTileEntities(Map<IntVector3, BlockState> value);
    public abstract List<Object>[] getEntitySlices();
    public abstract void setEntitySlices(List<Object>[] value);
    public abstract Chunk getBukkitChunk();
    public abstract void setBukkitChunk(Chunk value);
    /**
     * Stores class members for <b>net.minecraft.server.Chunk</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkClass extends Template.Class<ChunkHandle> {
        public final Template.Field.Converted<ChunkSectionHandle[]> sections = new Template.Field.Converted<ChunkSectionHandle[]>();
        public final Template.Field.Converted<WorldHandle> world = new Template.Field.Converted<WorldHandle>();
        public final Template.Field.Integer locX = new Template.Field.Integer();
        public final Template.Field.Integer locZ = new Template.Field.Integer();
        public final Template.Field.Converted<Map<IntVector3, BlockState>> tileEntities = new Template.Field.Converted<Map<IntVector3, BlockState>>();
        public final Template.Field.Converted<List<Object>[]> entitySlices = new Template.Field.Converted<List<Object>[]>();
        public final Template.Field<Chunk> bukkitChunk = new Template.Field<Chunk>();

        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<BlockData> getBlockDataAtCoord = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<BlockData> setBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<Void> addEntity = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<HeightMap> getHeightMap = new Template.Method.Converted<HeightMap>();
        public final Template.Method.Converted<Integer> getHeight = new Template.Method.Converted<Integer>();
        public final Template.Method.Converted<Integer> getBrightness = new Template.Method.Converted<Integer>();
        public final Template.Method<Integer> getTopSliceY = new Template.Method<Integer>();
        public final Template.Method<Void> addEntities = new Template.Method<Void>();
        public final Template.Method<Boolean> checkCanSave = new Template.Method<Boolean>();
        public final Template.Method<Void> markDirty = new Template.Method<Void>();

    }

}

