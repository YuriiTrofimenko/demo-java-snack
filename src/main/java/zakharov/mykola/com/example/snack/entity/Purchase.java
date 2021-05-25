package zakharov.mykola.com.example.snack.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name="purchases")
@Data
@EqualsAndHashCode()
@ToString()
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    // @NotBlank(message = "Date is mandatory")
    // @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name="date", columnDefinition = "TIMESTAMP")
    // @CreationTimestamp
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable=false)
    private Category category;

}
