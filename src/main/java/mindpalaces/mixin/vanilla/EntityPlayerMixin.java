package mindpalaces.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mindpalaces.handler.MPTeleporter;
import mindpalaces.util.FromMPTeleporterThreadLocal;
import mindpalaces.util.IEntityPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin implements IEntityPlayer {
    @Shadow private int sleepTimer;
    @Shadow public abstract String getName();

    @Unique private boolean mp$playerShouldTP = false;
    @Unique private int mp$targetDimension = 0;

    @Override
    public void mp$setDimensionToTPTo(int targetDimension){
        this.mp$playerShouldTP = true;
        this.mp$targetDimension = targetDimension;
    }

    @WrapOperation(
            method = "onUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;wakeUpPlayer(ZZZ)V", ordinal =  1)
    )
    private void mindpalaces_teleportAfterWake(EntityPlayer player, boolean immediately, boolean updateWorldFlag, boolean setSpawn, Operation<Void> original){
        original.call(player, immediately, updateWorldFlag, setSpawn); //default behavior

        if(mp$playerShouldTP) {
            sleepTimer = 0;
            MPTeleporter.TeleportHandler.teleportToDimension(player, mp$targetDimension);
            mp$playerShouldTP = false;
            FromMPTeleporterThreadLocal.set(false);
        }
    }
}
