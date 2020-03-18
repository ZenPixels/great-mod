package greatmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = GreatMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GreatModConfig {
    public static double monsterHealth;
	public static double monsterArmor;
	public static double spawnChance;
	public static int spawnFrequency;
}