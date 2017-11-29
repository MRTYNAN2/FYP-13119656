package com.fyp.rory.fyp.Models;

/**
 * Created by Rory on 29/11/2017.
 * Object for reactions for post;
 */

public class FBReactions {
    private boolean fbLIKE = false;
    private boolean fbWOW = false;
    private boolean fbHAHA = false;
    private boolean fbLOVE = false;
    private boolean fbANGERY = false;
    private boolean fbSAD = false;

    public FBReactions(){    }

    public boolean isFbLIKE() {
        return fbLIKE;
    }

    public void setFbLIKE(boolean fbLIKE) {
        this.fbLIKE = fbLIKE;
    }

    public boolean isFbWOW() {
        return fbWOW;
    }

    public void setFbWOW(boolean fbWOW) {
        this.fbWOW = fbWOW;
    }

    public boolean isFbHAHA() {
        return fbHAHA;
    }

    public void setFbHAHA(boolean fbHAHA) {
        this.fbHAHA = fbHAHA;
    }

    public boolean isFbLOVE() {
        return fbLOVE;
    }

    public void setFbLOVE(boolean fbLOVE) {
        this.fbLOVE = fbLOVE;
    }

    public boolean isFbANGERY() {
        return fbANGERY;
    }

    public void setFbANGERY(boolean fbANGERY) {
        this.fbANGERY = fbANGERY;
    }

    public boolean isFbSAD() {
        return fbSAD;
    }

    public void setFbSAD(boolean fbSAD) {
        this.fbSAD = fbSAD;
    }
}
