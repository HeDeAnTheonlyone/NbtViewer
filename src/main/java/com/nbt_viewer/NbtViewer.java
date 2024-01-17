package com.nbt_viewer;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NbtViewer implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("nbt_viewer");

	@Override
	public void onInitialize() {
		
		LOGGER.info("Hello Fabric world!");
	}
}