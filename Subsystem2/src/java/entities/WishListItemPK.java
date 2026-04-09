/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author lukapantovic
 */
@Embeddable
public class WishListItemPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "IdW")
    private int idW;
    @Basic(optional = false)
    @NotNull
    @Column(name = "IdI")
    private int idI;

    public WishListItemPK() {
    }

    public WishListItemPK(int idW, int idI) {
        this.idW = idW;
        this.idI = idI;
    }

    public int getIdW() {
        return idW;
    }

    public void setIdW(int idW) {
        this.idW = idW;
    }

    public int getIdI() {
        return idI;
    }

    public void setIdI(int idI) {
        this.idI = idI;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idW;
        hash += (int) idI;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WishListItemPK)) {
            return false;
        }
        WishListItemPK other = (WishListItemPK) object;
        if (this.idW != other.idW) {
            return false;
        }
        if (this.idI != other.idI) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.WishListItemPK[ idW=" + idW + ", idI=" + idI + " ]";
    }
    
}
