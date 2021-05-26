package zakharov.mykola.com.example.snack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zakharov.mykola.com.example.snack.dao.CategoryHibernateDAO;
import zakharov.mykola.com.example.snack.dao.PurchaseHibernateDAO;
import zakharov.mykola.com.example.snack.entity.Category;
import zakharov.mykola.com.example.snack.entity.Purchase;
import zakharov.mykola.com.example.snack.model.CategoryModel;
import zakharov.mykola.com.example.snack.model.ReportItemModel;
import zakharov.mykola.com.example.snack.model.ReportModel;
import zakharov.mykola.com.example.snack.model.ResponseModel;

import javax.transaction.Transactional;
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
        List<Purchase> purchases =
            purchaseDao.findAllByDateAfterOrderByCategoryAsc(date.minusDays(1));
        return makeReport(purchases);
    }

    // report by month
    public ResponseModel reportByMonth(String yearMonth) {
        List<Purchase> purchases =
            purchaseDao.findAllByDate_Month(
                Integer.parseInt(yearMonth.split("-")[0]),
                Integer.parseInt(yearMonth.split("-")[1])
            );
        return makeReport(purchases);
    }

    private ResponseModel makeReport(List<Purchase> purchases) {
        Map<String, ReportItemModel> purchaseStatsMap = new HashMap<>();
        purchases.forEach(purchase -> {
            Category category = purchase.getCategory();
            String name = category.getName();
            if (purchaseStatsMap.containsKey(name)) {
                ReportItemModel item = purchaseStatsMap.get(name);
                item.quantity++;
                item.totalPrice = item.totalPrice.add(item.totalPrice);
                purchaseStatsMap.put(name, item);
            } else {
                purchaseStatsMap.put(
                    name,
                    ReportItemModel.builder()
                        .categoryName(name)
                        .totalPrice(category.getPrice())
                        .quantity(1)
                        .build()
                );
            }
        });
        BigDecimal total = new BigDecimal(0);
        for (ReportItemModel item: purchaseStatsMap.values()) {
            total = total.add(item.totalPrice);// * item.quantity;
        }
        return ResponseModel.builder()
            .status(ResponseModel.SUCCESS_STATUS)
            .message(ReportModel.builder().items(purchaseStatsMap.values()).totalSum(total).build().toString())
            .data(purchaseStatsMap)
            .build();
    }
}
