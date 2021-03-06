package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.Block</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class BlockHandle extends Template.Handle {
    /** @See {@link BlockClass} */
    public static final BlockClass T = new BlockClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BlockHandle.class, "net.minecraft.server.Block");

    @SuppressWarnings("rawtypes")
    public static final Iterable REGISTRY_ID = T.REGISTRY_ID.getSafe();
    /* ============================================================================== */

    public static BlockHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Iterable<?> getRegistry() {
        return T.getRegistry.invoke();
    }

    public static IBlockDataHandle getByCombinedId(int combinedId) {
        return T.getByCombinedId.invoke(combinedId);
    }

    public static int getCombinedId(IBlockDataHandle iblockdata) {
        return T.getCombinedId.invoke(iblockdata);
    }

    public abstract void entityHitVertical(WorldHandle world, EntityHandle entity);
    public abstract int getOpacity(IBlockDataHandle iblockdata, World world, int x, int y, int z);
    public abstract int getEmission(IBlockDataHandle iblockdata);
    public abstract boolean isOccluding(IBlockDataHandle iblockdata);
    public abstract boolean isPowerSource(IBlockDataHandle iblockdata);
    public abstract boolean canSupportTop(IBlockDataHandle iblockdata);
    public abstract float getDamageResillience();
    public abstract void dropNaturally(IBlockDataHandle iblockdata, World world, IntVector3 blockposition, float yield, int chance);
    public abstract void ignite(World world, IntVector3 blockposition, ExplosionHandle explosion);
    public abstract void stepOn(World world, IntVector3 blockposition, Entity entity);
    public abstract IBlockDataHandle updateState(IBlockDataHandle iblockdata, World world, IntVector3 blockposition);
    public abstract AxisAlignedBBHandle getBoundingBox(IBlockDataHandle iblockdata, IBlockAccessHandle iblockaccess, IntVector3 blockposition);
    public abstract IBlockDataHandle getBlockData();

    public SoundEffectTypeHandle getSoundType() {
        if (T.getSoundType.isAvailable()) {
            return T.getSoundType.invoke(getRaw());
        } else {
            return T.opt_1_8_8_soundType.get(getRaw());
        }
    }
    /**
     * Stores class members for <b>net.minecraft.server.Block</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockClass extends Template.Class<BlockHandle> {
        @SuppressWarnings("rawtypes")
        public final Template.StaticField.Converted<Iterable> REGISTRY_ID = new Template.StaticField.Converted<Iterable>();

        @Template.Optional
        public final Template.Field.Converted<SoundEffectTypeHandle> opt_1_8_8_soundType = new Template.Field.Converted<SoundEffectTypeHandle>();

        public final Template.StaticMethod<Iterable<?>> getRegistry = new Template.StaticMethod<Iterable<?>>();
        public final Template.StaticMethod.Converted<IBlockDataHandle> getByCombinedId = new Template.StaticMethod.Converted<IBlockDataHandle>();
        public final Template.StaticMethod.Converted<Integer> getCombinedId = new Template.StaticMethod.Converted<Integer>();

        public final Template.Method.Converted<Void> entityHitVertical = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Integer> getOpacity = new Template.Method.Converted<Integer>();
        public final Template.Method.Converted<Integer> getEmission = new Template.Method.Converted<Integer>();
        public final Template.Method.Converted<Boolean> isOccluding = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Boolean> isPowerSource = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Boolean> canSupportTop = new Template.Method.Converted<Boolean>();
        public final Template.Method<Float> getDamageResillience = new Template.Method<Float>();
        @Template.Optional
        public final Template.Method.Converted<SoundEffectTypeHandle> getSoundType = new Template.Method.Converted<SoundEffectTypeHandle>();
        public final Template.Method.Converted<Void> dropNaturally = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> ignite = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> stepOn = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<IBlockDataHandle> updateState = new Template.Method.Converted<IBlockDataHandle>();
        public final Template.Method.Converted<AxisAlignedBBHandle> getBoundingBox = new Template.Method.Converted<AxisAlignedBBHandle>();
        public final Template.Method.Converted<IBlockDataHandle> getBlockData = new Template.Method.Converted<IBlockDataHandle>();

    }

}

