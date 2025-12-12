package mindpalaces.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnFinder {

    public static BlockPos findMPSpawn(World world, BlockPos mindPalacePos, int size){
        // First check center position
        int center = size >> 1; //divide by 2, intended truncation for odd numbers. returns ++ corner of center 2x2 if size is even
        BlockPos centerPos = mindPalacePos.add(center, 0, center);

        for(int dy = 0; dy <= size - 2; dy ++) { //check for all valid heights (player is 2 blocks high)
            for (int currSize = 1; currSize <= size; currSize++) { //go through the possible sizes in shells of L shape
                int sign = (currSize % 2 == 0) ? -1 : 1; //even: -1, odd: +1

                // create corner first but don't check it yet
                int corner = sign * currSize / 2;
                BlockPos cornerPos = centerPos.add(corner, dy, corner);

                // check from each end of the L towards the corner
                if(currSize > 1) //special case is currSize = 1 where we only check one block, the corner
                    for (int shift = currSize - 1; shift >= 0; shift--) {
                        BlockPos checkPosX = cornerPos.add(-sign * shift, 0, 0);
                        if (isValidSpawnPos(world, checkPosX)) return checkPosX;

                        BlockPos checkPosZ = cornerPos.add(0, 0, -sign * shift);
                        if (isValidSpawnPos(world, checkPosZ)) return checkPosZ;
                    }

                // last check the corner of the L shape
                if (isValidSpawnPos(world, cornerPos)) return cornerPos;
            }
        }

        //if everything fails, just spawn at the center inside a block
        return new BlockPos(centerPos);
    }

    public static BlockPos findOriginalSpawn(EntityPlayer player, World world, BlockPos start, int dimension){
        // If we already have a valid respawn pos, don't worry about it
        if(start != null) {
            if (isValidSpawnPos(world, start)) return start;

            for (int i = 1; i <= 2; i++) { //5x5x5 around player
                for (int dy = -i; dy <= i; dy++) {
                    for (int dx = -i; dx <= i; dx++) {
                        for (int dz = -i; dz <= i; dz++) {
                            //Never put 4 for loops into each other, except if you're too lazy to do it correctly
                            if (Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz)) != i)
                                continue; //only shell

                            BlockPos candidate = start.add(dx, dy, dz);
                            if (isValidSpawnPos(world, candidate)) return candidate;
                        }
                    }
                }
            }

            //Or where original position was saved
            if(start.getY() >= 2) return start;
        }

        //if everything fails, spawn at bed spawn location
        BlockPos bedLoc = player.getBedLocation(dimension);
        if(bedLoc != null) {
            BlockPos bedSpawn = EntityPlayer.getBedSpawnLocation(world, bedLoc, true);
            if (bedSpawn != null) return bedSpawn;
        }

        //Or at worldspawn
        return world.getSpawnPoint();
    }

    //Copied from fonnymonkey's BedBreakBegone
    public static boolean isValidSpawnPos(World worldIn, BlockPos blockPos) {
        return worldIn.getBlockState(blockPos.down()).getMaterial().isSolid() &&
                worldIn.getBlockState(blockPos).getBlock().canSpawnInBlock() &&
                worldIn.getBlockState(blockPos.up()).getBlock().canSpawnInBlock();
    }
}
