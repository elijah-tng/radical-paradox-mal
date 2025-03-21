package tripleo.elijah.world.impl;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.entrypoints.MainClassEntryPoint;
import tripleo.elijah.lang.*;
import tripleo.elijah.stages.gen_fn.BaseGeneratedFunction;
import tripleo.elijah.stages.gen_fn.GeneratedClass;
import tripleo.elijah.stages.gen_fn.GeneratedNamespace;
import tripleo.elijah.world.i.LivingClass;
import tripleo.elijah.world.i.LivingFunction;
import tripleo.elijah.world.i.LivingPackage;
import tripleo.elijah.world.i.LivingRepo;
import tripleo.elijah_fluffy.util.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

public class DefaultLivingRepo implements LivingRepo {
	private final Map<String, LivingPackage> _packages     = new HashMap<>();
	private       int                        _packageCode  = 1;
	private       int                        _classCode    = 101;
	private       int                        _functionCode = 1001;

	public OS_Package makePackage(final @NotNull Qualident pkg_name) {
		// FIXME hier lookup/query db

		final String pkg_name_s = pkg_name.toString();
		if (!isPackage(pkg_name_s)) {
			final OS_Package    newPackage = new OS_Package(pkg_name, nextPackageCode());
			final LivingPackage lp         = new DefaultLivingPackage(pkg_name, newPackage);
			_packages.put(pkg_name_s, lp);
			return lp.getElement();
		} else {
			return _packages.get(pkg_name_s).getElement();
		}
	}

	public boolean isPackage(final String pkg) {
		return _packages.containsKey(pkg);
	}

	private int nextPackageCode() {
		return _packageCode++;
	}

	@Override
	public LivingClass addClass(final ClassStatement cs) {
		return null;
	}

	@Override
	public LivingFunction addFunction(final BaseFunctionDef fd) {
		return null;
	}

	@Override
	public LivingPackage addPackage(final OS_Package pk) {
		return null;
	}

	@Override
	public OS_Package getPackage(final String aPackageName) {
		return _packages.get(aPackageName).getElement();
	}

	@Override
	public DefaultLivingFunction addFunction(final BaseGeneratedFunction aFunction, final Add addFlag) {
		switch (addFlag) {
			case NONE -> {
				aFunction.setCode(nextFunctionCode());
			}
			case MAIN_FUNCTION -> {
				if (aFunction.getFD() instanceof FunctionDef
						&& MainClassEntryPoint.is_main_function_with_no_args((FunctionDef) aFunction.getFD())) {
					aFunction.setCode(1000);
					// compilation.notifyFunction(code, aFunction);
				} else {
					throw new IllegalArgumentException("not a main function");
				}
			}
			case MAIN_CLASS -> {
				throw new IllegalArgumentException("not a class");
			}
		}

		final DefaultLivingFunction living = new DefaultLivingFunction(aFunction);
		aFunction._living = living;

		return living;
	}

	public int nextFunctionCode() {
		return _functionCode++;
	}

	@Override
	public DefaultLivingClass addClass(final GeneratedClass aClass, final Add addFlag) {
		switch (addFlag) {
			case NONE -> {
				aClass.setCode(nextClassCode());
			}
			case MAIN_FUNCTION -> {
				throw new IllegalArgumentException("not a function");
			}
			case MAIN_CLASS -> {
				final boolean isMainClass = MainClassEntryPoint.isMainClass(aClass.getKlass());
				if (!isMainClass) {
					throw new IllegalArgumentException("not a main class");
				}
				aClass.setCode(100);
			}
		}

		final DefaultLivingClass living = new DefaultLivingClass(aClass);
		aClass._living = living;

		return living;
	}

	public int nextClassCode() {
		return _classCode++;
	}

	@Override
	public void addNamespace(final GeneratedNamespace aNamespace, final Add aNone) {
		throw new NotImplementedException();
	}
}
