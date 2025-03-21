package tripleo.elijah.ci;

import tripleo.vendor.antlr277.Token;
import tripleo.elijah.lang.IExpression;

public interface LibraryStatementPart {

    String getName();

    void setName(Token i1);

    String getDirName();

    void setDirName(Token dirName);

    void addDirective(Token token, IExpression iExpression);

    CompilerInstructions getInstructions();

    void setInstructions(CompilerInstructions instructions);
}
