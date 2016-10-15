node {
   stage 'Checkout'
   checkout([$class: 'GitSCM', branches: [[name: '*/develop']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/mufela/WordPress-Android.git']]])

   stage 'Build'
   sh "./gradlew clean assembleDebug"

   stage 'Test'
   sh "./gradlew check"
}