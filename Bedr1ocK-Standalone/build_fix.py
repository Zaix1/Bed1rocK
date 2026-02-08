import os
import shutil
import subprocess

def run_command(cmd):
    print(f"Executing: {cmd}")
    return subprocess.run(cmd, shell=True)

def cleanup():
    print("--- Cleaning up junk ---")
    folders = ['.gradle', 'build', 'app/build']
    for folder in folders:
        if os.path.exists(folder):
            shutil.rmtree(folder)
            print(f"Deleted {folder}")

def create_gradle_properties():
    print("--- Writing strict gradle.properties ---")
    props = (
        "android.useAndroidX=true\n"
        "android.enableJetifier=true\n"
        "org.gradle.jvmargs=-Xmx2048m\n"
        "org.gradle.configuration-cache=false\n"
        "org.gradle.configureondemand=false\n"
        "org.gradle.parallel=false\n"
    )
    with open("gradle.properties", "w") as f:
        f.write(props)

def main():
    # 1. Kill any existing daemons
    run_command("./gradlew --stop")
    
    # 2. Hard wipe caches
    cleanup()
    
    # 3. Rewrite properties to disable the "Observation" features
    create_gradle_properties()
    
    # 4. Final attempt with total isolation
    print("--- Starting Isolated Build ---")
    # We use --no-daemon and --no-build-cache to prevent "Mutation" errors
    result = run_command("./gradlew :app:assembleDebug --no-daemon --no-build-cache --offline")
    
    if result.returncode != 0:
        print("\n[!] Build failed again. Attempting online build...")
        run_command("./gradlew :app:assembleDebug --no-daemon --no-build-cache")

if __name__ == "__main__":
    main()