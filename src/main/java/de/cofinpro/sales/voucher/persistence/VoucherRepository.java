package de.cofinpro.sales.voucher.persistence;

import de.cofinpro.sales.voucher.model.Voucher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends CrudRepository<Voucher, Long> {
}
