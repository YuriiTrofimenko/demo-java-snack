package zakharov.mykola.com.example.snack.service;

import org.springframework.stereotype.Service;
import zakharov.mykola.com.example.snack.dao.CategoryHibernateDAO;
import zakharov.mykola.com.example.snack.entity.Category;
import zakharov.mykola.com.example.snack.model.CategoryModel;
import zakharov.mykola.com.example.snack.model.ResponseModel;
import zakharov.mykola.com.example.snack.service.interfaces.ICategoryService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService implements ICategoryService {

    private final CategoryHibernateDAO categoryDao;
    public CategoryService (CategoryHibernateDAO categoryDao) {
        this.categoryDao = categoryDao;
    }

    private static CategoryModel categoryEntityToModel(Category category) {
        return CategoryModel.builder()
            .id(category.getId())
            .name(category.getName())
            .price(category.getPrice())
            .number(category.getNumber())
            .available(category.getAvailable())
            .build();
    }

    // create new
    public ResponseModel addCategory(CategoryModel categoryModel) {
        Category category =
                Category.builder()
                        .name(categoryModel.getName().trim())
                        .price(categoryModel.getPrice())
                        .number(categoryModel.getNumber() != null ? categoryModel.getNumber() : 0)
                        // .available(categoryModel.getAvailable())
                        .build();
        categoryDao.save(category);
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                //.message(String.format("Category %s Added", categoty.getName()))
                .message(String.format("%s %f %d", category.getName(), category.getPrice(), category.getNumber()))
                .build();
    }

    //edit current, add items for sale
    public ResponseModel addItem(String categoryName, Integer number) {
        Category category = categoryDao.findCategoryByName(categoryName);
        if (category != null) {
            category.setNumber(category.getNumber() + number);
            categoryDao.save(category);
            return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .message(String.format("%s %.2f %d", category.getName(), category.getPrice(), category.getNumber()))
                .build();
        } else {
            return ResponseModel.builder()
                .status(ResponseModel.FAIL_STATUS)
                .message(
                    String.format(
                        "Category %s not found",
                        categoryName
                    )
                ).build();
        }
        /* Category category =
                Category.builder()
                        .id(categoryModel.getId())
                        .name(categoryModel.getName())
                        .price(categoryModel.getPrice())
                        .number(categoryDao.findById(categoryModel.getId()).get().getNumber() + categoryModel.getNumber())
                        .available(categoryModel.getAvailable())
                        .build(); */
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
