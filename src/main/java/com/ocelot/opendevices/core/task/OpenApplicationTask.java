package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.LaptopDesktop;
import com.ocelot.opendevices.core.LaptopTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Objects;

@Task.Register(OpenDevices.MOD_ID + ":open_application")
public class OpenApplicationTask extends Task
{
    private BlockPos pos;
    private ResourceLocation registryName;
    private CompoundNBT layoutData;
    private CompoundNBT initData;

    public OpenApplicationTask()
    {
        this(null, null, null, null);
    }

    public OpenApplicationTask(BlockPos pos, ResourceLocation registryName, CompoundNBT layoutData, @Nullable CompoundNBT initData)
    {
        this.pos = pos;
        this.registryName = registryName;
        this.layoutData = layoutData;
        this.initData = initData;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putLong("pos", this.pos.toLong());
        nbt.putString("registryName", this.registryName.toString());
        nbt.put("layoutData", this.layoutData);

        if (this.initData != null)
        {
            nbt.put("initData", this.initData);
        }
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.pos = BlockPos.fromLong(nbt.getLong("pos"));
        this.registryName = new ResourceLocation(nbt.getString("registryName"));
        this.layoutData = nbt.getCompound("layoutData");
        this.initData = nbt.contains("initData", Constants.NBT.TAG_COMPOUND) ? nbt.getCompound("initData") : null;

        if (world.getTileEntity(this.pos) instanceof LaptopTileEntity)
        {
            LaptopTileEntity laptop = (LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos));
            LaptopDesktop desktop = laptop.getDesktop();

            desktop.syncOpenWindow(desktop.createWindow(this.registryName, this.initData));
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundNBT nbt)
    {
        if (this.isSucessful())
        {
            this.prepareRequest(nbt);
        }
    }

    @Override
    public void processResponse(CompoundNBT nbt, World world, PlayerEntity player)
    {
        if (this.isSucessful())
        {
            this.processRequest(nbt, world, player);
        }
    }
}
