package zakharov.mykola.com.example.snack.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import zakharov.mykola.com.example.snack.dao.CategoryHibernateDAO;
import zakharov.mykola.com.example.snack.entity.Category;
import zakharov.mykola.com.example.snack.model.CategoryModel;
import zakharov.mykola.com.example.snack.model.ResponseModel;
import zakharov.mykola.com.example.snack.service.CategoryService;
import zakharov.mykola.com.example.snack.service.interfaces.ICategoryService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations="classpath:application.properties")
public class CategoryServiceTest {

    @Mock
    private CategoryHibernateDAO categoryDAO;

    @Mock
    private ICategoryService categoryServiceMock;

    @InjectMocks
    private CategoryService categoryService;

    ArgumentCaptor<Category> categoryArgument =
            ArgumentCaptor.forClass(Category.class);

    @Test
    void shouldCreatedAddCategoryCategorySuccessfully() {
        ResponseModel responseModel =
                categoryService.addCategory(returnCategoryModelWithoutId());
        assertNotNull(responseModel);
        assertEquals(ResponseModel.SUCCESS_STATUS, responseModel.getStatus());
        verify(categoryDAO, atLeast(1))
                .save(categoryArgument.capture());
        verify(categoryDAO, atMost(1))
                .save(categoryArgument.capture());
    }

    // ЯКЩО ЗАКОМЕНТУВАТИ ЦЕЙ ТО ІНШІ КРАЩЕ ПРОЙДУТЬ ТЕСТ
    @Test
    void shouldUpdatedAddedItemCategoryUnsuccessfully() {
        /* final CategoryModel categoryModel =
                returnCategoryModelWithId(); */
        ResponseModel responseModel =
                categoryService.addItem("Undefined", 1);
        assertNotNull(responseModel);
        assertEquals(ResponseModel.FAIL_STATUS, responseModel.getStatus());
        /* verify(categoryDAO, atLeast(1))
                .findById(categoryArgument.capture().getId());
        verify(categoryDAO, atMost(1))
                .findById(categoryArgument.capture().getId()); */
    }

    @Test
    void shouldUpdatedAddedItemCategorySuccessfully() {
        /* final CategoryModel categoryModel =
                returnCategoryModelWithId(); */
        doReturn(Category.builder().name("Donut").number(0).build())
            .when(categoryDAO)
            .findCategoryByName("Donut");
        ResponseModel responseModel =
                categoryService.addItem("Donut", 5);
        assertNotNull(responseModel);
        assertEquals(ResponseModel.SUCCESS_STATUS, responseModel.getStatus());
        verify(categoryDAO, atLeast(1))
                .save(categoryArgument.capture());
        verify(categoryDAO, atMost(1))
                .save(categoryArgument.capture());
    }

    @Test
    void shouldReturnGetAllList() {
        doReturn(returnListOfCategories()
        ).when(categoryDAO)
            .findAllByAvailableTrueOrderByNumberDesc();
        ResponseModel responseModel = categoryService.list();
        assertNotNull(responseModel);
        assertNotNull(responseModel.getData());
        assertEquals(((List)responseModel.getData()).size(), 3);
    }

    @Test
    void shouldClearSuccessfully() {
        doReturn(returnListOfCategoriesWithZeroAmount()
        ).when(categoryDAO)
            .findAllByNumberEquals(0);
            categoryService.clear();
        verify(categoryDAO, atLeast(1))
            .findAllByNumberEquals(0);
    }

    CategoryModel returnCategoryModelWithoutId () {
        return CategoryModel.builder()
                .name("Chocolate bar")
                .price(BigDecimal.valueOf(22.15))
                .number(12)
                .available(true)
                .build();
    }

    CategoryModel returnCategoryModelWithId () {
        return CategoryModel.builder()
                .id(1L)
                .name("Donut")
                .price(BigDecimal.valueOf(9))
                .number(5)
                .available(true)
                .build();
    }

    List<Category> returnListOfCategories () {
        return Arrays.asList(
                        Category.builder()
                                .id(1L)
                                .name("Cracker")
                                .price(BigDecimal.valueOf(7))
                                .number(7)
                                .available(true)
                                .build(),
                        Category.builder()
                                .id(2L)
                                .name("Donut")
                                .price(BigDecimal.valueOf(9))
                                .number(5)
                                .available(true)
                                .build(),
                        Category.builder()
                                .id(3L)
                                .name("Chocolate bar")
                                .price(BigDecimal.valueOf(22.15))
                                .number(12)
                                .available(true)
                                .build()
        );
    }

    List<Category> returnListOfCategoriesWithZeroAmount () {
        return Arrays.asList(
                Category.builder()
                        .id(4L)
                        .name("Burger")
                        .price(BigDecimal.valueOf(9.75))
                        .number(0)
                        .available(true)
                        .build(),
                Category.builder()
                        .id(5L)
                        .name("Fanta")
                        .price(BigDecimal.valueOf(2))
                        .number(0)
                        .available(true)
                        .build(),
                Category.builder()
                        .id(6L)
                        .name("Water")
                        .price(BigDecimal.valueOf(4))
                        .number(0)
                        .available(true)
                        .build()
        );
    }

    List<CategoryModel> categoryListToCategoryModelList(List<Category> categoriesAvailableTrue) {
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
        return categoryModels;
    }

}
