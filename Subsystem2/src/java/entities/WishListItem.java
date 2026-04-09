/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author lukapantovic
 */
@Entity
@Table(name = "WishList_Item")
@NamedQueries({
    @NamedQuery(name = "WishListItem.findAll", query = "SELECT w FROM WishListItem w"),
    @NamedQuery(name = "WishListItem.findByIdW", query = "SELECT w FROM WishListItem w WHERE w.wishListItemPK.idW = :idW"),
    @NamedQuery(name = "WishListItem.findByIdI", query = "SELECT w FROM WishListItem w WHERE w.wishListItemPK.idI = :idI"),
    @NamedQuery(name = "WishListItem.findByTime", query = "SELECT w FROM WishListItem w WHERE w.time = :time")})
public class WishListItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected WishListItemPK wishListItemPK;
    @Size(max = 45)
    @Column(name = "Time")
    private String time;
    @JoinColumn(name = "IdI", referencedColumnName = "IdI", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Item item;
    @JoinColumn(name = "IdW", referencedColumnName = "IdW", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private WishList wishList;

    public WishListItem() {
    }

    public WishListItem(WishListItemPK wishListItemPK) {
        this.wishListItemPK = wishListItemPK;
    }

    public WishListItem(int idW, int idI) {
        this.wishListItemPK = new WishListItemPK(idW, idI);
    }

    public WishListItemPK getWishListItemPK() {
        return wishListItemPK;
    }

    public void setWishListItemPK(WishListItemPK wishListItemPK) {
        this.wishListItemPK = wishListItemPK;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public WishList getWishList() {
        return wishList;
    }

    public void setWishList(WishList wishList) {
        this.wishList = wishList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (wishListItemPK != null ? wishListItemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WishListItem)) {
            return false;
        }
        WishListItem other = (WishListItem) object;
        if ((this.wishListItemPK == null && other.wishListItemPK != null) || (this.wishListItemPK != null && !this.wishListItemPK.equals(other.wishListItemPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.WishListItem[ wishListItemPK=" + wishListItemPK + " ]";
    }
    
}
