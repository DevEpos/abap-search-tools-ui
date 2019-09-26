package com.devepos.adt.saat.internal.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.Assert;

public class Reflection {
	private final Object object;

	public static Reflection forObject(Object o) {
		return new Reflection(o);
	}

	public static Reflection forClass(Class<?> clazz) {
		return new Reflection(clazz);
	}

	public static boolean classIsAvailable(String className, ClassLoader classLoader) {
		return classForName(className, classLoader) != null;
	}

	public static Class<?> classForName(String className, ClassLoader classLoader) {
		try {
			return Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException localClassNotFoundException) {
			return null;
		} catch (LinkageError e) {
		}
		return null;
	}

	private Reflection(Object object) {
		this.object = object;
	}

	public boolean supportsMethod(String methodName) {
		return supportsMethod(methodName);
	}

	public boolean supportsMethod(String methodName, Class<?>... argTypes) {
		return getMethod(methodName, argTypes) != null;
	}

	public Object invoke(String methodName) {
		return invoke(methodName, null);
	}

	public Object invoke(String methodName, Class<?>[] argTypes, Object... args) {
		try {
			Method method = getMethod(methodName, argTypes);
			if (method == null) {
				return null;
			}
			method.setAccessible(true);
			return method.invoke(this.object, args);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if ((cause instanceof RuntimeException)) {
				throw ((RuntimeException) cause);
			}
			if ((cause instanceof Error)) {
				throw ((Error) cause);
			}
			throw new IllegalStateException(cause.getMessage(), cause);
		}
	}

	public Object getFieldValue(String fieldName, Class<?> clazz) {
		try {
			Class<?> cls = clazz != null ? clazz : this.object.getClass();
			Field field = getField(fieldName, cls);
			if (field != null) {
				field.setAccessible(true);
				return field.get(this.object);
			}
			return null;
		} catch (SecurityException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public void setFieldValue(String fieldName, Class<?> clazz, Object value) {
		try {
			Class<?> cls = clazz != null ? clazz : this.object.getClass();
			Field field = getField(fieldName, cls);
			if (field != null) {
				field.setAccessible(true);
				field.set(this.object, value);
			}
		} catch (SecurityException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static boolean hasField(String fieldName, Class<?> clazz) {
		return getField(fieldName, clazz) != null;
	}

	private Method getMethod(String methodName, Class<?>... argTypes) {
		Method method = getPublicMethod(methodName, argTypes);
		if (method != null) {
			return method;
		}
		return getDeclaredMethod(methodName, argTypes);
	}

	private Method getPublicMethod(String methodName, Class<?>... argTypes) {
		if (this.object == null) {
			return null;
		}
		return getPublicMethod(getEffectiveClass(), methodName, argTypes);
	}

	private Class<?> getEffectiveClass() {
		if ((this.object instanceof Class)) {
			return (Class<?>) this.object;
		}
		return this.object.getClass();
	}

	private static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>... argTypes) {
		try {
			return clazz.getMethod(methodName, argTypes);
		} catch (SecurityException localSecurityException) {
		} catch (NoSuchMethodException localNoSuchMethodException) {
		}
		return null;
	}

	private Method getDeclaredMethod(String methodName, Class<?>... paramTypes) {
		if (this.object == null) {
			return null;
		}
		return getDeclaredMethod(getEffectiveClass(), methodName, paramTypes);
	}

	private static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
		try {
			return clazz.getDeclaredMethod(name, paramTypes);
		} catch (NoSuchMethodException localNoSuchMethodException) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null) {
				return getDeclaredMethod(superClass, name, paramTypes);
			}
		} catch (SecurityException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		return null;
	}

	private static Field getField(String fieldName, Class<?> clazz) {
		Assert.isNotNull(clazz, "class must not be null");
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException localNoSuchFieldException) {
			return null;
		} catch (SecurityException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
