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

package chikachi.idtga.tt.handler;

import chikachi.idtga.tt.config.Configuration;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.List;

public class PlayerEventHandler {
    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent()
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player.getServer().isDedicatedServer() && !event.player.isServerWorld()) {
            return;
        }

        boolean firstTime = false;

        try {
            NBTTagCompound nbtTagCompound = event.player.getServer().getPlayerList().getPlayerNBT((EntityPlayerMP) event.player);

            if (nbtTagCompound == null) {
                firstTime = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (firstTime) {
            List<ItemStack> itemStacks = Configuration.getItems();

            if (itemStacks.size() > 0) {
                if (Configuration.showMessage()) {
                    event.player.sendMessage(new TextComponentString("It's dangerous to go alone! Take this!"));
                }

                for (ItemStack itemStack : itemStacks) {
                    if (itemStack != null) {
                        event.player.inventory.addItemStackToInventory(itemStack.copy());
                    }
                }
            }
        }
    }
}
