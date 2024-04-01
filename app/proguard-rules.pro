# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#Mantém todas as entidades e DAOs.
-keep class * extends androidx.room.RoomDatabase {
    public static <methods>;
}

-keep @androidx.room.Entity class * {*;}
-keep @androidx.room.Dao class * {*;}
-keep @androidx.room.Database class * {*;}

# Mantém as classes que têm métodos anotados com @Insert, @Delete, @Query, etc., e suas chamadas de callback.
-keepclassmembers,allowobfuscation class * {
    @androidx.room.* <methods>;
}

# Mantém as classes de modelo usadas nas consultas do Room.
-keep class **_Impl { *; }

# Mantém os nomes dos métodos de consulta, para que o Room possa encontrá-los em tempo de execução.
-keepclassmembers class * {
    @androidx.room.Query <methods>;
}