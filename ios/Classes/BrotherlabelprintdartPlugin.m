#import "BrotherlabelprintdartPlugin.h"
#if __has_include(<brotherlabelprintdart/brotherlabelprintdart-Swift.h>)
#import <brotherlabelprintdart/brotherlabelprintdart-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "brotherlabelprintdart-Swift.h"
#endif

@implementation BrotherlabelprintdartPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftBrotherlabelprintdartPlugin registerWithRegistrar:registrar];
}
@end
