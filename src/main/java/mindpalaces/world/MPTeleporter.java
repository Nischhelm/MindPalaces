package mindpalaces.world;

import mindpalaces.MindPalaces;
import mindpalaces.mindpalace.MindPalace;
import mindpalaces.mindpalace.MindPalaceData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

public class MPTeleporter implements ITeleporter {
    public static final MPTeleporter INSTANCE = new MPTeleporter();

    private int currentDim = 0;
    public MPTeleporter setFrom(int dim){
        this.currentDim = dim;
        return this;
    }

    @Override
    public void placeEntity(World world, Entity entity, float yaw) {
        if(!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;

        if(this.currentDim != MindPalaces.DIMENSION_ID) {
            //TO MP
            MindPalaceData data = MindPalaceData.get();
            MindPalace mindPalace = data.prepareToTravel(player, this.currentDim);
            Vec3d mpPos = mindPalace.getSpawnPos();
            player.setLocationAndAngles(mpPos.x, mpPos.y, mpPos.z, yaw, player.rotationPitch);
            mindPalace.setLastTravelTick(MindPalaces.getWorld(0).getWorldTime());
        } else {
            //FROM MP
            MindPalace mindPalace = MindPalaceData.get().getForPlayer(player);
            BlockPos origPos = mindPalace.getOriginalPosition();
            //TODO: check if theres a good spot in origin dimension as well, might have been griefed
            player.setLocationAndAngles(origPos.getX() + 0.5, origPos.getY(), origPos.getZ() + 0.5, yaw, player.rotationPitch);
        }
    }
}

