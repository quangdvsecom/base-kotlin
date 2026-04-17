# Keep all classes and members within the CameraView package
-keep class com.otaliastudios.cameraview.** { *; }

# If you're using custom filters, make sure their constructors are kept (as per previous answer)
# Replace 'com.yourpackage.MyCustomFilter' with your actual filter class
-keep class com.otaliastudios.cameraview.filter.BaseFilter {
    public <init>();
}

# If CameraView uses reflection for specific interfaces or abstract classes,
# you might need rules like these to keep implementers.
# (This is illustrative; actual needs depend on CameraView's internal implementation)
-keep class * implements com.otaliastudios.cameraview.filter.Filter {
    public <init>();
}

-keep class * implements com.otaliastudios.cameraview.filter.Filters {
    public <init>();
}

# Sometimes, specific attributes like Signature are needed for generics or certain APIs.
# You might not need this for CameraView specifically, but it's a common ProGuard rule.
-keepattributes Signature