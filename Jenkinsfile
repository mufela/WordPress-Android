node {

   stage 'Build'
   sh "./gradlew clean assembleDebug"

   stage 'Test'
   sh "./gradlew check"
}