# Sound Files for Focus Timer App

This directory should contain the actual audio files for the timer notifications. Currently, placeholder text files are used to prevent build errors.

## Required Sound Files

Replace the `.txt` files with actual audio files:

1. **gentle_chime.mp3** - Soft, pleasant chime sound
2. **bell.mp3** - Traditional bell sound  
3. **notification.mp3** - Standard notification sound
4. **nature.mp3** - Natural ambient sound (birds, water, etc.)

## Audio Specifications

- **Format**: MP3 or WAV
- **Duration**: 1-3 seconds for chimes/bells, up to 5 seconds for nature sounds
- **Sample Rate**: 44.1 kHz recommended
- **Bit Rate**: 128 kbps minimum for MP3
- **Volume**: Normalized to prevent audio clipping

## Usage

These sounds are played when:
- Focus session starts (start sound setting)
- Focus session ends (end sound setting) 
- Micro-break begins
- Long break begins

Users can select different sounds for start and end events in the Settings screen.
