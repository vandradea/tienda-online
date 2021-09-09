package pe.edu.upc.invoice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.invoice.entity.InvoiceItem;

public interface InvoiceItemsRepository extends JpaRepository<InvoiceItem,Long> {
}
