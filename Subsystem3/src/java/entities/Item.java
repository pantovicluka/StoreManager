/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "Item")
@NamedQueries({
    @NamedQuery(name = "Item.findAll", query = "SELECT i FROM Item i"),
    @NamedQuery(name = "Item.findByIdI", query = "SELECT i FROM Item i WHERE i.idI = :idI"),
    @NamedQuery(name = "Item.findByIdItem", query = "SELECT i FROM Item i WHERE i.idItem = :idItem"),
    @NamedQuery(name = "Item.findByAmount", query = "SELECT i FROM Item i WHERE i.amount = :amount"),
    @NamedQuery(name = "Item.findByPricePerUnit", query = "SELECT i FROM Item i WHERE i.pricePerUnit = :pricePerUnit")})
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdI")
    private Integer idI;
    @Column(name = "IdItem")
    private Integer idItem;
    @Column(name = "Amount")
    private Integer amount;
    @Column(name = "PricePerUnit")
    private Integer pricePerUnit;
    @JoinColumn(name = "IdD", referencedColumnName = "IdD")
    @ManyToOne(optional = false)
    private Delivery idD;
    @JoinColumn(name = "IdSell", referencedColumnName = "IdU")
    @ManyToOne
    private User idSell;

    public Item() {
    }

    public Item(Integer idI) {
        this.idI = idI;
    }

    public Integer getIdI() {
        return idI;
    }

    public void setIdI(Integer idI) {
        this.idI = idI;
    }

    public Integer getIdItem() {
        return idItem;
    }

    public void setIdItem(Integer idItem) {
        this.idItem = idItem;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Integer pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Delivery getIdD() {
        return idD;
    }

    public void setIdD(Delivery idD) {
        this.idD = idD;
    }

    public User getIdSell() {
        return idSell;
    }

    public void setIdSell(User idSell) {
        this.idSell = idSell;
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
