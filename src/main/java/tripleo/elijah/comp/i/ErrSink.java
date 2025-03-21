/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.comp.i;

import tripleo.elijah.comp.StdErrSink;
import tripleo.elijah_fluffy.diagnostic.ElDiagnostic;
import tripleo.vendor.org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface ErrSink {

    void exception(Throwable exception);

    /* @ ensures errorCount() == \old errorCount + 1 */
    void reportError(String s);

    void reportWarning(String s);

    int errorCount();

    void info(String format);

    void reportDiagnostic(ElDiagnostic diagnostic);

    Pair<StdErrSink.Desc, Object> _error(int index);

    List<Pair<StdErrSink.Desc, Object>> _errors();

    void PrintErrors();

    enum Errors {
        ERROR,
        WARNING,
        INFO
    }

    enum Desc {
        INFO_STRING,
        WARNING_STRING,
        EXCEPTION_STRING,
        DIAGNOSTIC
    }
}

//
//
//
