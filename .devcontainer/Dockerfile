# Use the standard Java dev container image
FROM mcr.microsoft.com/devcontainers/java:17

# Set environment variables for Android SDK
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH="${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools"

# Install required system tools
RUN apt-get update && apt-get install -y unzip wget

# Download and install Android Command Line Tools
# (Note: Check for the latest version number on developer.android.com if this becomes outdated)
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools \
    && wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O android_tools.zip \
    && unzip -q android_tools.zip -d ${ANDROID_HOME}/cmdline-tools \
    && mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest \
    && rm android_tools.zip

# Accept licenses and install basic SDK packages (Build Tools, Platforms)
RUN yes | sdkmanager --licenses \
    && sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
