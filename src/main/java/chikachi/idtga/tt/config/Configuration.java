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

package chikachi.idtga.tt.config;

import chikachi.idtga.tt.Constants;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private static File config;

    private static boolean showMessage = true;
    private static List<ItemStack> items = new ArrayList<>();

    public static void onPreInit(String directoryPath) {
        File directory = new File(directoryPath);

        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        config = new File(directory, Constants.MODID + ".json");
    }

    private static NBTTagCompound readNBTTagCompound(JsonReader reader) throws IOException {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            JsonToken type = reader.peek();
            switch (type) {
                case STRING:
                    nbtTagCompound.setString(key, reader.nextString());
                    break;
                case NUMBER:
                    String value = reader.nextString();
                    try {
                        Double doubleValue = Double.parseDouble(value);
                        if (doubleValue % 1 == 0) {
                            nbtTagCompound.setInteger(key, Integer.valueOf(value));
                        } else {
                            nbtTagCompound.setDouble(key, doubleValue);
                        }
                    } catch (Exception ignored) {
                        nbtTagCompound.setInteger(key, Integer.valueOf(value));
                    }
                    break;
                case BOOLEAN:
                    nbtTagCompound.setBoolean(key, reader.nextBoolean());
                    break;
                case BEGIN_OBJECT:
                    NBTTagCompound newNBTTagCompound = readNBTTagCompound(reader);
                    nbtTagCompound.setTag(key, newNBTTagCompound);
                    nbtTagCompound = newNBTTagCompound;
                    break;
            }
        }
        reader.endObject();

        return nbtTagCompound;
    }

    public static void load() {
        if (config == null) {
            return;
        }

        if (!config.exists()) {
            save();
        } else {
            try {
                JsonReader reader = new JsonReader(new FileReader(config));
                String name;

                reader.beginObject();
                while (reader.hasNext()) {
                    name = reader.nextName();
                    if (name.equalsIgnoreCase("message") && reader.peek() == JsonToken.BOOLEAN) {
                        showMessage = reader.nextBoolean();
                    } else if (name.equalsIgnoreCase("items") && reader.peek() == JsonToken.BEGIN_ARRAY) {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            if (reader.peek() == JsonToken.BEGIN_OBJECT) {
                                ItemStack itemStack = null;

                                String itemId = null;
                                int itemMeta = 0;
                                int itemAmount = 1;
                                NBTTagCompound nbtTagCompound = null;

                                reader.beginObject();
                                while (reader.hasNext()) {
                                    name = reader.nextName();
                                    if (name.equalsIgnoreCase("id")) {
                                        if (reader.peek() == JsonToken.STRING) {
                                            itemId = reader.nextString();
                                        } else {
                                            reader.skipValue();
                                        }
                                    } else if (name.equalsIgnoreCase("meta")) {
                                        if (reader.peek() == JsonToken.NUMBER) {
                                            itemMeta = reader.nextInt();
                                        } else {
                                            reader.skipValue();
                                        }
                                    } else if (name.equalsIgnoreCase("amount")) {
                                        if (reader.peek() == JsonToken.NUMBER) {
                                            itemAmount = reader.nextInt();
                                        } else {
                                            reader.skipValue();
                                        }
                                    } else if (name.equalsIgnoreCase("nbt")) {
                                        if (reader.peek() == JsonToken.BEGIN_OBJECT) {
                                            nbtTagCompound = readNBTTagCompound(reader);
                                        } else {
                                            reader.skipValue();
                                        }
                                    } else {
                                        reader.skipValue();
                                    }
                                }

                                if (itemId != null) {
                                    Item item = Item.getByNameOrId(itemId);
                                    //noinspection ConstantConditions
                                    if (item != null) {
                                        itemStack = new ItemStack(item, itemAmount, itemMeta);
                                    } else {
                                        Block block = Block.getBlockFromName(itemId);
                                        if (block != null) {
                                            itemStack = new ItemStack(block, itemAmount, itemMeta);
                                        }
                                    }
                                }

                                if (itemStack != null) {
                                    if (nbtTagCompound != null) {
                                        itemStack.setTagCompound(nbtTagCompound);
                                    }

                                    items.add(itemStack);
                                }

                                reader.endObject();
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endArray();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void save() {
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(config));
            writer.setIndent("  ");

            writer.beginObject();

            writer.name("message");
            writer.value(true);

            writer.name("items");
            writer.beginArray();
            writer.endArray();

            writer.endObject();

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean showMessage() {
        return showMessage;
    }

    public static List<ItemStack> getItems() {
        return items;
    }
}
