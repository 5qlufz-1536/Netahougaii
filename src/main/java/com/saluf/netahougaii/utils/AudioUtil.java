package com.saluf.netahougaii.utils;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AudioUtil {

    private Clip audioClip;
    private FloatControl volumeControl;

    public void loadAudio(String filePath) {
        try {
            URL soundFile = getClass().getClassLoader().getResource(filePath);
            if (soundFile == null) {
                throw new IllegalArgumentException("Audio file not found: " + filePath);
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            volumeControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ignored) {
        }
    }

    public void play() {
        if (audioClip != null) {
            audioClip.start();
        }
    }

    public void stop() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
        }
    }

    public void reset() {
        if (audioClip != null) {
            audioClip.setFramePosition(0);
        }
    }

    public void setVolume(float volume) {
        if (volumeControl != null) {
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();

            volumeControl.setValue(Math.max(min, Math.min(max, volume)));
        }
    }
}
