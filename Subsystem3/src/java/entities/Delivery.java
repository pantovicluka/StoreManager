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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author lukapantovic
 */
@Entity
@Table(name = "Delivery")
@NamedQueries({
    @NamedQuery(name = "Delivery.findAll", query = "SELECT d FROM Delivery d"),
    @NamedQuery(name = "Delivery.findByIdD", query = "SELECT d FROM Delivery d WHERE d.idD = :idD"),
    @NamedQuery(name = "Delivery.findByValue", query = "SELECT d FROM Delivery d WHERE d.value = :value"),
    @NamedQuery(name = "Delivery.findByTime", query = "SELECT d FROM Delivery d WHERE d.time = :time"),
    @NamedQuery(name = "Delivery.findByAddress", query = "SELECT d FROM Delivery d WHERE d.address = :address"),
    @NamedQuery(name = "Delivery.findByCity", query = "SELECT d FROM Delivery d WHERE d.city = :city")})
public class Delivery implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdD")
    private Integer idD;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Value")
    private int value;
    @Column(name = "Time")
    @Temporal(TemporalType.TIME)
    private Date time;
    @Size(max = 45)
    @Column(name = "Address")
    private String address;
    @Size(max = 45)
    @Column(name = "City")
    private String city;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idD")
    private List<Item> itemList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idD")
    private List<Transaction> transactionList;
    @JoinColumn(name = "IdU", referencedColumnName = "IdU")
    @ManyToOne
    private User idU;

    public Delivery() {
    }

    public Delivery(Integer idD) {
        this.idD = idD;
    }

    public Delivery(Integer idD, int value) {
        this.idD = idD;
        this.value = value;
    }

    public Integer getIdD() {
        return idD;
    }

    public void setIdD(Integer idD) {
        this.idD = idD;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
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
        hash += (idD != null ? idD.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Delivery)) {
            return false;
        }
        Delivery other = (Delivery) object;
        if ((this.idD == null && other.idD != null) || (this.idD != null && !this.idD.equals(other.idD))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Delivery[ idD=" + idD + " ]";
    }
    
}
