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
public @interface SourceGenerator
{
    public String value();
}
