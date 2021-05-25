package zakharov.mykola.com.example.snack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import zakharov.mykola.com.example.snack.entity.Category;

import java.util.List;

public interface CategoryHibernateDAO extends JpaRepository<Category, Long> {
    List<Category> findAllByAvailableTrueOrderByNumberDesc ();
    List<Category> findAllByNumberEquals (Integer number);
    Category findCategoryByName( String name);
}
