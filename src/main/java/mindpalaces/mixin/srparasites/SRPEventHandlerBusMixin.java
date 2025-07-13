package mindpalaces.mixin.srparasites;

import com.dhanantry.scapeandrunparasites.util.handlers.SRPEventHandlerBus;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mindpalaces.util.FromMPTeleporterThreadLocal;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SRPEventHandlerBus.class)
public class SRPEventHandlerBusMixin {
    @WrapWithCondition(
            method = "playerUp",
            at = @At(value = "INVOKE", target = "Lcom/dhanantry/scapeandrunparasites/world/SRPSaveData;setTotalKills(IIZLnet/minecraft/world/World;Z)Z"),
            remap = false
    )
    private boolean mindpalaces_dontIncreasePoints(SRPSaveData instance, int id, int in, boolean plus, World worldIn, boolean canChangePhase){
        return !FromMPTeleporterThreadLocal.get(); //dont increase points if from teleporting to/from MP
    }
}
