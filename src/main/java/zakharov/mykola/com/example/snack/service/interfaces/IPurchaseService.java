package zakharov.mykola.com.example.snack.service.interfaces;

import zakharov.mykola.com.example.snack.model.CategoryModel;
import zakharov.mykola.com.example.snack.model.ResponseModel;

import java.time.LocalDate;

public interface IPurchaseService {
    ResponseModel purchase(CategoryModel categoryModel);
    ResponseModel reportByDay(LocalDate date);
    ResponseModel reportByMonth(Short month);
}
