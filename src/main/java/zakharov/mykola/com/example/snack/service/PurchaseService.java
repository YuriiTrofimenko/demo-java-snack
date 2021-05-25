package zakharov.mykola.com.example.snack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zakharov.mykola.com.example.snack.dao.CategoryHibernateDAO;
import zakharov.mykola.com.example.snack.dao.PurchaseHibernateDAO;
import zakharov.mykola.com.example.snack.entity.Category;
import zakharov.mykola.com.example.snack.entity.Purchase;
import zakharov.mykola.com.example.snack.model.CategoryModel;
import zakharov.mykola.com.example.snack.model.ResponseModel;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseService {

    @Autowired
    private PurchaseHibernateDAO purchaseDao;

    @Autowired
    private CategoryHibernateDAO categoryDao;

    //edit current, purchase item
    public ResponseModel purchase(@Valid CategoryModel categoryModel) {
        Optional<Category> categoryFromDao = categoryDao.findById(categoryModel.getId());
        if (categoryFromDao.isEmpty()
                || (categoryFromDao.get().getNumber() == 0) ) {
            return ResponseModel.builder()
                .status(ResponseModel.FAIL_STATUS)
                .message(String.format("Item %s Is Not Purchased", categoryModel.getName()))
                .build();
        }
        Category category =
            Category.builder()
                .id(categoryModel.getId())
                .name(categoryModel.getName())
                .price(categoryModel.getPrice())
                .number(categoryFromDao.get().getNumber() - 1)
                .available(categoryModel.getAvailable())
                .build();
        categoryDao.save(category);
        Purchase purchase;
        Purchase purchaseFromDao = purchaseDao.findByDateAndCategory_Name(LocalDate.now(), categoryModel.getName());
        if (purchaseFromDao.getCategory().getName() == null) {
            purchase =
                Purchase.builder()
                    .date(LocalDate.now())
                    .category(
                        Category.builder()
                            .name(categoryFromDao.get().getName())
                            .price(categoryFromDao.get().getPrice())
                            .number(1)
                            .build()
                    )
                    .build();
        } else {
            purchase =
                Purchase.builder()
                    .id(purchaseFromDao.getId())
                    .date(purchaseFromDao.getDate())
                    .category(
                        Category.builder()
                            .name(categoryFromDao.get().getName())
                            .price(purchaseFromDao.getCategory().getPrice()
                                    .add(categoryFromDao.get().getPrice())
                            )
                            .number(purchaseFromDao.getCategory().getNumber() + 1)
                            .build()
                    )
                    .build();
        }
        purchaseDao.save(purchase);
        return ResponseModel.builder()
            .status(ResponseModel.SUCCESS_STATUS)
            //.message(String.format("Item %s Added For Sale", category.getName()) + "\n" +
                    //String.format("%s Purchase Is completed", purchase.getCategory().getName()))
            .message(String.format("%s \n %s %f",
                    purchase.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    category.getName(), category.getPrice()))
            .data(purchase)
            .build();
    }

    // report by day
    public ResponseModel reportByDay(LocalDate date) {
        List<Purchase> purchases = purchaseDao.findAllByDateOrderByCategoryAsc(date);
        List<CategoryModel> categoryModels =
            purchases.stream()
                .map(purchase ->
                    CategoryModel.builder()
                        .name(purchase.getCategory().getName())
                        .price(purchase.getCategory().getPrice())
                        .number(purchase.getCategory().getNumber())
                        .build()
                )
                .collect(Collectors.toList());
        List<BigDecimal> listOfPrices =
                purchases.stream()
                .map(purchase -> purchase.getCategory().getPrice())
                .collect(Collectors.toList());
        double total = 0.00;
        for (BigDecimal number: listOfPrices) {
            total += number.doubleValue();
        }
        return ResponseModel.builder()
            .status(ResponseModel.SUCCESS_STATUS)
            .data(categoryModels)
            .message(String.format(">Total %s", total))
            .build();
    }

    // report by month
    public ResponseModel reportByMonth(Short month) {
        List<Purchase> purchases = purchaseDao.findAllByDate_Month(month);
        List<Purchase> purchasesSortedByCategoryName =
            purchases.stream()
                .sorted(Comparator.comparing(purchase -> purchase.getCategory().getName()))
                .collect(Collectors.toList());
        List<CategoryModel> categoryModels =
                purchasesSortedByCategoryName.stream()
                .map(purchase ->
                    CategoryModel.builder()
                        .name(purchase.getCategory().getName())
                        .price(purchase.getCategory().getPrice())
                        .number(purchase.getCategory().getNumber())
                        .build()
                    )
                    .collect(Collectors.toList());
        List<BigDecimal> listOfPrices =
                purchases.stream()
                        .map(purchase -> purchase.getCategory().getPrice())
                        .collect(Collectors.toList());
        double total = 0.00;
        for (BigDecimal number: listOfPrices) {
            total += number.doubleValue();
        }
        return ResponseModel.builder()
            .status(ResponseModel.SUCCESS_STATUS)
            .data(categoryModels)
            .message(String.format(">Total %s", total))
            .build();
    }

}
