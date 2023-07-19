@echo off

pushd %~dp0\..\..\..\..\Projects\Personal\ImgResizer\ImgResizer
call gradlew fatJar sourcesJar --console=plain
popd
