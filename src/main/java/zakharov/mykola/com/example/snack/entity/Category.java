package zakharov.mykola.com.example.snack.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="categories")
@Data
@EqualsAndHashCode(exclude = "setOfPurchases")
@ToString(exclude = "setOfPurchases")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank(message = "Name is mandatory")
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @NotNull
    // @NotBlank(message = "Price is mandatory")
    @Column(name="price", nullable = false, columnDefinition="Decimal(10,2)")
    private BigDecimal price;

    // @Min(0)
    // @Column(name="number", columnDefinition = "integer NOT NULL default 0")
    @ColumnDefault(value = "0")
    private Integer number = 0;

    // @Column(name="available", columnDefinition = "boolean NOT NULL default true")
    @ColumnDefault(value = "true")
    private Boolean available = true;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<Purchase> setOfPurchases = new HashSet<>(0);

    /* public boolean isAvailable () {
        return available.equals("true");
    } */

}
