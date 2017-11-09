package pl.arieals.lobby.maps.xml;

import static pl.north93.zgame.api.global.utils.lang.CollectionUtils.findInCollection;


import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class TranslatableImage
{
    private List<ImageEntry> images;

    public String getFileByLanguage(final Locale locale)
    {
        final String languageTag = locale.toLanguageTag();

        final ImageEntry imageEntry = findInCollection(this.images, ImageEntry::getLanguage, languageTag);
        if (imageEntry == null)
        {
            return "";
        }

        return imageEntry.fileLocation;
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

    public static final class ImageEntry
    {
        private String language;
        private String fileLocation;

        public String getLanguage()
        {
            return this.language;
        }

        public String getFileLocation()
        {
            return this.fileLocation;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("language", this.language).append("fileLocation", this.fileLocation).toString();
        }
    }
}
