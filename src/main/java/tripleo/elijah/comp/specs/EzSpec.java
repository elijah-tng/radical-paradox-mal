package tripleo.elijah.comp.specs;

import tripleo.elijah.comp.internal.CiReasoning;

import java.io.File;
import java.io.InputStream;

public record EzSpec(String f, InputStream s, File file, CiReasoning reasoning) {
}
