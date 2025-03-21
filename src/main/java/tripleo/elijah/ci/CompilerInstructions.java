package tripleo.elijah.ci;

import tripleo.vendor.antlr277.Token;

import java.util.List;

public interface CompilerInstructions {

    IndexingStatement indexingStatement();

    void add(GenerateStatement generateStatement);

    void add(LibraryStatementPartImpl libraryStatementPart);

    String getFilename();

    void setFilename(String filename);

    String genLang();

    String getName();

    void setName(String name);

    void setName(Token name);

    List<LibraryStatementPart> getLibraryStatementParts();
}
