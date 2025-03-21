package tripleo.elijah_prolific.v;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.comp.StdErrSink;
import tripleo.elijah.comp.functionality.f203.F203;
import tripleo.elijah.nextgen.inputtree.EIT_Input;
import tripleo.elijah.nextgen.outputstatement.EG_SequenceFactory;
import tripleo.elijah.nextgen.outputstatement.EG_SingleStatement;
import tripleo.elijah.nextgen.outputstatement.EG_Statement;
import tripleo.elijah.nextgen.outputstatement.EG_StatementUtils;
import tripleo.elijah.nextgen.outputtree.EOT_OutputFile;
import tripleo.elijah.nextgen.outputtree.EOT_OutputType;
import tripleo.elijah.stages.gen_generic.GenerateResult;
import tripleo.elijah.stages.gen_generic.GenerateResultItem;
import tripleo.elijah_prolific.u.U;
import tripleo.vendor.org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static tripleo.elijah_fluffy.util.Helpers.List_of;

public class V {
    private static final List<String> logs = new ArrayList<>();
    private static final List<_JsonLog> jsonLogs = new ArrayList<>();

    public static void asv(final e aE, final String aKey) {
        final String x = "{{V.asv}} " + aE + " " + aKey;
        addLog(x);
        addJsonLog("asv", "" + aE, List_of(aKey));
        // System.err.println("[debug] "+x);
    }

    public static void gri(final GenerateResult gr) {
        final PrintStream stream = System.out;

        for (final GenerateResultItem ab : gr.results()) {
            // stream.println(ab.counter);
            final String ty = "" + ab.ty;
            // stream.println(ty);
            final String ou = ab.output;
            // stream.println("\"[240914 0051] \"+"+ou);
            final String ns = ab.node.identityString();
            // stream.println(ns);

            // noinspection unused
            final String bt = ab.buffer.getText();
            // stream.println(bt);

            final String x = "{{V.gr}} " + ty + " " + ou + " " + ns;
            addLog(x);
            addJsonLog("gr", "", List_of(ty, ou, ns));
        }
    }

    public static void exit(final Compilation c) {
        final String x = "{{V.exit}}";
        addLog(x);
        addJsonLog("exit", "", List_of(""));

        finishJsonLog(c);
    }

    private static void addJsonLog(final String aHeadCode, final String aSecond, final List<String> aStringList) {
        jsonLogs.add(new _JsonLog(aHeadCode, aSecond, aStringList));
    }

    private static void finishJsonLog(final Compilation c) {
        final Gson gson = U.getGson();

        final String jsonString = gson.toJson(jsonLogs);
        // noinspection unused
        // final String jsonString1  = gson.toJson(logs);

        final List<EIT_Input> inputs = List_of();
        // final EOT_OutputFile  off    = convertLogsAndInputsToOutputFile(c, inputs, logs);
        // c.getOutputTree().add(off);

        final EG_Statement seq2 = new EG_SingleStatement(jsonString);
        final String fn = "error-report.json";
        final EOT_OutputFile off2 = new EOT_OutputFile(c, inputs, fn, EOT_OutputType.ERROR_REPORT, seq2);
        c.getOutputTree().add(off2);

        c.comp_dir_promise()
                .resolve(
                        new Supplier<File>() {
                            @Override
                            public File get() {
                                // noinspection UnnecessaryLocalVariable
                                final File f = new F203(c.getErrSink(), c).chooseDirectory();
                                return f;
                            }
                        }.get());
        c.comp_dir_promise().then(f -> {
            try {
                Files.write(jsonString.getBytes(), new File(f, fn));
                // Files.write(off.getStatementSequence().getText().getBytes(), new File(f, off.getFilename()));

                final List<Pair<StdErrSink.Desc, Object>> err = c.getErrSink()._errors();
                Files.write((gson.toJson(err)).getBytes(), new File(f, "stdErrSink.json"));
            } catch (final IOException aE) {
                throw new RuntimeException(aE);
            }
        });

        // System.err.println("[240914 0088] error-report.json"+jsonString);
    }

    @NotNull
    private static EOT_OutputFile convertLogsAndInputsToOutputFile(
            final Compilation c, final List<EIT_Input> inputs, final List<String> aLogs) {
        final List<EG_Statement> sts = EG_StatementUtils.mapStringListToSingleStatementList(aLogs);
        final EG_SequenceFactory._SequenceBuilder sequenceBuilder =
                EG_SequenceFactory.newSequence().addParts(sts);
        final EG_Statement seq = sequenceBuilder.build();
        // noinspection UnnecessaryLocalVariable
        final EOT_OutputFile off = new EOT_OutputFile(c, inputs, "error-report.txt", EOT_OutputType.ERROR_REPORT, seq);
        return off;
    }

    private static void addLog(final String x) {
        logs.add(x);
        // System.err.println(x);
    }

    public enum e {
        f202_writing_logs,
        _putSeq,
        DT2_1785,
        d399_147,
        DT2_2304,
        DT2_2163
    }

    public record _JsonLog(@Expose String headCode, @Expose String second, @Expose List<String> stringList) {}

    // public static class _JsonLog_TypeAdapter implements JsonSerializer<_JsonLog_TypeAdapter>,
    // JsonDeserializer<_JsonLog> {
    //	@Override
    //	public _JsonLog deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
    // throws JsonParseException {
    //		JsonObject jsonObject = json.getAsJsonObject();
    //		String     headcode   = jsonObject.get("headCode").getAsString();
    //		String     second   = jsonObject.get("second").getAsString();
    //		String     stringList   = jsonObject.get("stringList").getAsString();
    //
    //		try {
    //			Class<? extends _JsonLog> cls = (Class<? extends _JsonLog>) Class.forName(typeName);
    //			return new Gson().fromJson(json, cls);
    //		} catch (ClassNotFoundException e) {
    //			throw new JsonParseException(e);
    //		}
    //	}
    //
    //	@Override
    //	public JsonElement serialize(final _JsonLog_TypeAdapter src, final Type typeOfSrc, final JsonSerializationContext
    // context) {
    //		JsonElement elem = new Gson().toJsonTree(_JsonLog.class);
    //		elem.getAsJsonObject().addProperty("type", _JsonLog.class.getName());
    //		return elem;
    //	}
    // }
}
