package zakharov.mykola.com.example.snack.service;

import org.springframework.stereotype.Service;
import zakharov.mykola.com.example.snack.dao.CategoryHibernateDAO;
import zakharov.mykola.com.example.snack.entity.Category;
import zakharov.mykola.com.example.snack.model.CategoryModel;
import zakharov.mykola.com.example.snack.model.ResponseModel;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryHibernateDAO categoryDao;
    public CategoryService (CategoryHibernateDAO categoryDao) {
        this.categoryDao = categoryDao;
    }

    // create new
    public ResponseModel addCategory(@Valid CategoryModel categoryModel) {
        Category category =
                Category.builder()
                        .name(categoryModel.getName().trim())
                        .price(categoryModel.getPrice())
                        .number(categoryModel.getNumber())
                        .available(categoryModel.getAvailable())
                        .build();
        categoryDao.save(category);
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                //.message(String.format("Category %s Added", categoty.getName()))
                .message(String.format("%s %f %d", category.getName(), category.getPrice(), category.getNumber()))
                .build();
    }

    //edit current, add items for sale
    public ResponseModel addItem(@Valid CategoryModel categoryModel) {
        if (categoryDao.findById(categoryModel.getId()).isEmpty()) {
            return ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message(String.format("Item %s Is Not Added For Sale", categoryModel.getName()))
                    .build();
        }
        Category category =
                Category.builder()
                        .id(categoryModel.getId())
                        .name(categoryModel.getName())
                        .price(categoryModel.getPrice())
                        .number(categoryDao.findById(categoryModel.getId()).get().getNumber() + categoryModel.getNumber())
                        .available(categoryModel.getAvailable())
                        .build();
        categoryDao.save(category);
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                //.message(String.format("Item %s Added For Sale", category.getName()))
                .message(String.format("%s %f %d", category.getName(), category.getPrice(), category.getNumber()))
                .build();
    }

    // show all categories available for sale sorted by amount
    public ResponseModel list() {
        List<Category> categoriesAvailableTrue =
            categoryDao.findAllByAvailableTrueOrderByNumberDesc();
        List<CategoryModel> categoryModels =
            categoriesAvailableTrue.stream()
                .map(category ->
                    CategoryModel.builder()
                        .name(category.getName())
                        .price(category.getPrice())
                        .number(category.getNumber())
                        .build()
                )
            .collect(Collectors.toList());
        return ResponseModel.builder()
            .status(ResponseModel.SUCCESS_STATUS)
            .data(categoryModels)
            .build();
    }

    // stop serving snack with zero amount
    public ResponseModel clear() {
        List<Category> categoriesAllWithNumberZero = categoryDao.findAllByNumberEquals(0);
        categoriesAllWithNumberZero.forEach(category -> {
            category.setAvailable(false);
            categoryDao.save(category);
        });
        if (categoriesAllWithNumberZero.isEmpty()) {
            return ResponseModel.builder()
                    .status(ResponseModel.SUCCESS_STATUS)
                    .message("All Snacks Are Available")
                    .build();
        }
        List<CategoryModel> categoryModels =
                categoriesAllWithNumberZero.stream()
                .map(category ->
                    CategoryModel.builder()
                        .name(category.getName())
                        .price(category.getPrice())
                        .build()
                )
                .collect(Collectors.toList());
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .data(categoryModels)
                .build();
    }

}
