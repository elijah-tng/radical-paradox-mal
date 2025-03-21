/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.stages.gen_fn;

import org.jdeferred2.DoneCallback;
import org.jdeferred2.Promise;
import org.jdeferred2.impl.DeferredObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tripleo.elijah.comp.i.ErrSink;
import tripleo.elijah.lang.Context;
import tripleo.elijah.lang.IExpression;
import tripleo.elijah.lang.OS_Element;
import tripleo.elijah.lang.OS_Type;
import tripleo.elijah.stages.deduce.ClassInvocation;
import tripleo.elijah.stages.deduce.DeduceProcCall;
import tripleo.elijah.stages.deduce.DeduceTypes2;
import tripleo.elijah.stages.deduce.FunctionInvocation;
import tripleo.elijah.stages.deduce.post_bytecode.DeduceElement3_ProcTableEntry;
import tripleo.elijah.stages.deduce.post_bytecode.IDeduceElement3;
import tripleo.elijah.stages.deduce.zero.PTE_Zero;
import tripleo.elijah.stages.instructions.InstructionArgument;
import tripleo.elijah.stages.logging.ElLog;
import tripleo.elijah_fluffy.util.Eventual;
import tripleo.elijah_fluffy.util.Helpers;
import tripleo.elijah_fluffy.util.NotImplementedException;
import tripleo.elijah_fluffy.util.SimplePrintLoggerToRemoveSoon;
import tripleo.elijah_prolific.deduce.DT_Element3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created 9/12/20 10:07 PM
 */
public class ProcTableEntry extends BaseTableEntry implements TableEntryIV {
    public final int index;
    public final List<TypeTableEntry> args;
    /**
     * Either a hint to the programmer-- The compiler should be able to work without
     * this. <br/>
     * Or for synthetic methods
     */
    public final IExpression expression;

    public final InstructionArgument expression_num;
    public final DeduceProcCall dpc = new DeduceProcCall(this);
    private final DeferredObject<ProcTableEntry, Void, Void> completeDeferred = new DeferredObject<>();
    private final DeferredObject2<FunctionInvocation, Void, Void> onFunctionInvocations = new DeferredObject2<>();
    private final DeferredObject<GenType, Void, Void> typeDeferred = new DeferredObject<>();
    private final Eventual<DT_Element3> _p_resolvedElement = new Eventual<>();
    private ClassInvocation classInvocation;
    private FunctionInvocation functionInvocation;
    private DeduceElement3_ProcTableEntry _de3;
    private PTE_Zero _zero;

    public ProcTableEntry(
            final int aIndex,
            final IExpression aExpression,
            final InstructionArgument aExpressionNum,
            final List<TypeTableEntry> aArgs) {
        index = aIndex;
        expression = aExpression;
        expression_num = aExpressionNum;
        args = aArgs;

        addStatusListener(new StatusListener() {
            @Override
            public void onChange(final IElementHolder eh, final Status newStatus) {
                if (newStatus == Status.KNOWN) {
                    setResolvedElement(eh.getElement());
                }
            }
        });

        for (final TypeTableEntry tte : args) {
            tte.addSetAttached(new TypeTableEntry.OnSetAttached() {
                @Override
                public void onSetAttached(final TypeTableEntry aTypeTableEntry) {
                    ProcTableEntry.this.onSetAttached();
                }
            });
        }

        elementPromise(p -> _p_resolvedElement.resolve(new __DT_Element3(p)), d -> _p_resolvedElement.reject(d));

        setupResolve();
    }

    public void onSetAttached() {
        int state = 0;
        if (args != null) {
            final int ac = args.size();
            int acx = 0;
            for (final TypeTableEntry tte : args) {
                if (tte.getAttached() != null) acx++;
            }
            if (acx < ac) {
                state = 1;
            } else if (acx > ac) {
                state = 2;
            } else if (acx == ac) {
                state = 3;
            }
        } else {
            state = 3;
        }
        switch (state) {
            case 0:
                throw new IllegalStateException();
            case 1:
                SimplePrintLoggerToRemoveSoon.println_err2("136 pte not finished resolving " + this);
                break;
            case 2:
                SimplePrintLoggerToRemoveSoon.println_err2("138 Internal compiler error");
                break;
            case 3:
                if (completeDeferred.isPending()) completeDeferred.resolve(this);
                break;
            default:
                throw new NotImplementedException();
        }
    }

    public void setArgType(final int aIndex, final OS_Type aType) {
        args.get(aIndex).setAttached(aType);
    }

    public ClassInvocation getClassInvocation() {
        return classInvocation;
    }

    public void setClassInvocation(final ClassInvocation aClassInvocation) {
        classInvocation = aClassInvocation;
    }

    @Override
    @NotNull
    public String toString() {
        return "ProcTableEntry{" + "index=" + index + ", expression=" + expression + ", expression_num="
                + expression_num + ", args=" + args + '}';
    }

    // have no idea what this is for
    public void onFunctionInvocation(final DoneCallback<FunctionInvocation> callback) {
        onFunctionInvocations.then(callback);
    }

    public DeferredObject<GenType, Void, Void> typeDeferred() {
        return typeDeferred;
    }

    public Promise<GenType, Void, Void> typePromise() {
        return typeDeferred.promise();
    }

    public FunctionInvocation getFunctionInvocation() {
        return functionInvocation;
    }

    // have no idea what this is for
    public void setFunctionInvocation(final FunctionInvocation aFunctionInvocation) {
        if (functionInvocation != null && functionInvocation.sameAs(aFunctionInvocation))
            return; // short circuit for better behavior
        // ABOVE 2b
        if (functionInvocation != aFunctionInvocation) {
            functionInvocation = aFunctionInvocation;
            onFunctionInvocations.reset();
            onFunctionInvocations.resolve(functionInvocation);
        }
    }

    public DeduceProcCall deduceProcCall() {
        return dpc;
    }

    @NotNull
    public String getLoggingString(final @Nullable DeduceTypes2 aDeduceTypes2, final ElLog LOG) {
        final String pte_string;
        @NotNull final List<String> l = new ArrayList<String>();

        for (@NotNull final TypeTableEntry typeTableEntry : getArgs()) {
            final OS_Type attached = typeTableEntry.getAttached();

            if (attached != null) l.add(attached.toString());
            else {
                //				if (aDeduceTypes2 != null)
                //					LOG.err("267 attached == null for "+typeTableEntry);

                if (typeTableEntry.expression != null)
                    l.add(String.format("<Unknown expression: %s>", typeTableEntry.expression));
                else l.add("<Unknkown>");
            }
        }

        final String sb2 = "[" + Helpers.String_join(", ", l) + "]";
        pte_string = sb2;
        return pte_string;
    }

    public List<TypeTableEntry> getArgs() {
        return args;
    }

    public void setDeduceTypes2(
            final DeduceTypes2 aDeduceTypes2,
            final Context aContext,
            final BaseGeneratedFunction aGeneratedFunction,
            final ErrSink aErrSink) {
        _p_resolvedElement.then(xx -> {
            xx.setDeduceTypes2(aDeduceTypes2);
            xx.setContext(aContext);
            xx.setGeneratedFunction(aGeneratedFunction);
            xx.setErrSink(aErrSink);
        });
        dpc.setDeduceTypes2(aDeduceTypes2, aContext, aGeneratedFunction, aErrSink);
    }

    public IDeduceElement3 getDeduceElement3() {
        assert dpc._deduceTypes2() != null; // TODO setDeduce... called; Promise?

        return getDeduceElement3(dpc._deduceTypes2(), dpc._generatedFunction());
    }

    public IDeduceElement3 getDeduceElement3(
            final DeduceTypes2 aDeduceTypes2, final BaseGeneratedFunction aGeneratedFunction) {
        if (_de3 == null) {
            _de3 = new DeduceElement3_ProcTableEntry(this, aDeduceTypes2, aGeneratedFunction);
            //			_de3.
        }
        return _de3;
    }

    public PTE_Zero zero() {
        if (_zero == null) _zero = new PTE_Zero(this);

        return _zero;
    }

    public void onResolvedElement(final Consumer<DT_Element3> cb) {
        _p_resolvedElement.then(cb::accept);
    }

    private static class __DT_Element3 implements DT_Element3 {
        private final OS_Element p;
        private DeduceTypes2 deduceTypes2;
        private BaseGeneratedFunction generatedFunction;
        private ErrSink errSink;

        public __DT_Element3(final OS_Element aP) {
            p = aP;
        }

        @Override
        public OS_Element getResolvedElement() {
            return p;
        }

        @Override
        public void setContext(final Context aContext) {}

        @Override
        public void op_fail(final DTEL aDTEL) {
            System.err.println("{{DTEL}} " + aDTEL.name());
        }

        @Override
        public ErrSink getErrSink() {
            return errSink;
        }

        @Override
        public void setErrSink(final ErrSink aErrSink) {
            errSink = aErrSink;
        }

        public BaseGeneratedFunction getGeneratedFunction() {
            return generatedFunction;
        }

        @Override
        public void setGeneratedFunction(final BaseGeneratedFunction aGeneratedFunction) {
            this.generatedFunction = aGeneratedFunction;
        }

        public DeduceTypes2 getDeduceTypes2() {
            return deduceTypes2;
        }

        @Override
        public void setDeduceTypes2(final DeduceTypes2 aDeduceTypes2) {
            if (deduceTypes2 == null) {
                deduceTypes2 = aDeduceTypes2;
            }
        }
    }
}

//
//
//
