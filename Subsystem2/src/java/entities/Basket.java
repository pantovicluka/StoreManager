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

/**
 *
 * @author lukapantovic
 */
@Entity
@Table(name = "Basket")
@NamedQueries({
    @NamedQuery(name = "Basket.findAll", query = "SELECT b FROM Basket b"),
    @NamedQuery(name = "Basket.findByIdB", query = "SELECT b FROM Basket b WHERE b.idB = :idB"),
    @NamedQuery(name = "Basket.findByValue", query = "SELECT b FROM Basket b WHERE b.value = :value")})
public class Basket implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdB")
    private Integer idB;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Value")
    private int value;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "basket")
    private List<BasketItem> basketItemList;
    @JoinColumn(name = "IdU", referencedColumnName = "IdU")
    @ManyToOne(optional = false)
    private User idU;

    public Basket() {
    }

    public Basket(Integer idB) {
        this.idB = idB;
    }

    public Basket(Integer idB, int value) {
        this.idB = idB;
        this.value = value;
    }

    public Integer getIdB() {
        return idB;
    }

    public void setIdB(Integer idB) {
        this.idB = idB;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<BasketItem> getBasketItemList() {
        return basketItemList;
    }

    public void setBasketItemList(List<BasketItem> basketItemList) {
        this.basketItemList = basketItemList;
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
        hash += (idB != null ? idB.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Basket)) {
            return false;
        }
        Basket other = (Basket) object;
        if ((this.idB == null && other.idB != null) || (this.idB != null && !this.idB.equals(other.idB))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Basket[ idB=" + idB + " ]";
    }
    
}
