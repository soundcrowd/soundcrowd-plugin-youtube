# soundcrowd-plugin-youtube

[![android](https://github.com/soundcrowd/soundcrowd-plugin-youtube/actions/workflows/android.yml/badge.svg)](https://github.com/soundcrowd/soundcrowd-plugin-youtube/actions/workflows/android.yml)
[![GitHub release](https://img.shields.io/github/release/soundcrowd/soundcrowd-plugin-youtube.svg)](https://github.com/soundcrowd/soundcrowd-plugin-youtube/releases)
[![GitHub](https://img.shields.io/github/license/soundcrowd/soundcrowd-plugin-youtube.svg)](LICENSE)

This soundcrowd plugin adds basic YouTube support (audio only). It allows you to browse the initial trending page and to search for videos. The plugin greatly depends on the `NewPipeExtractor` library, thanks!

## Building

    $ git clone --recursive https://github.com/soundcrowd/soundcrowd-plugin-youtube
    $ cd soundcrowd-plugin-youtube
    $ ./gradlew assembleDebug

Install via ADB:

    $ adb install youtube/build/outputs/apk/debug/youtube-debug.apk

## License

Licensed under GPLv3.

## Dependencies

- [NewPipeExtractor](https://github.com/TeamNewPipe/NewPipeExtractor) - GPLv3