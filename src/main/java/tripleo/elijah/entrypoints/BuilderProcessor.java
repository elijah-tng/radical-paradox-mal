package tripleo.elijah.entrypoints;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BuilderProcessor extends AbstractProcessor {

	protected ProcessingEnvironment processingEnv;

	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		for (final TypeElement annotation : annotations) {
			final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

			final Map<Boolean, List<Element>> annotatedMethods = annotatedElements.stream().collect(Collectors.partitioningBy(element -> ((ExecutableType) element.asType()).getParameterTypes().size() == 1 && element.getSimpleName().toString().startsWith("set")));

			final List<Element> setters      = annotatedMethods.get(true);
			final List<Element> otherMethods = annotatedMethods.get(false);

			otherMethods.forEach(element -> processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@BuilderProperty must be applied to a setXxx method with a single argument", element));

			if (setters.isEmpty()) {
				continue;
			}

			final String className = ((TypeElement) setters.get(0).getEnclosingElement()).getQualifiedName().toString();

			final Map<String, String> setterMap = setters.stream().collect(Collectors.toMap(setter -> setter.getSimpleName().toString(), setter -> ((ExecutableType) setter.asType()).getParameterTypes().get(0).toString()));

			try {

				String    packageName = null;
				final int lastDot     = className.lastIndexOf('.');
				if (lastDot > 0) {
					packageName = className.substring(0, lastDot);
				}

				final String simpleClassName        = className.substring(lastDot + 1);
				final String builderClassName       = className + "Builder";
				final String builderSimpleClassName = builderClassName.substring(lastDot + 1);

				final JavaClassSource javaClass = Roaster.create(JavaClassSource.class);
				javaClass
						.setName(builderSimpleClassName)
						.setPackage(packageName)
						.setPublic();

				javaClass.addField()
						.setName("object")
						.setType(simpleClassName)
						.setPrivate()
						.setFinal(true)
						.setLiteralInitializer("new " + simpleClassName + "();");

				javaClass.addMethod()
						.setName("build")
						.setReturnType(simpleClassName)
						.setPublic()
						.setBody("        return object;");

				setterMap.entrySet().forEach(setter -> {
					final String methodName   = setter.getKey();
					final String argumentType = setter.getValue();

					final MethodSource<JavaClassSource> m = javaClass.addMethod()
							.setName(methodName)
							.setPublic()
							.setReturnType(builderSimpleClassName);
					m.addParameter(argumentType, "value");
					m.setBody("this.object." + methodName + " (value);" + "return this;");
				});


				final JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(builderClassName);
				try (final PrintWriter out = new PrintWriter(builderFile.openWriter())) {
					out.print(javaClass.toString());
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		return true;
	}
}
