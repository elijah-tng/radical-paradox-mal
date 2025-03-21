package tripleo.elijah.world.impl;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.lang.OS_Package;
import tripleo.elijah.lang.Qualident;
import tripleo.elijah.world.i.LivingPackage;

public class DefaultLivingPackage implements LivingPackage {
    private final OS_Package _element;
    private /*final*/ Qualident packageName;

    public DefaultLivingPackage(final OS_Package aElement) {
        _element = aElement;
    }

    public DefaultLivingPackage(final @NotNull Qualident pkg_name, final OS_Package aPackage) {
        _element    = aPackage;
        packageName = pkg_name;
    }

    @Override
    public OS_Package getElement() {
        return _element;
    }

    @Override
    public int getCode() {
        return 0;
    }
}
