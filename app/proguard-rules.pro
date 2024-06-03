-allowaccessmodification
-repackageclasses

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
	public static void checkNotNull(...);
	public static void checkExpressionValueIsNotNull(...);
	public static void checkNotNullExpressionValue(...);
	public static void checkParameterIsNotNull(...);
	public static void checkNotNullParameter(...);
	public static void checkReturnedValueIsNotNull(...);
	public static void checkFieldIsNotNull(...);
	public static void throwUninitializedPropertyAccessException(...);
	public static void throwNpe(...);
	public static void throwJavaNpe(...);
	public static void throwAssert(...);
	public static void throwIllegalArgument(...);
	public static void throwIllegalState(...);
}

-assumenosideeffects class java.lang.Throwable {
    public void printStackTrace(...);
}

-assumenosideeffects class android.util.Log {
    public static *(...);
}

-assumenosideeffects class ** { @zatrit.skinbread.DebugOnly *; }