package webfx.framework.shared.expression.terms;

import webfx.framework.shared.expression.Expression;
import webfx.framework.shared.expression.lci.DataReader;
import webfx.framework.shared.expression.lci.DataWriter;
import webfx.framework.shared.expression.terms.function.Call;
import webfx.framework.shared.expression.terms.function.Function;
import webfx.platform.shared.util.collection.HashList;

import java.util.Collection;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class Dot<T> extends BinaryExpression<T> {

    private final boolean outerJoin;
    private final boolean readLeftKey;

    public Dot(Expression<T> left, Expression<T> right) {
        this(left, right, false);
    }

    public Dot(Expression<T> left, Expression<T> right, boolean outerJoin) {
        this(left, right, outerJoin, true);
    }

    public Dot(Expression<T> left, Expression<T> right, boolean outerJoin, boolean readLeftKey) {
        super(left, outerJoin ? ".." : ".", right, 8);
        this.outerJoin = outerJoin;
        this.readLeftKey = readLeftKey;
    }

    public boolean isOuterJoin() {
        return outerJoin;
    }

    public boolean isReadLeftKey() {
        return readLeftKey;
    }

    @Override
    public Expression<T> getForwardingTypeExpression() {
        return right;
    }

    @Override
    public Object evaluate(T domainObject, DataReader<T> dataReader) {
        Object leftValue = left.evaluate(domainObject, dataReader);
        if (leftValue == null)
            return null;
        T rightData = dataReader.getDomainObjectFromId(leftValue, domainObject);
        return right.evaluate(rightData, dataReader);
    }

    @Override
    public Object evaluate(Object leftValue, Object rightValue, DataReader<T> dataReader) {
        return null; // never called due to above evaluate method override
    }

    @Override
    public void setValue(T domainObject, Object value, DataWriter<T> dataWriter) {
        Object leftValue = left.evaluate(domainObject, dataWriter);
        if (leftValue != null) {
            T rightData = dataWriter.getDomainObjectFromId(leftValue, domainObject);
            right.setValue(rightData, value, dataWriter);
        }
    }

    public void collectPersistentTerms(Collection<Expression<T>> persistentTerms) {
        List<Expression<T>> rightPersistentTerms = new HashList<>();
        right.collectPersistentTerms(rightPersistentTerms);
        if (!rightPersistentTerms.isEmpty()) {
            Dot<T> persistentDot;
            if (rightPersistentTerms.size() != 1)
                persistentDot = new Dot<>(left, new ExpressionArray<>(rightPersistentTerms), outerJoin);
            else if (rightPersistentTerms.get(0) == right)
                persistentDot = this;
            else
                persistentDot = new Dot<>(left, rightPersistentTerms.get(0), outerJoin);
            Expression<T> expandLeft = persistentDot.expandLeft();
            if (expandLeft == persistentDot)
                persistentTerms.add(persistentDot);
            else
                expandLeft.collectPersistentTerms(persistentTerms);
        }
    }

    public Expression<T> expandLeft() {
        if (left instanceof Call) {
            Call<T> call = (Call<T>) this.left;
            Function function = call.getFunction();
            if (function.isIdentity())
                return new Call<>(function.getName(), new Dot<>(call.getOperand(), getRight(), isOuterJoin()).expandLeft(), call.getOrderBy());
        }
        if (left instanceof Dot) {
            Dot<T> leftDot = (Dot<T>) left;
            return new Dot<>(leftDot.getLeft(), new Dot<>(leftDot.getRight(), getRight(), isOuterJoin()), leftDot.isOuterJoin()).expandLeft();
        }
        Expression<T> leftForwardingTypeExpression = left.getForwardingTypeExpression();
        if (leftForwardingTypeExpression == left)
            return this;
        if (leftForwardingTypeExpression instanceof Dot) {
            Dot<T> leftDot = (Dot<T>) leftForwardingTypeExpression;
            return new Dot<>(leftDot.getLeft(), new Dot<>(leftDot.getRight(), getRight(), isOuterJoin()), leftDot.isOuterJoin()).expandLeft();
        }
        if (leftForwardingTypeExpression instanceof TernaryExpression) {
            TernaryExpression<T> leftTernaryExpression = (TernaryExpression<T>) leftForwardingTypeExpression;
            return new TernaryExpression<T>(leftTernaryExpression.getQuestion(), new Dot<>(leftTernaryExpression.getYes(), getRight(), isOuterJoin()).expandLeft(), new Dot(leftTernaryExpression.getNo(), getRight(), isOuterJoin()).expandLeft());
        }
        return this;
    }
}
