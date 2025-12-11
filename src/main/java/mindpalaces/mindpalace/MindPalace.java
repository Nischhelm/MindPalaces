package mindpalaces.mindpalace;

import mindpalaces.MindPalaces;
import mindpalaces.handler.ConfigHandler;
import mindpalaces.util.SpawnFinder;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class MindPalace {

	public static Block wallBlock = null;

	private final BlockPos mindPalacePos;
	private final int size;

	private Vec3d spawnPos;
	private BlockPos originPos = null;
	private int originDimension = 0;
	private int tick = 0;
	private long lastTravelWorldTick = 0;

	public MindPalace(BlockPos mindPalacePos, int size) {
		this.mindPalacePos = mindPalacePos;
		this.size = size;
		this.spawnPos = new Vec3d(this.mindPalacePos.add(this.size / 2, 0 , this.size / 2)).add(0.7, 0, 0.7);
	}

	public static MindPalace createFromNBT(NBTTagCompound mp) {
		return new MindPalace(readBlockPosFromNBT(mp.getCompoundTag("mpPos")), mp.getInteger("size"))
				.setOriginalPosition(mp.getInteger("origDim"), readBlockPosFromNBT(mp.getCompoundTag("origPos")))
				.setSpawnPosition(readVec3dFromNBT(mp.getCompoundTag("spawnPos")))
				.setTick(mp);
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound nbt = new NBTTagCompound();

		nbt.setTag("mpPos", writeBlockPosToNBT(this.mindPalacePos));
		nbt.setTag("spawnPos", writeVec3dToNBT(this.spawnPos));
		nbt.setTag("origPos", writeBlockPosToNBT(this.originPos));
		nbt.setInteger("origDim", this.originDimension);
		nbt.setInteger("size", this.size);
		nbt.setInteger("tick", this.tick);
		nbt.setLong("lastTravelWorldTick", this.lastTravelWorldTick);

		return nbt;
	}

	public static NBTTagCompound writeBlockPosToNBT(BlockPos pos){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("x", pos.getX());
		nbt.setInteger("y", pos.getY());
		nbt.setInteger("z", pos.getZ());
		return nbt;
	}

	public static BlockPos readBlockPosFromNBT(NBTTagCompound nbt){
		return new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
	}

	public static NBTTagCompound writeVec3dToNBT(Vec3d pos){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble("x", pos.x);
		nbt.setDouble("y", pos.y);
		nbt.setDouble("z", pos.z);
		return nbt;
	}

	public static Vec3d readVec3dFromNBT(NBTTagCompound nbt){
		return new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
	}

	public MindPalace setOriginalPosition(int dimension, BlockPos position) {
		this.originDimension = dimension;
		this.originPos = position;
		if(!MindPalaceData.isLoading)
			MindPalaceData.get().markDirty();
		return this;
	}

	public int getOriginalDimension(){
		return this.originDimension;
	}

	public BlockPos getOriginalPosition() {
		return originPos;
	}

	public MindPalace setTick(NBTTagCompound mp) {
		if(mp.hasKey("lastTravelWorldTick")) this.lastTravelWorldTick = mp.getLong("lastTravelWorldTick");
		if(mp.hasKey("tick")) this.tick = mp.getInteger("tick"); //current

		return this;
	}

	public void incrementTick(){
		this.tick += 10; //Has to match the amount of skipped ticks in MPEventHandler
	}

	public boolean isReadyToKick(){
		if(this.tick > ConfigHandler.maxStayTicks){
			this.tick = 0;
			return true;
		}
		return false;
	}

	public boolean isReadyToEnter(long currentWorldTick){
        return currentWorldTick > this.lastTravelWorldTick + ConfigHandler.travelDelay;
    }

	public void resetTick(long worldTime) {
		this.tick = 0;
		this.lastTravelWorldTick = worldTime;
	}

	private MindPalace setSpawnPosition(Vec3d spawnPos) {
		this.spawnPos = spawnPos;
		return this;
	}

	public void generateMindPalace() {
		World world = MindPalaces.getMPWorld();

		if(wallBlock == null){
			wallBlock = Block.getBlockFromName(ConfigHandler.wallBlock);
			if(wallBlock == null) wallBlock = Blocks.BEDROCK;
		}

		for(int side = 0; side <= 1; side++) { //each axis has two faces
			int shift = 2 * side - 1; //shifting the walls one out (+1/-1)
			//diagonal corners of each face (-- and ++)
			int mainAxisValue = side * (size - 1) + shift;
			Vec3i corner1 = new Vec3i(mainAxisValue, 0, 0);
			Vec3i corner2 = new Vec3i(mainAxisValue, size - 1, size - 1);
			for (WallAxis axis : WallAxis.values())
				for (BlockPos.MutableBlockPos blockPosMutable : BlockPos.getAllInBoxMutable(mindPalacePos.add(axis.cycle(corner1)), mindPalacePos.add(axis.cycle(corner2))))
					if(world.getBlockState(blockPosMutable).getBlock() != wallBlock)
						world.setBlockState(blockPosMutable, wallBlock.getDefaultState());
		}
	}

	public boolean positionIsInMindPalace(BlockPos pos){
		boolean xCorrect = pos.getX() >= mindPalacePos.getX() && pos.getX() <= mindPalacePos.getX() + size;
		if(!xCorrect) return false;
		boolean yCorrect = pos.getY() >= mindPalacePos.getY() && pos.getY() <= mindPalacePos.getY() + size;
		if(!yCorrect) return false;
		return pos.getZ() >= mindPalacePos.getZ() && pos.getZ() <= mindPalacePos.getZ() + size; //adjusted for head
	}

	public Vec3d getSpawnPos() {
		World world = MindPalaces.getMPWorld();

		// If we already have a valid spawn pos, don't worry about it
		if(SpawnFinder.isValidSpawnPos(world, new BlockPos(spawnPos))) return spawnPos;

		spawnPos = new Vec3d(SpawnFinder.findMPSpawn(world, mindPalacePos, size)).add(0.7, 0.0, 0.7);
		return spawnPos;
	}

	public int getTicks() {
		return tick;
	}

	public enum WallAxis{
		X { @Override public Vec3i cycle(Vec3i in) {return in;} },  //XYZ
		Y { @Override public Vec3i cycle(Vec3i in) {return new Vec3i(in.getZ(), in.getX(), in.getY());} }, //ZXY
		Z { @Override public Vec3i cycle(Vec3i in) {return new Vec3i(in.getY(), in.getZ(), in.getX());} }; //YZX
		public abstract Vec3i cycle(Vec3i in);
	}
}