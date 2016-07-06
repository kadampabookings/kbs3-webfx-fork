package naga.core.orm.expressionsqlcompiler;

import naga.core.orm.expression.Expression;
import naga.core.orm.expression.term.*;
import naga.core.orm.expressionsqlcompiler.lci.CompilerDomainModelReader;
import naga.core.orm.expressionsqlcompiler.sql.DbmsSqlSyntaxOptions;
import naga.core.orm.expressionsqlcompiler.sql.SqlBuild;
import naga.core.orm.expressionsqlcompiler.sql.SqlClause;
import naga.core.orm.expressionsqlcompiler.sql.SqlCompiled;
import naga.core.orm.expressionsqlcompiler.term.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bruno Salmon
 */
public class ExpressionSqlCompiler {

    private static Map<Class<? extends Expression>, AbstractTermSqlCompiler<?>> termCompilers = new HashMap<>();

    static {
        // Registering all term compilers (some can actually compile several term classes).
        registerTermCompilers(
                new ExpressionArraySqlCompiler(),   // ExpressionArray
                new BinaryExpressionSqlCompiler(),  // Divide, Minus, Multiply, Plus
                new BooleanExpressionSqlCompiler(), // And, Equals, GreaterThan, GreaterThanOrEquals, In, LessThan, LessThanOrEquals, Like, NotEquals, NotLike, Or, All, Any
                new AliasSqlCompiler(),             // Alias, ArgumentAlias
                new CallSqlCompiler(),              // Call
                new DotSqlCompiler(),               // Dot
                new SelectExpressionSqlCompiler(),  // SelectExpression, Exists
                new TernaryExpressionSqlCompiler(), // TernaryExpression
                new UnaryExpressionSqlCompiler(),   // Array, As, Not
                new OrderedSqlCompiler(),           // Ordered
                new ConstantSqlCompiler(),          // Constant
                new ParameterSqlCompiler(),         // Parameter
                new IdSqlCompiler(),                // IdExpression
                new SymbolSqlCompiler()             // Symbol
        );
    }

    private static void registerTermCompilers(AbstractTermSqlCompiler<?>... termSqlCompilers) {
        for (AbstractTermSqlCompiler<?> expressionSqlCompiler : termSqlCompilers)
            for (Class<? extends Expression> expressionClass : expressionSqlCompiler.getSupportedTermClasses())
                termCompilers.put(expressionClass, expressionSqlCompiler);
    }

    /*** Public entry points ***/

    // Select compilation

    public static SqlCompiled compileSelect(Select select, Object[] parameterValues, DbmsSqlSyntaxOptions dbmsSyntax, boolean generateQueryMapping, boolean readForeignFields, CompilerDomainModelReader modelReader) {
        SqlBuild sqlBuild = buildSelect(select, parameterValues, dbmsSyntax, generateQueryMapping, readForeignFields, null, null, modelReader);
        return sqlBuild.toSqlCompiled();
    }

    // Insert compilation

    public static SqlCompiled compileInsert(Insert insert, Object[] parameterValues, DbmsSqlSyntaxOptions dbmsSyntax, CompilerDomainModelReader modelReader) {
        SqlBuild sqlBuild = buildInsert(insert, parameterValues, dbmsSyntax, modelReader);
        return sqlBuild.toSqlCompiled();
    }

    // Update compilation

    public static SqlCompiled compileUpdate(Update update, Object[] parameterValues, DbmsSqlSyntaxOptions dbmsSyntax, CompilerDomainModelReader modelReader) {
        SqlBuild sqlBuild = buildUpdate(update, parameterValues, dbmsSyntax, modelReader);
        return sqlBuild.toSqlCompiled();
    }

    // Delete compilation

    public static SqlCompiled compileDelete(Delete delete, Object[] parameterValues, DbmsSqlSyntaxOptions dbmsSyntax, CompilerDomainModelReader modelReader) {
        SqlBuild sqlBuild = buildDelete(delete, parameterValues, dbmsSyntax, modelReader);
        return sqlBuild.toSqlCompiled();
    }


     /*** Private entry points ***/

     // Select compilation

    public static SqlBuild buildSelect(Select select, Options parentOptions) {
        Options o = parentOptions;
        return buildSelect(select, null /*o.parameterValues - can it disappear?*/, o.build.getDbmsSyntax(), o.generateQueryMapping, o.readForeignFields, o.build, o.clause, o.modelReader);
    }

    public static SqlBuild buildSelect(Select select, Object[] parameterValues, DbmsSqlSyntaxOptions dbmsSyntax, boolean generateQueryMapping, boolean readForeignFields, SqlBuild parent, SqlClause parentClause, CompilerDomainModelReader modelReader) {
        SqlBuild sqlBuild = createSqlOrderBuild(select, SqlClause.SELECT, dbmsSyntax, parent, modelReader);
        sqlBuild.setDistinct(select.isDistinct());
        boolean grouped = select.getGroupBy() != null;
        if (select.isIncludeIdColumn())
            sqlBuild.addColumnInClause(sqlBuild.getTableAlias(), modelReader.getDomainClassPrimaryKeySqlColumnName(select.getDomainClass()), null, null, SqlClause.SELECT, "", grouped, false, true);
        if (select.getFields() != null)
            compileExpression(select.getFields(), new Options(sqlBuild, SqlClause.SELECT, ", ", parameterValues, grouped, generateQueryMapping, readForeignFields, modelReader));
        if (select.getGroupBy() != null)
            compileExpression(select.getGroupBy(), new Options(sqlBuild, SqlClause.GROUP_BY, ", ", parameterValues, grouped, false, false, modelReader));
        if (select.getHaving() != null)
            compileExpression(select.getHaving(), new Options(sqlBuild, SqlClause.HAVING, ", ", parameterValues, grouped, false, false, modelReader));
        return buildCommonSqlOrder(select, parameterValues, sqlBuild, grouped, dbmsSyntax, parent, parentClause, modelReader);
    }

    private static SqlBuild createSqlOrderBuild(SqlOrder sqlOrder, SqlClause sqlClause, DbmsSqlSyntaxOptions dbmsSyntax, SqlBuild parent, CompilerDomainModelReader modelReader) {
        return new SqlBuild(parent, sqlOrder.getDomainClass(), sqlOrder.getDomainClassAlias(), sqlClause, dbmsSyntax, modelReader);
    }

    private static SqlBuild buildCommonSqlOrder(SqlOrder sqlOrder, Object[] parameterValues, SqlBuild sqlBuild, boolean grouped, DbmsSqlSyntaxOptions dbmsSyntax, SqlBuild parent, SqlClause parentClause, CompilerDomainModelReader modelReader) {
        if (sqlOrder.getWhere() != null)
            compileExpression(sqlOrder.getWhere(), new Options(sqlBuild, SqlClause.WHERE, null, parameterValues, grouped, false, false, modelReader));
        if (sqlOrder.getOrderBy() != null)
            compileExpression(sqlOrder.getOrderBy(), new Options(sqlBuild, SqlClause.ORDER_BY, ", ", parameterValues, grouped, false, false, modelReader));
        if (sqlOrder.getLimit() != null && dbmsSyntax != DbmsSqlSyntaxOptions.HSQL_SYNTAX) // temporary fix
            compileExpression(sqlOrder.getLimit(), new Options(sqlBuild, SqlClause.LIMIT, null, parameterValues, grouped, false, false, modelReader));
        if (parent != null)
            parent.prepareAppend(parentClause, null).append(sqlBuild.toSql()); // is it the right way?
        sqlOrder.setCacheable(sqlBuild.isCacheable());
        return sqlBuild;
    }

    // Update compilation

    private static SqlBuild buildInsert(Insert insert, Object[] parameterValues, DbmsSqlSyntaxOptions dbmsSyntax, CompilerDomainModelReader modelReader) {
        SqlBuild sqlBuild = createSqlOrderBuild(insert, SqlClause.INSERT, dbmsSyntax, null, modelReader);
        sqlBuild.setAutoGeneratedKeyColumnNames(new String[]{modelReader.getDomainClassPrimaryKeySqlColumnName(insert.getDomainClass())});
        for (Expression expression : insert.getSetClause().getExpressions()) {
            Equals equals = (Equals) expression;
            compileExpression(equals.getLeft(), new Options(sqlBuild, SqlClause.INSERT, ", ", parameterValues, false, false, false, modelReader));
            compileExpression(equals.getRight(), new Options(sqlBuild, SqlClause.VALUES, ", ", parameterValues, false, false, false, modelReader));
        }
        return buildCommonSqlOrder(insert, parameterValues, sqlBuild, false, dbmsSyntax, null, null, modelReader);
    }

    // Update compilation

    private static SqlBuild buildUpdate(Update update, Object[] parameterValues, DbmsSqlSyntaxOptions dbmsSyntax, CompilerDomainModelReader modelReader) {
        SqlBuild sqlBuild = createSqlOrderBuild(update, SqlClause.UPDATE, dbmsSyntax, null, modelReader);
        compileExpression(update.getSetClause(), new Options(sqlBuild, SqlClause.UPDATE, ", ", parameterValues, false, false, false, modelReader));
        return buildCommonSqlOrder(update, parameterValues, sqlBuild, false, dbmsSyntax, null, null, modelReader);
    }

    // Delete compilation

    private static SqlBuild buildDelete(Delete delete, Object[] parameterValues, DbmsSqlSyntaxOptions dbmsSyntax, CompilerDomainModelReader modelReader) {
        SqlBuild sqlBuild = createSqlOrderBuild(delete, SqlClause.DELETE, dbmsSyntax, null, modelReader);
        return buildCommonSqlOrder(delete, parameterValues, sqlBuild, false, dbmsSyntax, null, null, modelReader);
    }

    // Expression compilation

    public static void compileExpression(Expression expression, Options options) {
        Class<? extends Expression> expressionClass = expression.getClass();
        AbstractTermSqlCompiler termSqlCompiler = termCompilers.get(expressionClass);
        if (termSqlCompiler == null) {
            /* J2ME CLDC
            // trying to find the compiler of a super class (ex: DomainField compiler is actually a Term compiler)
            for (Class superClass = expressionClass.getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
                termSqlCompiler = termCompilers.get(superClass);
                if (termSqlCompiler != null) {
                    termCompilers.put(expressionClass, termSqlCompiler);
                    break;
                }
            }
            if (termSqlCompiler == null) */
            throw new IllegalArgumentException("Sql not compilable: " + expression);
        }
        termSqlCompiler.compileExpressionToSql(expression, options);
    }

    /*** Helper methods ***/

    public static String toSqlString(String name) {
        if (name == null || name.length() < 2)
            return name;
        StringBuilder sb = new StringBuilder();
        boolean underscoreAllowed = false;
        int i0 = 0, i = 1;
        for (; i < name.length(); i++) {
            if (Character.isUpperCase(name.charAt(i))) {
                if (underscoreAllowed)
                    sb.append('_');
                for (int j = i0; j < i; j++)
                    sb.append(Character.toLowerCase(name.charAt(j)));
                underscoreAllowed = i > i0 + 1; // no underscore between two consecutive uppercase
                i0 = i;
            }
        }
        if (underscoreAllowed)
            sb.append('_');
        for (int j = i0; j < i; j++)
            sb.append(Character.toLowerCase(name.charAt(j)));
        return sb.toString();
    }

}
