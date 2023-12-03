package de.cofinpro.sales.voucher.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "sequenceVoucher", sequenceName = "VoucherSeq")
@Table(name = "vouchers")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "sequenceVoucher")
    @Column(name = "voucher_id")
    private Long id;

    private String code;

    private int discount;

    @CreationTimestamp
    private LocalDateTime validFrom;

    @CreationTimestamp
    private LocalDateTime validUntil;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Voucher voucher = (Voucher) o;
        return id != null && Objects.equals(id, voucher.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
