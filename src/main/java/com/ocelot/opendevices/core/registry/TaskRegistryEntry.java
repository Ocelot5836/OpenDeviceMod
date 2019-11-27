package com.ocelot.opendevices.core.registry;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Objects;

public class TaskRegistryEntry extends ForgeRegistryEntry<TaskRegistryEntry>
{
    private Class<? extends Task> clazz;

    public TaskRegistryEntry(Class<? extends Task> clazz)
    {
        this.clazz = clazz;
    }

    @Nullable
    public Task createTask()
    {
        try
        {
            return this.clazz.newInstance();
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not create task: " + this.getRegistryName() + ". Verify there is a public empty constructor.", e);
        }

        return null;
    }

    public Class<? extends Task> getTaskClass()
    {
        return clazz;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof TaskRegistryEntry)) return false;
        TaskRegistryEntry that = (TaskRegistryEntry) o;
        return Objects.equals(this.clazz, that.clazz);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.clazz);
    }

    @Override
    public String toString()
    {
        return "registryName=\'" + this.getRegistryName() + "\', class=\'" + this.clazz.getName() + "\'";
    }
}