/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author lukapantovic
 */
@Entity
@Table(name = "WishList")
@NamedQueries({
    @NamedQuery(name = "WishList.findAll", query = "SELECT w FROM WishList w"),
    @NamedQuery(name = "WishList.findByIdW", query = "SELECT w FROM WishList w WHERE w.idW = :idW"),
    @NamedQuery(name = "WishList.findByDate", query = "SELECT w FROM WishList w WHERE w.date = :date")})
public class WishList implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdW")
    private Integer idW;
    @Column(name = "Date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wishList")
    private List<WishListItem> wishListItemList;
    @JoinColumn(name = "IdU", referencedColumnName = "IdU")
    @ManyToOne(optional = false)
    private User idU;

    public WishList() {
    }

    public WishList(Integer idW) {
        this.idW = idW;
    }

    public Integer getIdW() {
        return idW;
    }

    public void setIdW(Integer idW) {
        this.idW = idW;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<WishListItem> getWishListItemList() {
        return wishListItemList;
    }

    public void setWishListItemList(List<WishListItem> wishListItemList) {
        this.wishListItemList = wishListItemList;
    }

    public User getIdU() {
        return idU;
    }

    public void setIdU(User idU) {
        this.idU = idU;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idW != null ? idW.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WishList)) {
            return false;
        }
        WishList other = (WishList) object;
        if ((this.idW == null && other.idW != null) || (this.idW != null && !this.idW.equals(other.idW))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.WishList[ idW=" + idW + " ]";
    }
    
}
