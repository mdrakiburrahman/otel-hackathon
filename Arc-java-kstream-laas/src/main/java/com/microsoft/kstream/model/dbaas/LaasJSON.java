
package com.microsoft.kstream.model.dbaas;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class LaasJSON {

    @SerializedName("raw")
    @Expose
    private Raw raw;
    @SerializedName("std")
    @Expose
    private Std std;
    @SerializedName("laas")
    @Expose
    private Laas laas;
    @SerializedName("private")
    @Expose
    private Private _private;

    public Raw getRaw() {
        return raw;
    }

    public void setRaw(Raw raw) {
        this.raw = raw;
    }

    public Std getStd() {
        return std;
    }

    public void setStd(Std std) {
        this.std = std;
    }

    public Laas getLaas() {
        return laas;
    }

    public void setLaas(Laas laas) {
        this.laas = laas;
    }

    public Private getPrivate() {
        return _private;
    }

    public void setPrivate(Private _private) {
        this._private = _private;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LaasJSON.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("raw");
        sb.append('=');
        sb.append(((this.raw == null)?"<null>":this.raw));
        sb.append(',');
        sb.append("std");
        sb.append('=');
        sb.append(((this.std == null)?"<null>":this.std));
        sb.append(',');
        sb.append("laas");
        sb.append('=');
        sb.append(((this.laas == null)?"<null>":this.laas));
        sb.append(',');
        sb.append("_private");
        sb.append('=');
        sb.append(((this._private == null)?"<null>":this._private));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
