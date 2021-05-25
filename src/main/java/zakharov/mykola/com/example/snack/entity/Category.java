package zakharov.mykola.com.example.snack.entity;

import lombok.*;

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
@EqualsAndHashCode()
@ToString()
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank(message = "Name is mandatory")
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @NotNull
    @NotBlank(message = "Price is mandatory")
    @Column(name="price", nullable = false, columnDefinition="Decimal(10,2)")
    private BigDecimal price;

    @NotNull
    @Min(0)
    @Column(name="number", nullable = false, columnDefinition = "integer default 0")
    private Integer number;

    @NotNull
    @Column(name="available", nullable = false, columnDefinition = "boolean default true")
    private Boolean available;

    @OneToMany(mappedBy = "category")
    private Set<Purchase> setOfPurchases = new HashSet<>(0);

    public boolean isAvailable () {
        return available.equals("true");
    }

}
