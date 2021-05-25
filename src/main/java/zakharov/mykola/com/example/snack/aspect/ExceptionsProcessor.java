package zakharov.mykola.com.example.snack.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.annotation.Configuration;
import zakharov.mykola.com.example.snack.model.ResponseModel;
import zakharov.mykola.com.example.snack.util.ErrorsGetter;

@Configuration
@Aspect
public class ExceptionsProcessor {
    @Around("execution(* zakharov.mykola.com.example.snack.dao.*.*(..))")
    public Object onDaoException(ProceedingJoinPoint pjp) throws Exception {
        Object output = null;
        try {
            output = pjp.proceed();
        } catch (Exception ex) {
            System.out.println("sql error is " + ErrorsGetter.getException(ex));
            throw ex;
        } catch (Throwable throwable) {
            System.out.println("sql error is: ");
            throwable.printStackTrace();
        }
        return output;
    }

    @Around("execution(* zakharov.mykola.com.example.snack.service.*.*(..))")
    public Object onServiceException(ProceedingJoinPoint pjp) {
        Object output = null;
        try {
            output = pjp.proceed();
        } catch (ConstraintViolationException ex) {
            System.out.println("my service error");
            output =
                    ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message((ex.getMessage() != null ? ex.getMessage() : "Constraint violation"))
                            .build();
        } catch (Exception ex) {
            output =
                    ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Unknown database error")
                            .build();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return output;
    }
}
