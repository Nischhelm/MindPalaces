package mindpalaces.world;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MPChunkGenerator implements IChunkGenerator {

	private final World world;

	public MPChunkGenerator(World world) {
		this.world = world;
	}

	@Override
	@Nonnull
	public Chunk generateChunk(int x, int z) {
		Chunk chunk = new Chunk(world, x, z);

		byte biomeId = (byte) Biome.getIdForBiome(MPBiomeHandler.mindPalaceBiome);
		Arrays.fill(chunk.getBiomeArray(), biomeId);

		chunk.generateSkylightMap();
		return chunk;
	}

	@Override
	public void populate(int x, int z) {
	}

	@Override
	public boolean generateStructures(@Nonnull Chunk chunkIn, int x, int z) {
		return false;
	}

	@Override
	@Nonnull
	public List<SpawnListEntry> getPossibleCreatures(@Nonnull EnumCreatureType creatureType, @Nonnull BlockPos pos) {
		return Collections.emptyList();
	}

	@Nullable
	@Override
	public BlockPos getNearestStructurePos(@Nonnull World worldIn, @Nonnull String structureName, @Nonnull BlockPos position, boolean findUnexplored) {
		return null;
	}

	@Override
	public void recreateStructures(@Nonnull Chunk chunkIn, int x, int z) {
	}

	@Override
	public boolean isInsideStructure(@Nonnull World worldIn, @Nonnull String structureName, @Nonnull BlockPos pos) {
		return false;
	}
}
