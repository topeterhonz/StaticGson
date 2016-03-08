
check:
	./gradlew clean check bintrayUpload

publish: check
	./gradlew -PdryRun=false --info processor:bintrayUpload || echo 'Failure!'
	./gradlew -PdryRun=false --info library:bintrayUpload || echo 'Failure!'
	./gradlew releng
