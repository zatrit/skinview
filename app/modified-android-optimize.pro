-optimizationpasses 5
-allowaccessmodification
-verbose

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers,allowoptimization class * {
    public static final ** CREATOR;
}