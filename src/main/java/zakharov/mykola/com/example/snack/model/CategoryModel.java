package zakharov.mykola.com.example.snack.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode()
// @ToString()
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class CategoryModel {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer number;
    private Boolean available;

    @Override
    public String toString() {
        return name + " " + price + " " + number;
    }
}
