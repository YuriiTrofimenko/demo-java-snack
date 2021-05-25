package zakharov.mykola.com.example.snack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zakharov.mykola.com.example.snack.entity.Purchase;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseHibernateDAO extends JpaRepository<Purchase, Long> {
    List<Purchase> findAllByDateAfterOrderByCategoryAsc (LocalDate date);
    // List<Purchase> findAllByDate_Month (Short date_month);
    @Query("SELECT p FROM Purchase p WHERE EXTRACT (year FROM p.date) = :yearParam AND EXTRACT (month FROM p.date) = :monthParam")
    List<Purchase> findAllByDate_Month(@Param("yearParam") int yearParam, @Param("monthParam") int monthParam);
    Purchase findByDateAndCategory_Name (LocalDate date, String categoryName);
}
