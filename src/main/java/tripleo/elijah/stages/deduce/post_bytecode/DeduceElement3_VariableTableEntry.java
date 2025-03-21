package tripleo.elijah.stages.deduce.post_bytecode;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.jdeferred2.DoneCallback;
import org.jdeferred2.Promise;
import org.jdeferred2.impl.DeferredObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tripleo.elijah.comp.i.ErrSink;
import tripleo.elijah.lang.*;
import tripleo.elijah.lang.types.OS_UserType;
import tripleo.elijah.stages.deduce.*;
import tripleo.elijah.stages.deduce.post_bytecode.DED.DED_VTE;
import tripleo.elijah.stages.gen_fn.*;
import tripleo.elijah.stages.instructions.IdentIA;
import tripleo.elijah.stages.instructions.InstructionArgument;
import tripleo.elijah.stages.instructions.VariableTableType;
import tripleo.elijah.stages.logging.ElLog;
import tripleo.elijah.util.Operation2;
import tripleo.elijah_fluffy.diagnostic.ElDiagnostic;
import tripleo.elijah_fluffy.diagnostic.ElLocatable;
import tripleo.elijah_fluffy.util.Eventual;
import tripleo.elijah_fluffy.util.NotImplementedException;
import tripleo.vendor.org.apache.commons.lang3.tuple.ImmutablePair;
import tripleo.vendor.org.apache.commons.lang3.tuple.Pair;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static tripleo.elijah.stages.deduce.DeduceTypes2.to_int;

public class DeduceElement3_VariableTableEntry extends DefaultStateful implements IDeduceElement3 {
    private final VariableTableEntry principal;
    private final State st;
    private DeduceTypes2 deduceTypes2;
    private BaseGeneratedFunction generatedFunction;
    private GenType genType;

    @Contract(pure = true)
    public DeduceElement3_VariableTableEntry(final VariableTableEntry aVariableTableEntry) {
        principal = aVariableTableEntry;
        st = ST.INITIAL;
    }

    @NotNull
    private static ArrayList<TypeTableEntry> getPotentialTypesVte(
            @NotNull final GeneratedFunction generatedFunction, @NotNull final InstructionArgument vte_index) {
        return getPotentialTypesVte(generatedFunction.getVarTableEntry(to_int(vte_index)));
    }

    @NotNull
    static ArrayList<TypeTableEntry> getPotentialTypesVte(@NotNull final VariableTableEntry vte) {
        return new ArrayList<>(vte.potentialTypes());
    }

    @Override
    public void resolve(final IdentIA aIdentIA, final Context aContext, final FoundElement aFoundElement) {
        throw new UnsupportedOperationException("Should not be reached");
    }

    @Override
    public void resolve(final Context aContext, final DeduceTypes2 aDeduceTypes2) {
        throw new UnsupportedOperationException("Should not be reached");
    }

    @Override
    public OS_Element getPrincipal() {
        return principal.getResolvedElement();
    }

    @Override
    public DED elementDiscriminator() {
        return new DED_VTE(principal);
    }

    @Override
    public DeduceTypes2 deduceTypes2() {
        return deduceTypes2;
    }

    @Override
    public BaseGeneratedFunction generatedFunction() {
        return generatedFunction;
    }

    @Override
    public GenType genType() {
        // return genType;
        throw new AssertionError("UUE");
    }

    @Override
    public DeduceElement3_Kind kind() {
        return DeduceElement3_Kind.GEN_FN__VTE;
    }

    public Operation2<OS_Type> decl_test_001(final BaseGeneratedFunction gf) {
        final VariableTableEntry vte = principal;

        final OS_Type x = vte.type.getAttached();
        if (x == null && vte.potentialTypes().isEmpty()) {
            final ElDiagnostic diag;
            if (vte.vtt == VariableTableType.TEMP) {
                diag = new Diagnostic_8884(vte, gf);
            } else {
                diag = new Diagnostic_8885(vte);
            }
            return Operation2.failure(diag);
        }

        if (x == null) {
            return Operation2.failure(new GCFM_Diagnostic() {
                @Override
                public String _message() {
                    return "113/133 x is null";
                }

                @Override
                public String code() {
                    return "133";
                }

                @Override
                public Severity severity() {
                    return Severity.INFO;
                }

                @Override
                public @NotNull ElLocatable primary() {
                    return null;
                }

                @Override
                public @NotNull List<ElLocatable> secondary() {
                    return null;
                }

                @Override
                public void report(final PrintStream stream) {
                    stream.printf("%s %s%n", code(), _message());
                }
            });
        }

        return Operation2.success(x);
    }

    public void setDeduceTypes2(final DeduceTypes2 aDeduceTypes2, final BaseGeneratedFunction aGeneratedFunction) {
        deduceTypes2 = aDeduceTypes2;
        generatedFunction = aGeneratedFunction;
    }

    public void potentialTypesRunnableDo(
            final @Nullable InstructionArgument vte_ia,
            final @NotNull ElLog aLOG,
            final @NotNull VariableTableEntry aVte1,
            final ErrSink errSink,
            final Context ctx,
            final String aE_text,
            final @NotNull VariableTableEntry aVte) {
        assert vte_ia != null;
        final @NotNull List<TypeTableEntry> ll = getPotentialTypesVte((GeneratedFunction) generatedFunction, vte_ia);
        doLogic(ll, aVte1.typePromise(), aLOG, aVte1, errSink, ctx, aE_text, aVte);
    }

    public void doLogic(
            @NotNull final List<TypeTableEntry> potentialTypes,
            final Promise<GenType, Void, Void> p,
            final @NotNull ElLog LOG,
            final @NotNull VariableTableEntry vte1,
            final ErrSink errSink,
            final Context ctx,
            final String e_text,
            final @NotNull VariableTableEntry vte) {
        // noinspection ConstantValue
        assert potentialTypes.size() >= 0;
        switch (potentialTypes.size()) {
            case 1:
                //							tte.attached = ll.get(0).attached;
                //							vte.addPotentialType(instructionIndex, ll.get(0));
                if (p.isResolved()) {
                    LOG.info(String.format(
                            "1047 (vte already resolved) %s vte1.type = %s, gf = %s, tte1 = %s %n",
                            vte1.getName(), vte1.type, generatedFunction, potentialTypes.get(0)));
                } else {
                    final OS_Type attached = potentialTypes.get(0).getAttached();
                    if (attached == null) return;
                    switch (attached.getType()) {
                        case USER:
                            vte1.type.setAttached(attached); // !!
                            break;
                        case USER_CLASS:
                            final GenType gt = vte1.genType;
                            gt.setResolved(attached);
                            vte1.resolveType(gt);
                            break;
                        default:
                            errSink.reportWarning("Unexpected value: " + attached.getType());
                            //										throw new IllegalStateException("Unexpected value: " + attached.getType());
                    }
                }
                break;
            case 0:
                // README moved up here to elimiate work
                if (p.isResolved()) {
                    final var s = String.format(
                            "890-1 Already resolved type: vte1.type = %s, gf = %s %n", vte1.type, generatedFunction);
                    LOG.info(s);
                    break;
                }
                final LookupResultList lrl = ctx.lookup(e_text);
                @Nullable final OS_Element best = lrl.chooseBest(null);
                if (best instanceof @NotNull final FormalArgListItem fali) {
                    Q.action_FormalArgListItem(fali, vte, generatedFunction, vte1, errSink);
                } else if (best instanceof final @NotNull VariableStatement vs) {
                    Q.action_VariableStatement(vs, e_text, generatedFunction, vte1, p, LOG, vte, best);
                } else {
                    final int y = 2;
                    assert best != null;
                    LOG.err("543 " + best.getClass().getName());
                    throw new NotImplementedException();
                }
                break;
            default:
                // TODO hopefully this works
                final @NotNull ArrayList<TypeTableEntry> potentialTypes1 =
                        new ArrayList<>(Collections2.filter(potentialTypes, new Predicate<TypeTableEntry>() {
                            @Override
                            public boolean apply(final TypeTableEntry input) {
                                assert input != null;
                                return input.getAttached() != null;
                            }
                        }));
                // prevent infinite recursion
                if (potentialTypes1.size() < potentialTypes.size())
                    doLogic(potentialTypes1, p, LOG, vte1, errSink, ctx, e_text, vte);
                else LOG.info("913 Don't know");
                break;
        }
    }

    public void _action_002_no_resolved_element(
            final ErrSink errSink,
            final ProcTableEntry pte,
            final IdentTableEntry ite,
            final DeduceTypes2.@NotNull DeduceClient3 dc,
            final @NotNull DeducePhase phase) {
        final DeferredObject<Context, Void, Void> d = new DeferredObject<>();
        d.then(context -> {
            try {
                //				final Context context = resolvedElement.getContext();
                final IdentExpression lu = ite.getIdent();
                final LookupResultList lrl2 = dc.lookupExpression(lu, context);
                @Nullable final OS_Element best = lrl2.chooseBest(null);
                if (best != null) {
                    ite.setStatus(BaseTableEntry.Status.KNOWN, new GenericElementHolder(best));
                    final Eventual<ClassInvocation> ev = new Eventual<>();
                    ev.then(new DoneCallback<ClassInvocation>() {
                        @Override
                        public void onDone(final ClassInvocation Sci) {
                            if (Sci != null) {
                                pte.setClassInvocation(Sci);
                            } else {
                                dc.LOG().err("542 Null ClassInvocation");
                            }
                        }
                    });
                    // README was false so use null here, just preserving above
                    action_002_1(pte, ite, null, dc, phase);
                } else {
                    dc.LOG().err("348 cant resolve (DE3_VTE)" + lu);
                }
            } catch (final ResolveError aResolveError) {
                errSink.reportDiagnostic(aResolveError);
                assert false;
            }
        });

        final VariableTableEntry backlink = principal;

        final OS_Element resolvedElement = backlink.getResolvedElement();
        assert resolvedElement != null;

        if (resolvedElement instanceof IdentExpression) {
            backlink.typePromise().then(result -> {
                final Context context = result.getResolved().getClassOf().getContext();
                d.resolve(context);
            });
        } else {
            // FIXME one day see if spotbugs finds this
            final Context context = resolvedElement.getContext();
            d.resolve(context);
        }
    }

    private void action_002_1(
            @NotNull final ProcTableEntry pte,
            @NotNull final IdentTableEntry ite,
            final Eventual<ClassInvocation> setClassInvocation,
            final DeduceTypes2.DeduceClient3 dc,
            final DeducePhase phase) {
        final OS_Element resolvedElement = ite.getResolvedElement();

        assert resolvedElement != null;

        action_002_1_001(pte, setClassInvocation, dc, phase, resolvedElement);
    }

    private void action_002_1_001(
            final @NotNull ProcTableEntry pte,
            final Eventual<ClassInvocation> ciP,
            final DeduceTypes2.DeduceClient3 dc,
            final DeducePhase phase,
            final OS_Element resolvedElement) {
        if (pte.getFunctionInvocation() != null) return;

        final Pair<ClassInvocation, FunctionInvocation> p = action_002_1_002_1(pte, dc, phase, resolvedElement);
        if (p == null) throw new IllegalStateException();
        final ClassInvocation ci = p.getLeft();
        final FunctionInvocation fi = p.getRight();

        if (ciP != null) {
            ciP.resolve(ci);
        }

        pte.setFunctionInvocation(fi);
    }

    private @Nullable Pair<ClassInvocation, FunctionInvocation> action_002_1_002_1(
            final @NotNull ProcTableEntry pte,
            final DeduceTypes2.DeduceClient3 dc,
            final DeducePhase phase,
            final @NotNull OS_Element resolvedElement) {
        final Pair<ClassInvocation, FunctionInvocation> p;
        final FunctionInvocation fi;
        ClassInvocation ci;

        if (resolvedElement instanceof ClassStatement) {
            // assuming no constructor name or generic parameters based on function syntax
            ci = new ClassInvocation((ClassStatement) resolvedElement, null);
            ci = phase.registerClassInvocation(ci);
            assert ci != null;
            fi = new FunctionInvocation(null, pte, ci, phase.getGeneratePhase());
            p = new ImmutablePair<>(ci, fi);
        } else if (resolvedElement instanceof final FunctionDef functionDef) {
            final IInvocation invocation = dc.getInvocation((GeneratedFunction) generatedFunction);
            fi = new FunctionInvocation(functionDef, pte, invocation, phase.getGeneratePhase());
            if (functionDef.getParent() instanceof ClassStatement) {
                final ClassStatement classStatement =
                        (ClassStatement) fi.getFunction().getParent();
                assert classStatement != null;
                ci = new ClassInvocation(classStatement, null); // TODO generics
                ci = phase.registerClassInvocation(ci);
            } else {
                ci = null;
            }
            p = new ImmutablePair<>(ci, fi);
        } else {
            p = null;
        }

        return p;
    }

    public static class Q {
        public static void action_VariableStatement(
                final @NotNull VariableStatement vs,
                final String e_text,
                final BaseGeneratedFunction generatedFunction,
                final @NotNull VariableTableEntry vte1,
                final Promise<GenType, Void, Void> p,
                final @NotNull ElLog LOG,
                final @NotNull VariableTableEntry vte,
                final @Nullable OS_Element best) {
            //
            assert vs.getName().equals(e_text);
            //
            @Nullable final InstructionArgument vte2_ia = generatedFunction.vte_lookup(vs.getName());
            assert vte2_ia != null;
            @NotNull final VariableTableEntry vte2 = generatedFunction.getVarTableEntry(to_int(vte2_ia));
            if (p.isResolved()) {
                final var s = String.format(
                        "915 Already resolved type: vte2.type = %s, gf = %s %n", vte1.type, generatedFunction);
                LOG.info(s);
            } else {
                final GenType gt = vte1.genType;
                final OS_Type attached = vte2.type.getAttached();
                gt.setResolved(attached);
                vte1.resolveType(gt);
            }
            //								vte.type = vte2.type;
            //								tte.attached = vte.type.attached;
            assert best != null;
            vte.setStatus(BaseTableEntry.Status.KNOWN, new GenericElementHolder(best));
            vte2.setStatus(BaseTableEntry.Status.KNOWN, new GenericElementHolder(best)); // TODO ??
        }

        public static void action_FormalArgListItem(
                final @NotNull FormalArgListItem fali,
                final @NotNull VariableTableEntry vte,
                final @NotNull BaseGeneratedFunction generatedFunction,
                final @NotNull VariableTableEntry vte1,
                final ErrSink errSink) {
            final @NotNull OS_Type osType = new OS_UserType(fali.typeName());
            if (!osType.equals(vte.type.getAttached())) {
                @NotNull
                final TypeTableEntry tte1 = generatedFunction.newTypeTableEntry(
                        TypeTableEntry.Type.SPECIFIED, osType, fali.getNameToken(), vte1);
                /*
                				if (p.isResolved()) System.out.
                						printf("890 Already resolved type: vte1.type = %s, gf = %s, tte1 = %s %n",
                							   vte1.type, generatedFunction, tte1
                						);
                				else
                */
                {
                    final @Nullable OS_Type attached = tte1.getAttached();
                    assert attached != null;
                    switch (attached.getType()) {
                        case USER:
                            vte1.type.setAttached(attached); // !!
                            break;
                        case USER_CLASS:
                            final GenType gt = vte1.genType;
                            gt.setResolved(attached);
                            vte1.resolveType(gt);
                            break;
                        default:
                            errSink.reportWarning("2853 Unexpected value: " + attached.getType());
                    }
                }
            }
            // vte.type = tte1;
            // tte.attached = tte1.attached;
            // vte.setStatus(BaseTableEntry.Status.KNOWN, best);
        }
    }

    public static class ST {
        public static State EXIT_RESOLVE;
        public static State EXIT_CONVERT_USER_TYPES;
        static State INITIAL;

        public static void register(final @NotNull DeducePhase aDeducePhase) {
            EXIT_RESOLVE = aDeducePhase.register(new ExitResolveState());
            INITIAL = aDeducePhase.register(new InitialState());
            EXIT_CONVERT_USER_TYPES = aDeducePhase.register(new ExitConvertUserTypes());
        }

        static class ExitConvertUserTypes implements State {
            private int identity;

            private static void apply_normal(
                    final VariableTableEntry vte,
                    final DeduceTypes2 dt2,
                    final ErrSink errSink,
                    final DeducePhase phase,
                    final @NotNull ElLog LOG,
                    final @NotNull OS_Type attached,
                    final TypeName x,
                    final String tn) {
                final LookupResultList lrl = x.getContext().lookup(tn);
                @Nullable OS_Element best = lrl.chooseBest(null);

                while (best instanceof AliasStatement) {
                    best = DeduceLookupUtils._resolveAlias((AliasStatement) best, dt2);
                }

                if (best != null) {
                    if (!(OS_Type.isConcreteType(best))) {
                        errSink.reportError(String.format("Not a concrete type %s for (%s)", best, tn));
                    } else {
                        LOG.info("705 " + best);
                        // NOTE that when we set USER_CLASS from USER generic information is
                        // still contained in constructable_pte
                        @NotNull
                        final GenType genType = new GenType(
                                attached, ((ClassStatement) best).getOS_Type(), true, x, dt2, errSink, phase);
                        vte.setLikelyType(genType);
                    }
                    // vte.el = best;
                    // NOTE we called resolve_var_table_entry above
                    LOG.info("200 " + best);
                    assert vte.getResolvedElement() == null || vte.getStatus() == BaseTableEntry.Status.KNOWN;
                    // vte.setStatus(BaseTableEntry.Status.KNOWN, best/*vte.el*/);
                } else {
                    errSink.reportDiagnostic(new ResolveError(x, lrl));
                }
            }

            @Override
            public void apply(final DefaultStateful element) {
                final VariableTableEntry vte = ((DeduceElement3_VariableTableEntry) element).principal;

                final DeduceTypes2 dt2 = ((DeduceElement3_VariableTableEntry) element).deduceTypes2();
                final ErrSink errSink = dt2._errSink();
                final @NotNull DeducePhase phase = dt2._phase();
                final @NotNull ElLog LOG = dt2._LOG();

                if (vte.type == null) return; // TODO only for tests

                final @Nullable OS_Type attached = vte.type.getAttached();

                if (attached == null) return;
                if (Objects.requireNonNull(attached.getType()) == OS_Type.Type.USER) {
                    final TypeName x = attached.getTypeName();
                    if (x instanceof NormalTypeName) {
                        final String tn = ((NormalTypeName) x).getName();
                        apply_normal(vte, dt2, errSink, phase, LOG, attached, x, tn);
                    }
                }
            }

            @Override
            public void setIdentity(final int aId) {
                identity = aId;
            }

            @Override
            public boolean checkState(final DefaultStateful aElement3) {
                return true;
            }
        }

        static class InitialState implements State {
            private int identity;

            @Override
            public void apply(final DefaultStateful element) {}

            @Override
            public void setIdentity(final int aId) {
                identity = aId;
            }

            @Override
            public boolean checkState(final DefaultStateful aElement3) {
                return true;
            }
        }

        static class ExitResolveState implements State {
            @SuppressWarnings("FieldCanBeLocal")
            private int identity;

            @Override
            public void apply(final DefaultStateful element) {
                final VariableTableEntry vte = ((DeduceElement3_VariableTableEntry) element).principal;
                vte.resolve_var_table_entry_for_exit_function();
            }

            @Override
            public void setIdentity(final int aId) {
                identity = aId;
            }

            @Override
            public boolean checkState(final DefaultStateful aElement3) {
                return ((DeduceElement3_VariableTableEntry) aElement3).st == ST.INITIAL;
            }
        }
    }

    private static class Diagnostic_8884 implements GCFM_Diagnostic {
        private final VariableTableEntry vte;
        private final BaseGeneratedFunction gf;
        private final int _code = 8884;

        public Diagnostic_8884(final VariableTableEntry aVte, final BaseGeneratedFunction aGf) {
            vte = aVte;
            gf = aGf;
        }

        @Override
        public String code() {
            return "" + _code;
        }

        @Override
        public Severity severity() {
            return Severity.ERROR;
        }

        @Override
        public @NotNull ElLocatable primary() {
            return null;
        }

        @Override
        public @NotNull List<ElLocatable> secondary() {
            return null;
        }

        @Override
        public void report(final PrintStream stream) {
            stream.printf(_message());
        }

        @Override
        public String _message() {
            return String.format("%d temp variable has no type %s %s", _code, vte, gf);
        }
    }

    private static class Diagnostic_8885 implements GCFM_Diagnostic {
        private final VariableTableEntry vte;
        private final int _code = 8885;

        public Diagnostic_8885(final VariableTableEntry aVte) {
            vte = aVte;
        }

        @Override
        public String code() {
            return "" + _code;
        }

        @Override
        public Severity severity() {
            return Severity.ERROR;
        }

        @Override
        public @NotNull ElLocatable primary() {
            return null;
        }

        @Override
        public @NotNull List<ElLocatable> secondary() {
            return null;
        }

        @Override
        public void report(final @NotNull PrintStream stream) {
            stream.printf(_message());
        }

        @Override
        public String _message() {
            return String.format("%d x is null (No typename specified) for %s%n", _code, vte.getName());
        }
    }
}
