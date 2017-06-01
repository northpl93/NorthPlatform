package pl.north93.zgame.api.global.component.impl;

class JarBeanContext extends AbstractBeanContext
{
    private final JarComponentLoader loader;

    public JarBeanContext(final RootBeanContext root, final JarComponentLoader loader)
    {
        super(root, "jar-" + loader.getFileUrl().getPath());
        this.loader = loader;
    }

}
