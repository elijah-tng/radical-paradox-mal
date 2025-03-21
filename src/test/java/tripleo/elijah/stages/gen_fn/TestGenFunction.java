/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.stages.gen_fn;

import io.activej.test.rules.EventloopRule;
import org.jdeferred2.DoneCallback;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import tripleo.elijah.comp.AccessBus;
import tripleo.elijah.comp.CM_Module;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.comp.PipelineLogic;
import tripleo.elijah.entrypoints.MainClassEntryPoint;
import tripleo.elijah.factory.comp.CompilationFactory;
import tripleo.elijah.lang.ClassStatement;
import tripleo.elijah.lang.FunctionDef;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.lang.OS_Type;
import tripleo.elijah.stages.deduce.DeducePhase;
import tripleo.elijah.stages.deduce.FunctionMapHook;
import tripleo.elijah.stages.instructions.InstructionName;
import tripleo.elijah.work.DefaultWorkManager;
import tripleo.elijah_fluffy.comp.CM_Preludes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static tripleo.elijah_fluffy.util.Helpers.List_of;

/**
 * Created 9/10/20 2:20 PM
 */
public class TestGenFunction {
    @ClassRule
    public static final EventloopRule eventloopRule = new EventloopRule();

    @Test
    public void testDemoElNormalFact1Elijah() throws Exception {
        final Compilation c = CompilationFactory.mkCompilation();

        final String f    = "test/demo-el-normal/fact1.elijah";
        final File   file = new File(f);
        c.realParseElijjahFile(f, file, false).then(new DoneCallback<CM_Module>() {
            @Override
            public void onDone(final CM_Module mm) {
                OS_Module m = mm.getModule();
                Assert.assertNotNull("Method parsed correctly", m);
                m.prelude = c.findPrelude2(CM_Preludes.C).success().getModule(); // TODO we dont know which prelude to find yet

                //
                //
                //
                final ClassStatement main_class = (ClassStatement) m.findClass("Main");
                assert main_class != null;
                final MainClassEntryPoint mainClassEntryPoint = new MainClassEntryPoint(main_class);
                // m.entryPoints = new EntryPointList();
                m.entryPoints.add(mainClassEntryPoint);
                //
                //
                //

                final List<FunctionMapHook> ran_hooks = new ArrayList<>();

                final AccessBus ab = new AccessBus(c);
                ab.addPipelineLogic(PipelineLogic::new);

                c.setPipelineLogic(ab.__getPL());

                final @NotNull GeneratePhase generatePhase1 = c.getPipelineLogic().getGeneratePhase();
                final GenerateFunctions      gfm            = generatePhase1.getGenerateFunctions(m);
                final @NotNull DeducePhase   dp             = c.getPipelineLogic().getDp();
                gfm.generateFromEntryPoints(m.entryPoints, dp);

                final DeducePhase.@NotNull GeneratedClasses lgc = dp.getGeneratedClasses(); //new ArrayList<>();

                /*
                List<GeneratedNode> lgf = new ArrayList<>();
                for (GeneratedNode generatedNode : lgc) {
                  if (generatedNode instanceof GeneratedClass)
                    lgf.addAll(((GeneratedClass) generatedNode).functionMap.values());
                  if (generatedNode instanceof GeneratedNamespace)
                    lgf.addAll(((GeneratedNamespace) generatedNode).functionMap.values());
                  // TODO enum
                }
                */

                // Assert.assertEquals(2, lgf.size());

                final DefaultWorkManager wm = new DefaultWorkManager();

                c.addFunctionMapHook(new FunctionMapHook() {
                    @Override
                    public boolean matches(final FunctionDef fd) {
                        final boolean b = fd.name().equals("main") && fd.getParent() == main_class;
                        return b;
                    }

                    @Override
                    public void apply(final Collection<GeneratedFunction> aGeneratedFunctions) {
                        assert aGeneratedFunctions.size() == 1;

                        final GeneratedFunction gf = aGeneratedFunctions.iterator().next();

                        int pc = 0;
                        Assert.assertEquals(InstructionName.E, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.DECL, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.AGNK, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.DECL, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.AGN, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.CALL, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.X, gf.getInstruction(pc++).getName());

                        ran_hooks.add(this);
                    }
                });

                c.addFunctionMapHook(new FunctionMapHook() {
                    @Override
                    public boolean matches(final FunctionDef fd) {
                        final boolean b = fd.name().equals("factorial") && fd.getParent() == main_class;
                        return b;
                    }

                    @Override
                    public void apply(final Collection<GeneratedFunction> aGeneratedFunctions) {
                        assert aGeneratedFunctions.size() == 1;

                        final GeneratedFunction gf = aGeneratedFunctions.iterator().next();

                        int pc = 0;
                        Assert.assertEquals(InstructionName.E, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.DECL, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.AGNK, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.ES, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.DECL, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.AGNK, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.JE, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.CALLS, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.CALLS, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.JMP, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.XS, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.AGN, gf.getInstruction(pc++).getName());
                        Assert.assertEquals(InstructionName.X, gf.getInstruction(pc++).getName());

                        ran_hooks.add(this);
                    }
                });

                c.addFunctionMapHook(new FunctionMapHook() {
                    @Override
                    public boolean matches(final FunctionDef fd) {
                        final boolean b = fd.name().equals("main") && fd.getParent() == main_class;
                        return b;
                    }

                    @Override
                    public void apply(final Collection<GeneratedFunction> aGeneratedFunctions) {
                        assert aGeneratedFunctions.size() == 1;

                        final GeneratedFunction gf = aGeneratedFunctions.iterator().next();

                        System.out.println("main\n====");
                        for (int i = 0; i < gf.vte_list.size(); i++) {
                            final VariableTableEntry vte = gf.getVarTableEntry(i);
                            System.out.printf("8007 %s %s %s%n", vte.getName(), vte.type, vte.potentialTypes());
                            if (vte.type.getAttached() != null) {
                                Assert.assertNotEquals(OS_Type.Type.BUILT_IN, vte.type.getAttached().getType());
                                Assert.assertNotEquals(OS_Type.Type.USER, vte.type.getAttached().getType());
                            }
                        }
                        System.out.println();

                        ran_hooks.add(this);
                    }
                });

                c.addFunctionMapHook(new FunctionMapHook() {
                    @Override
                    public boolean matches(final FunctionDef fd) {
                        final boolean b = fd.name().equals("factorial") && fd.getParent() == main_class;
                        return b;
                    }

                    @Override
                    public void apply(final Collection<GeneratedFunction> aGeneratedFunctions) {
                        assert aGeneratedFunctions.size() == 1;

                        final GeneratedFunction gf = aGeneratedFunctions.iterator().next();

                        System.out.println("factorial\n=========");
                        for (int i = 0; i < gf.vte_list.size(); i++) {
                            final VariableTableEntry vte = gf.getVarTableEntry(i);
                            System.out.printf("8008 %s %s %s%n", vte.getName(), vte.type, vte.potentialTypes());
                            if (vte.type.getAttached() != null) {
                                Assert.assertNotEquals(OS_Type.Type.BUILT_IN, vte.type.getAttached().getType());
                                Assert.assertNotEquals(OS_Type.Type.USER, vte.type.getAttached().getType());
                            }
                        }
                        System.out.println();

                        ran_hooks.add(this);
                    }
                });

                dp.deduceModule(m, lgc, false, Compilation.gitlabCIVerbosity());
                dp.finish(dp.getGeneratedClasses());

                Assert.assertEquals("Not all hooks ran", 4, ran_hooks.size());
                Assert.assertEquals(108, c.errorCount());
            }
        });
    }

    @Ignore
    @Test
    public void testGenericA() {
        final Compilation c = CompilationFactory.mkCompilation();

        final String f = "test/basic1/genericA/";

        c.feedCmdLine(List_of(f));

        Assert.assertEquals(108, c.errorCount());
    }

    @Test
    public void testBasic1Backlink3Elijah() {
        final Compilation c = CompilationFactory.mkCompilation();

        final String ff = "test/basic1/backlink3/";
        c.feedCmdLine(List_of(ff));

        final int maybe_this_should_not_be_zero = 1;
        Assert.assertEquals(maybe_this_should_not_be_zero, c.errorCount());

        var x = "" + c.getErrSink()._error(1);
        Assert.assertEquals("(WARNING_STRING,9996 Not an .ez file test/basic1/backlink3/)", x);
    }
}

//
//
//
