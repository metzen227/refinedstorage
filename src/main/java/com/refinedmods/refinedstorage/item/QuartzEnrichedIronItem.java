package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import net.minecraft.item.Item;

public class QuartzEnrichedIronItem extends Item {
    public QuartzEnrichedIronItem() {
        super(new Item.Properties().group(RS.MAIN_GROUP));

        this.setRegistryName(RS.ID, "quartz_enriched_iron");
    }
}
