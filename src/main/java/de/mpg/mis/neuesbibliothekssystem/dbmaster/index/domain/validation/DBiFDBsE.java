package de.mpg.mis.neuesbibliothekssystem.dbmaster.index.domain.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import de.mpg.mis.neuesbibliothekssystem.dbmaster.index.domain.DBs;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;

/**
 * The annotated element must have corresponding DBsEs and a DBiF
 * 
 * @see de.de.mpg.mis.neuesbibliothekssystem.dbmaster.domain.de.mpg.mis.neuesbibliothekssystem.dbmaster.domain.DBMaster.domain.DBs
 * @author Tobias Kaatz
 */
@Documented
@ReportAsSingleViolation
@Constraint(validatedBy = DBiFDBsE.DBiFDBsEValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface DBiFDBsE {

    String message() default "DBsEs und DBiF passen nicht zueinander";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
	DBiFDBsE[] value();
    }

    class DBiFDBsEValidator implements ConstraintValidator<DBiFDBsE, DBs> {
	private String verifyField;
	private ExpressionParser parser;

	public void initialize(DBiFDBsE constraintAnnotation) {
	    parser = new SpelExpressionParser();
	}

	public boolean isValid(DBs value, ConstraintValidatorContext context) {
	    StandardEvaluationContext spelContext = new StandardEvaluationContext(
		    value);

	    boolean matches = parser.parseExpression(
		    "dbif.d == dbsed.i and dbif.g == dbseg.i").getValue(
		    spelContext, Boolean.class);

	    if (!matches) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate("message")
			.addNode(verifyField).addConstraintViolation();
	    }

	    return matches;
	}
    }

}
