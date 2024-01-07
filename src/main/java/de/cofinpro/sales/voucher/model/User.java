package de.cofinpro.sales.voucher.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "sequence", sequenceName = "UserSeq")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
    @Column(name = "user_id")
    private Long id;

    @NotEmpty(message = "The user name must not be empty")
    private String name;

    @NotEmpty(message = "last name must not be empty")
    private String lastname;

    @NotEmpty(message = "Email must not be empty")
    @Pattern(regexp = ".*@company\\.com", message = "Email from given domain not allowed")
    private String email;

    @NotNull
    // TODO: Validate password
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled = true;

    private boolean accountNonLocked = true;

    private int failedAttempt;

    @JsonIgnore
    public boolean isAdmin() {
        return Role.ADMIN.equals(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
