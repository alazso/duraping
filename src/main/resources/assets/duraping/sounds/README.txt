Placeholder for sound files.

Required files:
- warn.ogg (warning alert sound)
- critical.ogg (critical alert sound)

These should be short (< 1 second) Ogg Vorbis audio files.
For now, you can use silent/minimal sounds or custom alert tones.

To generate silent placeholder files using ffmpeg:
  ffmpeg -f lavfi -i anullsrc=r=44100:cl=mono -t 0.5 -acodec libvorbis warn.ogg
  ffmpeg -f lavfi -i anullsrc=r=44100:cl=mono -t 0.5 -acodec libvorbis critical.ogg

