package tripleo.elijah.lang;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.lang2.ElElementVisitor;
import tripleo.elijah_fluffy.diagnostic.ElLocatable;
import tripleo.elijah_fluffy.util.Helpers;
import tripleo.elijah_fluffy.util.NotImplementedException;
import tripleo.elijah_fluffy.util.SimplePrintLoggerToRemoveSoon;
import tripleo.vendor.antlr277.Token;

import java.io.File;

/**
 * @author Tripleo(sb)
 *
 */
public class IdentExpression implements IExpression, OS_Element, Resolvable, ElLocatable {

    public final Attached _a;
    private final Token text;
    OS_Type _type;
    private OS_Element _resolvedElement;

    public IdentExpression(final Token r1) {
        this.text = r1;
        this._a = new Attached();
    }

    public IdentExpression(final Token r1, final Context cur) {
        this.text = r1;
        this._a = new Attached();
        setContext(cur);
    }

    @Contract("_ -> new")
    public static @NotNull IdentExpression forString(final String string) {
        return new IdentExpression(Helpers.makeToken(string));
    }

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.IDENT;
    }

    @Override
    public void setKind(final ExpressionKind aIncrement) {
        // log and ignore
        SimplePrintLoggerToRemoveSoon.println_err2(
                "Trying to set ExpressionType of IdentExpression to " + aIncrement.toString());
    }

    @Override
    public IExpression getLeft() {
        return this;
    }

    @Override
    public void setLeft(final @NotNull IExpression iexpression) {
        //		if (iexpression instanceof IdentExpression) {
        //			text = ((IdentExpression) iexpression).text;
        //		} else {
        //			// NOTE was tripleo.elijah.util.Stupidity.println_err2
        throw new IllegalArgumentException("Trying to set left-side of IdentExpression to " + iexpression);
        //		}
    }

    @Override
    public String repr_() {
        return String.format("IdentExpression(%s %d)", text.getText(), _a.getCode());
    }

    @Override
    public boolean is_simple() {
        return true;
    }

    @Override
    public OS_Type getType() {
        return _type;
    }

    @Override
    public void setType(final OS_Type deducedExpression) {
        _type = deducedExpression;
    }

    /**
     * same as getText()
     */
    @Override
    public String toString() {
        return getText();
    }

    public String getText() {
        return text.getText();
    }

    @Override
    public void visitGen(final ElElementVisitor visit) {
        visit.visitIdentExpression(this);
    }

    @Override
    public Context getContext() {
        return _a.getContext();
    }

    public void setContext(final Context cur) {
        _a.setContext(cur);
    }

    @Override
    public OS_Element getParent() {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
        //		return null;
    }

    @Override
    public boolean hasResolvedElement() {
        return _resolvedElement != null;
    }

    @Override
    public OS_Element getResolvedElement() {
        return _resolvedElement;
    }

    @Override
    public void setResolvedElement(final OS_Element element) {
        _resolvedElement = element;
    }

    @Override
    public int getLine() {
        return token().getLine();
    }

    // region Locatable

    public Token token() {
        return text;
    }

    @Override
    public int getColumn() {
        return token().getColumn();
    }

    @Override
    public int getLineEnd() {
        return token().getLine();
    }

    @Override
    public int getColumnEnd() {
        return token().getColumn();
    }

    @Override
    public File getFile() {
        final String filename = token().getFilename();
        if (filename == null) return null;
        return new File(filename);
    }

    // endregion

    public boolean sameName(final String otherName) {
        return this.text.getText().equals(otherName);
    }

    public boolean sameName(final OS_Element2 aNamed) {
        return sameName(aNamed.name());
    }
}
