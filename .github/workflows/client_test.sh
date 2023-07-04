#!/usr/bin/env bash

tmux new-session -d -s TestRecorder 'ffmpeg -f x11grab -s 854x480 -i :99 -codec:v libx264 -r 5 run/screenshots/test_recording.mp4'
./gradlew runProductionAutoTestClient --stacktrace --warning-mode=fail
tmux send-keys -t TestRecorder q
