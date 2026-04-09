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

/**
 *
 * @author lukapantovic
 */
@Entity
@Table(name = "Basket_Item")
@NamedQueries({
    @NamedQuery(name = "BasketItem.findAll", query = "SELECT b FROM BasketItem b"),
    @NamedQuery(name = "BasketItem.findByIdI", query = "SELECT b FROM BasketItem b WHERE b.basketItemPK.idI = :idI"),
    @NamedQuery(name = "BasketItem.findByIdB", query = "SELECT b FROM BasketItem b WHERE b.basketItemPK.idB = :idB"),
    @NamedQuery(name = "BasketItem.findByAmount", query = "SELECT b FROM BasketItem b WHERE b.amount = :amount")})
public class BasketItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected BasketItemPK basketItemPK;
    @Column(name = "Amount")
    private Integer amount;
    @JoinColumn(name = "IdB", referencedColumnName = "IdB", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Basket basket;
    @JoinColumn(name = "IdI", referencedColumnName = "IdI", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Item item;

    public BasketItem() {
    }

    public BasketItem(BasketItemPK basketItemPK) {
        this.basketItemPK = basketItemPK;
    }

    public BasketItem(int idI, int idB) {
        this.basketItemPK = new BasketItemPK(idI, idB);
    }

    public BasketItemPK getBasketItemPK() {
        return basketItemPK;
    }

    public void setBasketItemPK(BasketItemPK basketItemPK) {
        this.basketItemPK = basketItemPK;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (basketItemPK != null ? basketItemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BasketItem)) {
            return false;
        }
        BasketItem other = (BasketItem) object;
        if ((this.basketItemPK == null && other.basketItemPK != null) || (this.basketItemPK != null && !this.basketItemPK.equals(other.basketItemPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.BasketItem[ basketItemPK=" + basketItemPK + " ]";
    }
    
}
