/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author lukapantovic
 */
@Entity
@Table(name = "Item")
@NamedQueries({
    @NamedQuery(name = "Item.findAll", query = "SELECT i FROM Item i"),
    @NamedQuery(name = "Item.findByIdI", query = "SELECT i FROM Item i WHERE i.idI = :idI"),
    @NamedQuery(name = "Item.findByName", query = "SELECT i FROM Item i WHERE i.name = :name"),
    @NamedQuery(name = "Item.findByDescription", query = "SELECT i FROM Item i WHERE i.description = :description"),
    @NamedQuery(name = "Item.findByPrice", query = "SELECT i FROM Item i WHERE i.price = :price"),
    @NamedQuery(name = "Item.findByDiscount", query = "SELECT i FROM Item i WHERE i.discount = :discount")})
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdI")
    private Integer idI;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Name")
    private String name;
    @Size(max = 45)
    @Column(name = "Description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Price")
    private int price;
    @Column(name = "Discount")
    private Integer discount;
    @JoinColumn(name = "IdC", referencedColumnName = "IdC")
    @ManyToOne(optional = false)
    private Category idC;
    @JoinColumn(name = "IdCrea", referencedColumnName = "IdU")
    @ManyToOne(optional = false)
    private User idCrea;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<WishListItem> wishListItemList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<BasketItem> basketItemList;

    public Item() {
    }

    public Item(Integer idI) {
        this.idI = idI;
    }

    public Item(Integer idI, String name, int price) {
        this.idI = idI;
        this.name = name;
        this.price = price;
    }

    public Integer getIdI() {
        return idI;
    }

    public void setIdI(Integer idI) {
        this.idI = idI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Category getIdC() {
        return idC;
    }

    public void setIdC(Category idC) {
        this.idC = idC;
    }

    public User getIdCrea() {
        return idCrea;
    }

    public void setIdCrea(User idCrea) {
        this.idCrea = idCrea;
    }

    public List<WishListItem> getWishListItemList() {
        return wishListItemList;
    }

    public void setWishListItemList(List<WishListItem> wishListItemList) {
        this.wishListItemList = wishListItemList;
    }

    public List<BasketItem> getBasketItemList() {
        return basketItemList;
    }

    public void setBasketItemList(List<BasketItem> basketItemList) {
        this.basketItemList = basketItemList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idI != null ? idI.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Item)) {
            return false;
        }
        Item other = (Item) object;
        if ((this.idI == null && other.idI != null) || (this.idI != null && !this.idI.equals(other.idI))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Item[ idI=" + idI + " ]";
    }
    
}
