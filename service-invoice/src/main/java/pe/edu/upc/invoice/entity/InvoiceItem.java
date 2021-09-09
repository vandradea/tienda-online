package pe.edu.upc.invoice.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Positive;

@Entity
@Data
@Table(name = "tbl_invoice_items")
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive(message = "El stock debe ser mayor que cero")
    private Double quantity;
    private Double price;

    @Column(name = "product_id")
    private Long productId;

    //Atributo que no será registrado en la BD, lo usaremos en nuestro Pojo
    // e incluso lo enviareos con json pero no sera regsitradro
    @Transient
    private Double subTotal;

    public Double getSubTotal(){
        if (this.price >0 && this.quantity >0){
            return this.quantity * this.price;
        }else{
            return (double) 0;
        }
    }

    public InvoiceItem(){
        this.quantity = (double)0;
        this.price = (double)0;
    }

}
