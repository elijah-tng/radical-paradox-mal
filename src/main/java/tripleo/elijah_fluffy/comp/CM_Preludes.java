package tripleo.elijah_fluffy.comp;

import org.jetbrains.annotations.Nullable;
import tripleo.elijah.stages.gen_c.GenerateC;
import tripleo.elijah.stages.gen_generic.GenerateFiles;
import tripleo.elijah.stages.gen_generic.OutputFileFactoryParams;

import java.util.Objects;

public enum CM_Preludes {
    C {
        @Override
        public GenerateFiles create(final OutputFileFactoryParams aParams) {
            return new GenerateC(aParams);
        }

        @Override
        public String getName() {
            return "c";
        }
    },
    JAVA {
        @Override
        public GenerateFiles create(final OutputFileFactoryParams aParams) {
            return null;
        }

        @Override
        public String getName() {
            return "java";
        }
    };

    public static @Nullable _Creator dispatch(final String lang) {
        for (CM_Preludes cmPreludes : CM_Preludes.values()) {
            if (Objects.equals(lang, cmPreludes.getName()))
                return new _Creator() {
                    @Override
                    public GenerateFiles create(final OutputFileFactoryParams aParams) {
                        return cmPreludes.create(aParams);
                    }
                };
        }
        // throw new NotImplementedException();
        return null;
    }

    public abstract GenerateFiles create(final OutputFileFactoryParams aP);

    public abstract String getName();

    public interface _Creator {
        GenerateFiles create(OutputFileFactoryParams aParams);
    }
}
