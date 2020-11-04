# soundcrowd-plugin-youtube

YouTube plugin for soundcrowd

[![Build Status](https://travis-ci.org/soundcrowd/soundcrowd-plugin-youtube.svg?branch=master)](https://travis-ci.org/soundcrowd/soundcrowd-plugin-youtube)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0df9a1131c1b4775a51cdaf253c97c19)](https://www.codacy.com/app/tiefensuche/soundcrowd-plugin-youtube?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=soundcrowd/soundcrowd-plugin-youtube&amp;utm_campaign=Badge_Grade)
[![Maintainability](https://api.codeclimate.com/v1/badges/ab37a3cea675fbdfb1e8/maintainability)](https://codeclimate.com/github/soundcrowd/soundcrowd-plugin-youtube/maintainability)
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