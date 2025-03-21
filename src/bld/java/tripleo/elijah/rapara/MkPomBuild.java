package tripleo.elijah.rapara;

import rife.bld.Project;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static rife.bld.dependencies.Repository.MAVEN_CENTRAL;
import static rife.bld.dependencies.Repository.RIFE2_RELEASES;
import static rife.bld.dependencies.Scope.compile;
import static rife.bld.dependencies.Scope.test;
import static rife.bld.operations.TemplateType.HTML;

public class MkPomBuild extends Project {
	public MkPomBuild() {
		pkg     = "rifers";
		name    = "Rifers";
		version = version(1, 0, 0);

		downloadSources   = true;
		autoDownloadPurge = true;

		final String V_activej_version = "6.0-beta2";//"6.0-SNAPSHOT";
		// final String V_jdeferred_version = "2.0.0";
		final String V_guava_version = "33.1.0-jre";

		repositories = List.of(MAVEN_CENTRAL, RIFE2_RELEASES);

		scope(compile)
				.include(dependency("com.uwyn.rife2", "bld", "1.9.1")) // ??
				.include(dependency("com.uwyn.rife2", "rife2", version(1, 7, 3)))
				.include(dependency("com.uwyn.rife2", "rife2-core", "1.8.1"))
				.include(dependency("com.uwyn.rife2", "rife2-renderers", "1.1.5"))

				.include(dependency("com.fasterxml.jackson.datatype", "jackson-datatype-pcollections", "2.17.2"))
				.include(dependency("org.pcollections", "pcollections", "4.0.2"))

				.include(dependency("io.activej", "activej-net", V_activej_version))
				.include(dependency("io.activej", "activej-inject", V_activej_version))
				.include(dependency("io.activej", "activej-launcher", V_activej_version))
				.include(dependency("io.activej", "activej-csp", V_activej_version))
				.include(dependency("io.activej", "activej-common", V_activej_version))
				.include(dependency("io.activej", "activej-http", V_activej_version))
				.include(dependency("io.activej", "activej-eventloop", V_activej_version))
				.include(dependency("io.activej", "activej-launchers-http", V_activej_version))
				.include(dependency("io.activej", "activej-jmxapi", V_activej_version))

				.include(dependency("com.fasterxml.jackson.core", "jackson-databind", "2.17.0"))
				.include(dependency("com.squareup.okhttp3", "okhttp", "4.12.0"))
				.include(dependency("io.undertow", "undertow-servlet", "2.3.12.Final"))

				.include(dependency("org.apache.commons", "commons-lang3", "3.14.0"))
				.include(dependency("commons-codec", "commons-codec", "1.17.0"))

				.include(dependency("net.java.dev.jna", "jna", "5.14.0"))

				.include(dependency("io.reactivex.rxjava3", "rxjava", "3.1.8"))
				.include(dependency("org.reactivestreams", "reactive-streams", "1.0.4"))

				//.include(dependency("me.friwi", "jcefmaven", V_jcef))

				.include(dependency("org.slf4j", "slf4j-simple", "2.0.13"))
				.include(dependency("org.slf4j", "slf4j-api", "2.0.13"))

				// .include(dependency("org.jdeferred.v2", "jdeferred-core", V_jdeferred_version))

				.include(dependency("org.eclipse.jdt", "org.eclipse.jdt.annotation", "2.3.0"))
				.include(dependency("org.jetbrains", "annotations", "24.1.0"))

				.include(dependency("antlr", "antlr", "2.7.7"))

//				.include(dependency("commons-cli", "commons-cli", "1.8.0"))
				.include(dependency("com.github.spotbugs", "spotbugs-annotations", "4.8.3"))

				.include(dependency("com.google.guava", "guava", V_guava_version))

				.include(dependency("tripleo.elijah", "elijah-cegont-upper", "0.1-prolific-remnant"))

		// .include(dependency("tripleo.buffers", "buffers-v1", V_buffers_version))
		// .include(dependency("tripleo.util.range", "range-v1", V_range_version))
		;

		scope(test)
				.include(dependency("org.jsoup", "jsoup", version(1, 17, 2)))

				.include(dependency("org.junit.jupiter", "junit-jupiter", version(5, 10, 2)))
				.include(dependency("org.junit.platform", "junit-platform-console-standalone", version(1, 10, 2)))

				.include(dependency("ch.qos.logback", "logback-classic", "1.5.3"))
				.include(dependency("org.easymock", "easymock", "5.2.0"))
				.include(dependency("junit", "junit", "4.13.2"));

		precompileOperation().templateTypes(HTML);

		final var core_directory = new File(workDirectory(), "src");
		final var mal_directory  = new File(workDirectory(), "src-mal");

		assert core_directory.exists();
		assert mal_directory.exists();

		compileOperation()
				.buildMainDirectory(Path.of(core_directory.toString(), "main", "java").toFile())
				.buildMainDirectory(Path.of(mal_directory.toString(), "java").toFile())
				//.buildMainDirectory(Path.of(undertow_directory.toString(), "java").toFile())
				//.buildMainDirectory(Path.of(cef_directory.toString(), "java").toFile())
				.buildTestDirectory(Path.of(core_directory.toString(), "test", "java").toFile());


	}

	public static void main(final String[] args) {
		final MkPomBuild build = new MkPomBuild();
		build.start(args);
		System.err.println("119 " + build.compileOperation().testSourceDirectories());
		System.err.println("127 " + build.compileOperation().testSourceFiles());
	}
}
