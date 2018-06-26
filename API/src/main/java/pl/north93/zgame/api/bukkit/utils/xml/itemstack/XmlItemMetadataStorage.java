package pl.north93.zgame.api.bukkit.utils.xml.itemstack;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

@XmlAccessorType(XmlAccessType.FIELD)
public class XmlItemMetadataStorage
{
    @XmlAttribute
    private String name;
    
    private List<XmlItemMetadataEntry> metadata = new ArrayList<>();
    
    public String getName()
    {
        return name;
    }
    
    public List<XmlItemMetadataEntry> getMetadata()
    {
        return metadata;
    }
    
    public void apply(net.minecraft.server.v1_12_R1.ItemStack is)
    {
        Preconditions.checkState(name != null);

        NBTTagCompound tag = is.c(name);
        metadata.forEach(metadata -> tag.setString(metadata.getKey(), metadata.getValue()));
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("name", name).append("metadata", metadata).build();
    }
}
