package webfx.framework.expression.builder.terms;

import webfx.framework.expression.terms.Constant;

/**
 * @author Bruno Salmon
 */
public class ConstantBuilder extends ExpressionBuilder {

    public Object constantValue;

    public ConstantBuilder(Object constantValue) {
        this.constantValue = constantValue;
    }

    @Override
    public Constant build() {
        return Constant.newConstant(constantValue);
    }
}