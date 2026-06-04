package com.xkball.roads.datacomponents;

import com.xkball.roads.Roads;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Roads.MODID);

    public static final Supplier<DataComponentType<StructuralSelection>> STRUCTURAL_SELECTION = DATA_COMPONENTS.registerComponentType(
            "structural_selection",
            builder -> builder
                    .persistent(StructuralSelection.CODEC)
                    .networkSynchronized(StructuralSelection.STREAM_CODEC));

}
