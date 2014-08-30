package com.bergerkiller.bukkit.common.bases;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerChunkMapRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerChunkRef;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.PlayerChunkMap;
import net.minecraft.server.WorldServer;

public class PlayerChunkMapBase extends PlayerChunkMap {
	private final SafeConstructor<?> playerChunkComparatorConst;
	
	public PlayerChunkMapBase(World world, int viewDistace) {
		super((WorldServer) Conversion.toWorldHandle.convert(world), viewDistace);
		NMSClassTemplate chunkCoordComparatorClassTemplate = new NMSClassTemplate("PlayerChunkMap$ChunkCoordComparator");
		this.playerChunkComparatorConst = chunkCoordComparatorClassTemplate.getConstructor(EntityPlayer.class);
	}

	/**
	 * @deprecated use {@link #getWorld()} instead
	 */
	@Deprecated
	@Override
	public final WorldServer a() {
		return CommonNMS.getNative(this.getWorld());
	}

	/**
	 * @deprecated use {@link #containsPlayer(Player, int, int) containsPlayer(player, x, z)} instead
	 */
	@Deprecated
	@Override
	public final boolean a(EntityPlayer entityplayer, int x, int z) {
		return this.containsPlayer(CommonNMS.getPlayer(entityplayer), x, z);
	}

	/**
	 * @deprecated use {@link #addChunksToSend(Player)} instead
	 */
	@Deprecated
	@Override
	public final void b(EntityPlayer entityplayer) {
		this.addChunksToSend(CommonNMS.getPlayer(entityplayer));
	}

	/**
	 * @deprecated use {@link #addPlayer(Player)} instead
	 */
	@Deprecated
	@Override
	public void addPlayer(EntityPlayer arg0) {
		this.addPlayer(CommonNMS.getPlayer(arg0));
	}

	/**
	 * @deprecated use {@link #movePlayer(Player)} instead
	 */
	@Deprecated
	@Override
	public void movePlayer(EntityPlayer arg0) {
		this.movePlayer(CommonNMS.getPlayer(arg0));
	}

	/**
	 * @deprecated use {@link #removePlayer(Player)} instead
	 */
	@Deprecated
	@Override
	public void removePlayer(EntityPlayer arg0) {
		removePlayer(CommonNMS.getPlayer(arg0));
	}
	
	public Object getPlayerChunk(Object playerChunk) {
		return playerChunk;
	}

	/**
	 * Updates player movement
	 * 
	 * @param player to update
	 */
	public void movePlayer(Player player) {
		EntityPlayer entityplayer = CommonNMS.getNative(player);
		int i = (int) entityplayer.locX >> 4;
		int j = (int) entityplayer.locZ >> 4;
		double d0 = entityplayer.d - entityplayer.locX;
		double d1 = entityplayer.e - entityplayer.locZ;
		double d2 = d0 * d0 + d1 * d1;

		if (d2 >= 64.0D) {
			int k = (int) entityplayer.d >> 4;
			int l = (int) entityplayer.e >> 4;
			int i1 = PlayerChunkMapRef.radius.get(this);
			int j1 = i - k;
			int k1 = j - l;
			List<ChunkCoordIntPair> chunksToLoad = new LinkedList<ChunkCoordIntPair>();

			if ((j1 != 0) || (k1 != 0)) {
				for (int l1 = i - i1; l1 <= i + i1; l1++) {
					for (int i2 = j - i1; i2 <= j + i1; i2++) {
						if(!PlayerChunkMapRef.shouldUnload.invoke(this, l1, i2, k, l, i1)) {
							chunksToLoad.add(new ChunkCoordIntPair(l1, i2));
						}

						if(!PlayerChunkMapRef.shouldUnload.invoke(this, l1 - j1, i2 - k1, i, j, i1)) {
							Object playerchunk = getPlayerChunk(PlayerChunkMapRef.getChunk.invoke(this, l1 - j1, i2 - k1, false));

							if (playerchunk != null) {
								PlayerChunkRef.unload.invoke(playerchunk, entityplayer);
							}
						}
					}
				}

				b(entityplayer);
				entityplayer.d = entityplayer.locX;
				entityplayer.e = entityplayer.locZ;

				Comparator<? super ChunkCoordIntPair> chunkCoordComparator = (Comparator<? super ChunkCoordIntPair>) playerChunkComparatorConst.newInstance(entityplayer);
				Collections.sort(chunksToLoad, chunkCoordComparator);
				for (ChunkCoordIntPair pair : chunksToLoad) {
					Object playerchunk = PlayerChunkMapRef.getChunk.invoke(pair.x, pair.z, true);
					PlayerChunkRef.load.invoke(playerchunk, entityplayer);
				}

				chunkCoordComparator = (Comparator<? super ChunkCoordIntPair>) playerChunkComparatorConst.newInstance(entityplayer);
				if ((j1 > 1) || (j1 < -1) || (k1 > 1) || (k1 < -1))
					Collections.sort(entityplayer.chunkCoordIntPairQueue, chunkCoordComparator);
			}
		}
	}

	/**
	 * Adds a new player
	 * 
	 * @param player to add
	 */
	public void addPlayer(Player player) {
		super.addPlayer(CommonNMS.getNative(player));
	}

	/**
	 * Removes an existing player
	 * 
	 * @param player to remove
	 */
	public void removePlayer(Player player) {
		super.removePlayer(CommonNMS.getNative(player));
	}

	/**
	 * Adds all chunks near a player to the chunk sending queue of a player
	 * 
	 * @param player to add the chunks to send to
	 */
	public void addChunksToSend(Player player) {
		super.b(CommonNMS.getNative(player));
	}

	/**
	 * Gets whether a player is registered for a Chunk.
	 * If this is the case, the player is liable for entity or block
	 * updates from entities or blocks in the chunk.
	 * 
	 * @param player to check
	 * @param chunkX of the Chunk
	 * @param chunkZ of the Chunk
	 * @return True if the player is contained, False if not
	 */
	public boolean containsPlayer(Player player, int chunkX, int chunkZ) {
		return super.a(CommonNMS.getNative(player), chunkX, chunkZ);
	}

	/**
	 * Gets the world from this PlayerManager<br>
	 * Is called by the PlayerChunkInstance initializer as well
	 * 
	 * @return World
	 */
	public World getWorld() {
		return Conversion.toWorld.convert(super.a());
	}
}
