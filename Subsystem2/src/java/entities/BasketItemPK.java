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
public class BasketItemPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "IdI")
    private int idI;
    @Basic(optional = false)
    @NotNull
    @Column(name = "IdB")
    private int idB;

    public BasketItemPK() {
    }

    public BasketItemPK(int idI, int idB) {
        this.idI = idI;
        this.idB = idB;
    }

    public int getIdI() {
        return idI;
    }

    public void setIdI(int idI) {
        this.idI = idI;
    }

    public int getIdB() {
        return idB;
    }

    public void setIdB(int idB) {
        this.idB = idB;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idI;
        hash += (int) idB;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BasketItemPK)) {
            return false;
        }
        BasketItemPK other = (BasketItemPK) object;
        if (this.idI != other.idI) {
            return false;
        }
        if (this.idB != other.idB) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.BasketItemPK[ idI=" + idI + ", idB=" + idB + " ]";
    }
    
}
