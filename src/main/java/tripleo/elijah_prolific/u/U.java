package tripleo.elijah_prolific.u;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

public enum U {
	;

	private static Gson gson;

	public static @NotNull Gson getGson() {
		if (gson == null) {
			gson = new GsonBuilder()
					// .registerTypeAdapter(_JsonLog.class, new _JsonLog_TypeAdapter())
					.enableComplexMapKeySerialization()
					// .serializeNulls()
					// .setDateFormat(DateFormat.LONG)
					.excludeFieldsWithoutExposeAnnotation()
					.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
					.setPrettyPrinting()
					.setVersion(1.0)
					.create();
		}
		return gson;
	}
}
