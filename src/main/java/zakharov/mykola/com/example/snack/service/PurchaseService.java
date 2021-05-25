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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseService {

    @Autowired
    private PurchaseHibernateDAO purchaseDao;

    @Autowired
    private CategoryHibernateDAO categoryDao;

    public ResponseModel purchase(String categoryName, LocalDate date) {
        Category category =
            categoryDao.findCategoryByName(categoryName);
        if (category != null) {
            if (category.getNumber() > 0) {
                purchaseDao.save(
                    Purchase.builder()
                        .category(category)
                        .date(date)
                        .build()
                );
                category.setNumber(category.getNumber() - 1);
                categoryDao.save(category);
                return ResponseModel.builder()
                    .status(ResponseModel.SUCCESS_STATUS)
                    .message(
                        String.format(
                            "%s \n %s %f",
                            date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            categoryName,
                            category.getPrice()
                        )
                    ).build();
            } else {
                return ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message(
                        String.format(
                            "Category %s out of stock",
                            categoryName
                        )
                    ).build();
            }
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
    }

    //edit current, purchase item
    public ResponseModel purchase(CategoryModel categoryModel) {
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
        List<Purchase> purchases = purchaseDao.findAllByDateAfterOrderByCategoryAsc(date);
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
    public ResponseModel reportByMonth(String yearMonth) {
        List<Purchase> purchases =
            purchaseDao.findAllByDate_Month(
                Integer.parseInt(yearMonth.split("-")[0]),
                Integer.parseInt(yearMonth.split("-")[1])
            );
        System.out.println(purchases.size());
        Map<Purchase, Integer> purchaseStatsMap = new HashMap<>();
        purchases.forEach(purchase -> {
            if (purchaseStatsMap.containsKey(purchase)) {
                purchaseStatsMap.put(purchase, purchaseStatsMap.get(purchase) + 1);
            } else {
                purchaseStatsMap.put(purchase, 1);
            }
        });

        /* List<Purchase> purchasesSortedByCategoryName =
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
                        .collect(Collectors.toList());*/
        double total = 0.00;
        for (Map.Entry<Purchase, Integer> purchaseEntry: purchaseStatsMap.entrySet()) {
            total += purchaseEntry.getKey().getCategory().getNumber() * purchaseEntry.getValue();
        }
        return ResponseModel.builder()
            .status(ResponseModel.SUCCESS_STATUS)
            .message(String.format(">Total %s", total))
            .data(purchaseStatsMap)
            .build();
    }

}
