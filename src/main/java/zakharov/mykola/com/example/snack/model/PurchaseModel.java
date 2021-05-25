package zakharov.mykola.com.example.snack.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Data
@EqualsAndHashCode()
@ToString()
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseModel {
    private Long id;
    private Date date;
    public Long categoryId;
    public CategoryModel category;
}
