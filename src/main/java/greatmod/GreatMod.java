package greatmod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(GreatMod.MOD_ID)
public class GreatMod {
    public static final String MOD_ID = "greatmod";
    public static Logger log = LogManager.getLogger();

    public GreatMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}