package com.kotori316.dumper.dumps.items;

import net.minecraft.item.ItemStack;

public interface Filter {

    boolean addtoList(ItemStack stack, String shortName, String displayName, String uniqueName);

    void writeToFile();
}
