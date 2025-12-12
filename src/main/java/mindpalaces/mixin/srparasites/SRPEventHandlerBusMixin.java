package mindpalaces.mixin.srparasites;

import com.dhanantry.scapeandrunparasites.util.handlers.SRPEventHandlerBus;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mindpalaces.util.FromMPTeleporterThreadLocal;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SRPEventHandlerBus.class)
public class SRPEventHandlerBusMixin {
    @WrapOperation(
            method = "playerUp",
            at = @At(value = "INVOKE", target = "Lcom/dhanantry/scapeandrunparasites/world/SRPSaveData;setTotalKills(IIZLnet/minecraft/world/World;Z)Z"),
            remap = false
    )
    private boolean mindpalaces_dontIncreasePoints(SRPSaveData instance, int id, int in, boolean plus, World worldIn, boolean canChangePhase, Operation<Boolean> original){
        if(!FromMPTeleporterThreadLocal.get())
            return original.call(instance, id, in, plus, worldIn, canChangePhase);
        return false; //dont increase points if from teleporting to/from MP
    }
}
