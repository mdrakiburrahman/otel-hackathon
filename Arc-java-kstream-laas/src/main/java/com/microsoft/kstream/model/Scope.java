
package com.microsoft.kstream.model;

import java.io.Serializable;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Scope implements Serializable
{

    private final static long serialVersionUID = -4262583398263983553L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Scope.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
