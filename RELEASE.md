# release checklist
1. make changes
2. update `build.gradle` for jist.app
    * versionCode++
    * versionName semver++
3. write changelog
3. `build > generate signed bundle`
    * upload `app/release/app-release.aab`
4. git commit
5. `git tag <versionName>`
6. `git push && git push origin <versionName>`

# CHANGELOG
## 1.3
* add 10px left-padding to note body to make selecting text easier at the beginning of the line.
* add monochrome app icon for adaptive themes.
## 1.2
* better scrolling for long notes
## 1.1
* fixed list refresh bug
## 1.0
* initial release
