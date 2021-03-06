/*
 * Copyright (C) 2017 Chikachi
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package chikachi.idtga.tt;

import chikachi.idtga.tt.config.Configuration;
import chikachi.idtga.tt.handler.PlayerEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

import java.io.File;

@SuppressWarnings({"WeakerAccess"})
@Mod(modid = Constants.MODID, name = Constants.MODNAME, version = Constants.VERSION, acceptableRemoteVersions = "*")
public class ItsDangerousToGoAloneTakeThis {
    @SuppressWarnings("unused")
    @Mod.Instance(value = Constants.MODID)
    public static ItsDangerousToGoAloneTakeThis instance;
    private static PlayerEventHandler eventHandler = new PlayerEventHandler();

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        Configuration.onPreInit(event.getModConfigurationDirectory().getAbsolutePath() + File.separator + "Chikachi");
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        Configuration.load();
        MinecraftForge.EVENT_BUS.register(eventHandler);
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        MinecraftForge.EVENT_BUS.unregister(eventHandler);
    }
}
