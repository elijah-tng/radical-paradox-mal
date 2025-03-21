/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.comp;

import tripleo.elijah.comp.i.ErrSink;
import tripleo.elijah_fluffy.diagnostic.ElDiagnostic;
import tripleo.elijah_fluffy.util.SimplePrintLoggerToRemoveSoon;
import tripleo.vendor.org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created Mar 25, 2019 at 3:00:39 PM
 *
 * @author tripleo(sb)
 */
public class StdErrSink implements ErrSink {
    private final ErrorList errorList = new ErrorList();
    private int _errorCount;

    @Override
    public void exception(final Throwable e) {
        _errorCount++;
        SimplePrintLoggerToRemoveSoon.println_err2("exception: " + e);
        e.printStackTrace(System.err);
        errorList.addException(e);
    }

    @Override
    public void reportError(final String message) {
        _errorCount++;
        if (CompilationAlways.VOODOO) {
            final var s = String.format("ERROR: %s%n", message);
            SimplePrintLoggerToRemoveSoon.println_err2(s);
        }
        errorList.addWarning(message);
    }

    @Override
    public void reportWarning(final String message) {
        if (CompilationAlways.VOODOO) {
            final var s = String.format("WARNING: %s%n", message);
            SimplePrintLoggerToRemoveSoon.println_err2(s);
        }
        errorList.addWarning(message);
    }

    @Override
    public int errorCount() {
        return _errorCount;
    }

    @Override
    public void info(final String message) {
        if (CompilationAlways.VOODOO) {
            final var s = String.format("INFO: %s%n", message);
            SimplePrintLoggerToRemoveSoon.println_err2(s);
        }
        errorList.addInfo(message);
    }

    @Override
    public void reportDiagnostic(final ElDiagnostic diagnostic) {
        if (diagnostic.severity() == ElDiagnostic.Severity.ERROR) {
            _errorCount++;
        }
        if (CompilationAlways.VOODOO) {
            diagnostic.report(System.err);
        }
        errorList.addDiagnostic(diagnostic);
    }

    @Override
    public Pair<Desc, Object> _error(final int index) {
        return errorList.get(index - 1);
    }

    @Override
    public List<Pair<Desc, Object>> _errors() {
        return errorList.__errors();
    }

    @Override
    public void PrintErrors() {
        for (final Pair<Desc, Object> error : _errors()) {
            switch (error.getLeft()) {
                case INFO_STRING -> System.err.println("INFO_STRING " + error.getRight());
                case WARNING_STRING -> System.err.println("WARNING_STRING " + error.getRight());
                case EXCEPTION_STRING -> System.err.println("EXCEPTION_STRING " + error.getRight());
                case DIAGNOSTIC -> ((ElDiagnostic) error.getRight()).report(System.err);
                default -> throw new IllegalStateException("Unexpected value: " + error.getLeft());
            }
        }
    }

    public static class ErrorList {
        List<Pair<Desc, Object>> backing = new ArrayList<>();

        public void addDiagnostic(final ElDiagnostic aDiagnostic) {
            backing.add(Pair.of(Desc.DIAGNOSTIC, aDiagnostic));
        }

        public void addInfo(final String aMessage) {
            backing.add(Pair.of(Desc.INFO_STRING, aMessage));
        }

        public void addWarning(final String aMessage) {
            backing.add(Pair.of(Desc.WARNING_STRING, aMessage));
        }

        public void addException(final Throwable aE) {
            backing.add(Pair.of(Desc.EXCEPTION_STRING, "" + aE));
        }

        public Pair<Desc, Object> get(final int index) {
            return backing.get(index);
        }

        public List<Pair<Desc, Object>> __errors() {
            return backing;
        }
    }
}

//
//
//
