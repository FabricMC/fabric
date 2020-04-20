package net.fabricmc.fabric.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * An informative annotation type used to indicate that a member
 * declaration is intended to be an <i>entrypoint</i> as
 * defined by the loader specification.
 *
 * The loader will treat any member meeting the definition of an
 * entrypoint as an entrypoint regardless of whether or not an
 * {@code Entrypoint} annotation is present on the member declaration.
 *
 * @since 0.1.3
 */
@Documented
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface Entrypoint {
}
