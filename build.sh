for lab in $(ls); do
  if [ -d $lab ]; then
    cd $lab
    ./gradlew build
    cd ..
  fi
done
