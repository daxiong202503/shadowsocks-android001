name: Build Android APK (NDK Fix)

on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
      with:
        submodules: recursive
        fetch-depth: 0
        
    - name: Initialize and update submodules
      run: |
        echo "Initializing submodules..."
        git submodule update --init --recursive --force
        echo "Submodule status:"
        git submodule status
        
    - name: Verify NDK source files
      run: |
        echo "Checking NDK source files..."
        find core/src/main/jni/ -name "*.c" -o -name "*.cpp" | head -10
        ls -la core/src/main/jni/redsocks/ || echo "redsocks directory not found"
        
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Setup Android SDK
      uses: android-actions/setup-android@v3
      with:
        api-level: 33
        build-tools: 33.0.0
        
    - name: Install NDK manually
      run: |
        $ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager "ndk;27.0.12077973"
        
    - name: Install platform-tools
      run: |
        $ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager "platform-tools"
        
    - name: Verify installation
      run: |
        echo "Android SDK Root: $ANDROID_SDK_ROOT"
        echo "Android Home: $ANDROID_HOME"
        ls -la $ANDROID_SDK_ROOT/platforms/
        ls -la $ANDROID_SDK_ROOT/build-tools/
        ls -la $ANDROID_SDK_ROOT/ndk/
        
    - name: Cache Gradle packages
      uses: actions/cache@v4
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-
          
    - name: Make gradlew executable
      run: chmod +x ./gradlew
      
    - name: Clean build directory
      run: ./gradlew clean
      
    - name: Build with Gradle (Debug)
      run: ./gradlew assembleDebug --stacktrace --info
      
    - name: Upload APK artifact
      uses: actions/upload-artifact@v4
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
