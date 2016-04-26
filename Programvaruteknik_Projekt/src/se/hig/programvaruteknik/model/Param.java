/**
 * 
 */
package se.hig.programvaruteknik.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Param
{
    /**
     * The value of the option
     * 
     * @return The value
     */
    public String value();

    /**
     * The name of the option
     * 
     * @return The name
     */
    public String name() default "";

    /**
     * Optional values to offer the client
     * 
     * @return The suggested enum
     */
    public Class<?> suggestEnum() default NULL.class;

    /**
     * Only use the suggested enum
     * 
     * @return If only the suggested values can be used
     */
    public boolean onlyEnum() default false;

    /**
     * NULL object
     */
    public class NULL
    {

    }
}
