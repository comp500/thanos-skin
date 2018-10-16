package link.infra.thanosskin;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ThanosSkin.MODID, name = ThanosSkin.MODNAME, version = ThanosSkin.VERSION, useMetadata = true)
public class ThanosSkin {

	public static final String MODID = "thanosskin";
	public static final String MODNAME = "thanos skin";
	public static final String VERSION = "1.12.2-1.0.0";

	@Mod.Instance
	public static ThanosSkin instance;
	public static Logger logger;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e) {
	}
}
