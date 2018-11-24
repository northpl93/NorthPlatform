package pl.north93.northplatform.api.bukkit.map.loader.xml;

import static pl.north93.northplatform.api.global.utils.lang.CollectionUtils.findInCollection;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlAccessorType(XmlAccessType.FIELD)
public final class XmlTranslatableImage
{
    @XmlElement(name = "entry")
    private List<ImageEntry> images;

    public File getFileByLanguage(final Locale locale)
    {
        final String languageTag = locale.toLanguageTag();

        final ImageEntry imageEntry = findInCollection(this.images, ImageEntry::getLanguage, languageTag);
        if (imageEntry == null)
        {
            final ImageEntry defaultEntry = this.images.get(0);
            return new File(defaultEntry.file);
        }

        return new File(imageEntry.file);
    }

    public List<ImageEntry> getImages()
    {
        return this.images;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("images", this.images).toString();
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class ImageEntry
    {
        @XmlAttribute(required = true)
        private String language;
        @XmlAttribute(required = true)
        private String file;

        public String getLanguage()
        {
            return this.language;
        }

        public String getFile()
        {
            return this.file;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("language", this.language).append("file", this.file).toString();
        }
    }
}
