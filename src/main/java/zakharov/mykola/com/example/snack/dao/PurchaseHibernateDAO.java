package zakharov.mykola.com.example.snack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import zakharov.mykola.com.example.snack.entity.Purchase;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseHibernateDAO extends JpaRepository<Purchase, Long> {
    List<Purchase> findAllByDateOrderByCategoryAsc (LocalDate date);
    List<Purchase> findAllByDate_Month (Short date_month);
    Purchase findByDateAndCategory_Name (LocalDate date, String categoryName);
}
