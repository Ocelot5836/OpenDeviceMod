package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@Deprecated
@Task.Register(OpenDevices.MOD_ID + ":open_application")
public class OpenApplicationTask extends Task
{
    private BlockPos pos;
    private CompoundNBT windowData;
    private CompoundNBT contentData;

    public OpenApplicationTask()
    {
    }

    public OpenApplicationTask(BlockPos pos, CompoundNBT windowData, @Nullable CompoundNBT contentData)
    {
        this.pos = pos;
        this.windowData = windowData;
        this.contentData = contentData;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putLong("pos", this.pos.toLong());
        nbt.put("windowData", this.windowData);
        if (this.contentData != null)
            nbt.put("contentData", this.contentData);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
//        this.pos = BlockPos.fromLong(nbt.getLong("pos"));
//        this.windowData = nbt.getCompound("windowData");
//        this.contentData = nbt.contains("contentData", Constants.NBT.TAG_COMPOUND) ? nbt.getCompound("contentData") : null;
//
//        if (world.getTileEntity(this.pos) instanceof LaptopTileEntity)
//        {
//            LaptopTileEntity laptop = (LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos));
//            LaptopDesktop desktop = laptop.getDesktop();
//
//            LaptopWindowOld window = desktop.createWindow(this.windowData);
//            if (window.getContentType() != WindowContentType.APPLICATION)
//            {
//                OpenDevices.LOGGER.error("Attempted to open application with non-application type window!");
//                return;
//            }
//
//            if (!window.getContentType().isValid(window.getContentId()))
//            {
//                OpenDevices.LOGGER.warn("Attempted to open invalid application: '" + window.getContentId() + "'! Applications MUST be registered on both the client AND server to function!");
//                return;
//            }
//
//            desktop.syncOpenApplication(window, this.contentData);
//            this.setSuccessful();
//        }
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
