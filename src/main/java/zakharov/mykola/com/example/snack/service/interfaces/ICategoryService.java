package zakharov.mykola.com.example.snack.service.interfaces;

import zakharov.mykola.com.example.snack.entity.Category;
import zakharov.mykola.com.example.snack.model.CategoryModel;
import zakharov.mykola.com.example.snack.model.ResponseModel;

import java.util.List;

public interface ICategoryService {
    ResponseModel addCategory (CategoryModel categoryModel);
    ResponseModel addItem (CategoryModel categoryModel);
    ResponseModel list();
    ResponseModel clear();
    List<Category> findAllByAvailableTrueOrderByNumberDesc();
    List<Category> findAllByNumberEquals(Short amount);
}
