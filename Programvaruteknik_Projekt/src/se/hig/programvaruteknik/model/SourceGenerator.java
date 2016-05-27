/**
 * 
 */
package se.hig.programvaruteknik.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a function as a source generator<br>
 * <br>
 * This allows {@link DataSourceGenerator} to automatically keep track of the
 * source generators<br>
 * <br>
 * To be used with {@link Param}
 * 
 * @see DataSourceGenerator
 * @see Param
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SourceGenerator
{
    /**
     * Get the name of the source
     * 
     * @return the name
     */
    public String value();
}
