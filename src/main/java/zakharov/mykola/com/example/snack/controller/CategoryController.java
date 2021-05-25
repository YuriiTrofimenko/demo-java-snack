package zakharov.mykola.com.example.snack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zakharov.mykola.com.example.snack.model.CategoryModel;
import zakharov.mykola.com.example.snack.model.ResponseModel;
import zakharov.mykola.com.example.snack.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;
    public CategoryController (CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // create new
    @PostMapping("")
    public ResponseEntity<ResponseModel> addCategory(@RequestBody CategoryModel category) {
        return new ResponseEntity<>(categoryService.addCategory(category), HttpStatus.CREATED);
    }

    //edit current, add items for sale
    @PatchMapping(value = "/{id}")
    public ResponseEntity<ResponseModel> addItem(@PathVariable Long id, @RequestBody CategoryModel category) {
        category.setId(id);
        return new ResponseEntity<>(categoryService.addItem(category), HttpStatus.OK);
    }

    // show all categories available for sale sorted by amount
    @GetMapping("")
    public ResponseEntity<ResponseModel> list() {
        return new ResponseEntity<>(categoryService.list(), HttpStatus.OK);
    }

    // show all categories available for sale sorted by amount
    @GetMapping("/clear")
    public ResponseEntity<ResponseModel> clear() {
        return new ResponseEntity<>(categoryService.clear(), HttpStatus.OK);
    }

}
