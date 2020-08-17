package com.budgetbox.genericfood.dao.shared;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This annotation will enable a read-only connection (executed on the slave) without a transaction.
 * You can add this this annotation to a method or a class.
 * 
 * @author xfacq
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Transactional("readOnlyWithoutTransaction")
public @interface NonTransactionalReadOnly {

	@AliasFor(annotation = Transactional.class)
	String value() default "";

	@AliasFor(annotation = Transactional.class)
	String transactionManager() default "";

	@AliasFor(annotation = Transactional.class)
    Isolation isolation() default Isolation.DEFAULT;
     
    @AliasFor(annotation = Transactional.class)
    Propagation propagation() default Propagation.SUPPORTS;
 
    @AliasFor(annotation = Transactional.class)
    int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;
 
    @AliasFor(annotation = Transactional.class)
    boolean readOnly() default true;
 
    @AliasFor(annotation = Transactional.class)
    Class<? extends Throwable>[] rollbackFor() default {};
 
    @AliasFor(annotation = Transactional.class)
    String[] rollbackForClassName() default {};
 
    @AliasFor(annotation = Transactional.class)
    Class<? extends Throwable>[] noRollbackFor() default {};
 
    @AliasFor(annotation = Transactional.class)
    String[] noRollbackForClassName() default {};
}
