package mindpalaces.world;

import mindpalaces.MindPalaces;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class MPBiomeHandler {
	public static class CustomBiome extends Biome {
		public CustomBiome() {
			super(new BiomeProperties("Mind Palace").setRainDisabled().setTemperature(0.8F));
		}
	}

	public static final CustomBiome mindPalaceBiome = new CustomBiome();

	public static void init() {
		BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(mindPalaceBiome, 10));
		BiomeManager.addSpawnBiome(mindPalaceBiome);
		BiomeDictionary.addTypes(mindPalaceBiome, BiomeDictionary.Type.VOID);
	}
	
	@SubscribeEvent
	public static void onRegisterBiomes(RegistryEvent.Register<Biome> event) {
		event.getRegistry().register(mindPalaceBiome.setRegistryName(MindPalaces.MODID, "Mind Palace"));
	}
}