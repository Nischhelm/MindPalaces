package mindpalaces;

import mindpalaces.compat.ReccomplexCompat;
import mindpalaces.handler.ConfigHandler;
import mindpalaces.world.MPBiomeHandler;
import mindpalaces.world.MPWorldProvider;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
		modid = MindPalaces.MODID,
		name = MindPalaces.MODNAME,
		version = MindPalaces.MODVERSION,
		dependencies = "required-after:fermiumbooter@[1.3.0,)"
)
public class MindPalaces {

	public static final String MODID = "mindpalaces";
	public static final String MODNAME = "Mindpalaces";
	public static final String MODVERSION = "1.0.2";
	public static final Logger LOGGER = LogManager.getLogger(MindPalaces.MODID);

	public static final int DIMENSION_ID = 44;
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		DimensionType.register("MindPalace Dimension", "_mindpalace", DIMENSION_ID, MPWorldProvider.class, false);
		DimensionManager.registerDimension(DIMENSION_ID, DimensionType.getById(DIMENSION_ID));
		
		MPBiomeHandler.init();

		if(ConfigHandler.disableReccomplex && Loader.isModLoaded("reccomplex")) MinecraftForge.EVENT_BUS.register(ReccomplexCompat.class);
	}

	public static World getWorld(int dimension){
		return FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getWorld(dimension);
	}

	public static World getOverworld(){
		return getWorld(0);
	}

	public static World getMPWorld(){
		return getWorld(DIMENSION_ID);
	}
}