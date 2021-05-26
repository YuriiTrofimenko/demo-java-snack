package zakharov.mykola.com.example.snack.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Builder
public class ReportModel {
    public Collection<ReportItemModel> items;
    public BigDecimal totalSum;
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        items.forEach(reportItemModel -> {
            sb.append(String.format("%s\n", reportItemModel.toString()));
        });
        sb.append("Total " + totalSum);
        return sb.toString();
    }
}
