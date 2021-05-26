package zakharov.mykola.com.example.snack.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class ReportItemModel {
    public String categoryName;
    public BigDecimal totalPrice;
    public Integer quantity;
    @Override
    public String toString() {
        return String.format("%s %.2f %d", categoryName, totalPrice, quantity);
    }
}
