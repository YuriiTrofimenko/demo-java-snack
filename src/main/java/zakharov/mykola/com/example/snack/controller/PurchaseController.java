package zakharov.mykola.com.example.snack.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zakharov.mykola.com.example.snack.model.CategoryModel;
import zakharov.mykola.com.example.snack.model.ResponseModel;
import zakharov.mykola.com.example.snack.service.PurchaseService;

import java.time.LocalDate;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;
    public PurchaseController (PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping("/{category}/{date}")
    public ResponseEntity<ResponseModel> reportByDay (
        @PathVariable String category,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return new ResponseEntity<>(purchaseService.purchase(category, date), HttpStatus.CREATED);
    }

    //edit current, purchase item
    @PatchMapping(value = "/{id}")
    public ResponseEntity<ResponseModel> purchase(@PathVariable Long id, @RequestBody CategoryModel category) {
        category.setId(id);
        return new ResponseEntity<>(purchaseService.purchase(category), HttpStatus.OK);
    }

    // report by day sorted by category name
    @GetMapping("/reportbyday/{date}")
    public ResponseEntity<ResponseModel> reportByDay (@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return new ResponseEntity<>(purchaseService.reportByDay(date), HttpStatus.OK);
    }

    // report by month
    @GetMapping("/reportbymonth/{yearMonth}")
    public ResponseEntity<ResponseModel> reportByMonth (@PathVariable String yearMonth) {
        return new ResponseEntity<>(purchaseService.reportByMonth(yearMonth), HttpStatus.OK);
    }

}
