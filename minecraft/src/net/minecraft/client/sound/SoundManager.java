package net.minecraft.client.sound;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.game.entity.EntityLiving;
import net.minecraft.game.level.World;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;
import util.MathHelper;

public final class SoundManager {
    private SoundSystem sndSystem;
    private SoundPool soundPoolSounds = new SoundPool();
    private SoundPool soundPoolMusic = new SoundPool();
    private int latestSoundID = 0;
    private GameSettings options;
    private boolean loaded = false;
    private boolean deathMusicPlaying = false; // Track if death music is playing
    private boolean transitioningToDeath = false; // Track if we're transitioning to death music
    private Mixer.Info currentMixerInfo;
    private Minecraft mc;
    private String currentMusicCategory = ""; // Track the current music category
    private boolean isFadingOut = false; // Flag for fade-out
    private boolean isFadingIn = false;  // Flag for fade-in

    public SoundManager(Minecraft minecraft) {
        this.currentMixerInfo = AudioSystem.getMixer(null).getMixerInfo();
        this.mc = minecraft;
    }

    public final void loadSoundSettings(GameSettings settings) {
        this.options = settings;
        if (!this.loaded && (settings.sound || settings.music)) {
            this.tryToSetLibraryAndCodecs();
        }
    }

    private void tryToSetLibraryAndCodecs() {
        try {
            if (this.sndSystem != null) {
                this.sndSystem.cleanup();
            }

            SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
            SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
            SoundSystemConfig.setCodec("wav", CodecWav.class);
            this.sndSystem = new SoundSystem();
            System.out.println("Sound system initialized with OpenAL and codecs.");
        } catch (Throwable e) {
            System.err.println("Error linking with the LibraryJavaSound plug-in");
            e.printStackTrace();
        }

        this.loaded = true;
    }

    public void reloadSoundSystem() {
        if (this.loaded) {
            this.sndSystem.cleanup();
            this.loaded = false;
        }
        this.tryToSetLibraryAndCodecs();
    }

    public void checkForAudioDeviceChange() {
        Mixer.Info newMixerInfo = AudioSystem.getMixer(null).getMixerInfo();
        if (!newMixerInfo.equals(currentMixerInfo)) {
            System.out.println("Audio device change detected. Restarting sound system...");
            this.reloadSoundSystem();
            this.currentMixerInfo = newMixerInfo;
        }
    }

    public final void closeMinecraft() {
        if (this.loaded) {
            this.sndSystem.cleanup();
        }
    }

    public final void addSound(String name, File file) {
        this.soundPoolSounds.addSound(name, file);
    }

    public final void addMusic(String name, File file) {
        this.soundPoolMusic.addSound(name, file);
    }

    // Stop background music
    public void stopBackgroundMusic() {
        if (this.sndSystem != null && this.sndSystem.playing("BgMusic")) {
            this.sndSystem.stop("BgMusic");
            currentMusicCategory = ""; // Reset the current category when stopping music
        }
    }

    // Fade out the current music before switching to death music
    public void fadeOutToDeathMusic() {
        if (this.sndSystem != null && this.sndSystem.playing("BgMusic")) {
            new Thread(() -> {
                float volume = 1.0F;

                // Gradually reduce the volume over 2 seconds (2000ms)
                for (int i = 0; i < 20; i++) {
                    volume -= 0.05F; // Reduce volume in steps
                    this.sndSystem.setVolume("BgMusic", volume);
                    try {
                        Thread.sleep(100); // Pause for 100ms between steps
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Stop the music once volume is completely down
                this.sndSystem.stop("BgMusic");
                currentMusicCategory = ""; // Reset the current category

                // Play death music after fade-out
                playDeathMusic();
            }).start();
        } else {
            // If no background music is playing, directly start death music
            playDeathMusic();
        }
    }

    // Play death music
    public void playDeathMusic() {
        try {
            File deathFile = new File(mc.mcDataDir, "resources/music/death.ogg");
            if (deathFile.exists()) {
                SoundPoolEntry deathEntry = new SoundPoolEntry(deathFile.getName(), deathFile.toURI().toURL());
                this.sndSystem.newSource(false, "DeathMusic", deathEntry.soundUrl, deathEntry.soundName, true, 0.0F, 0.0F, 0.0F, 0, 0.0F); // Loop the death music
                this.sndSystem.setVolume("DeathMusic", 1.0F);
                this.sndSystem.play("DeathMusic");
                deathMusicPlaying = true;
            } else {
                System.err.println("Death music file not found.");
            }
        } catch (MalformedURLException e) {
            System.err.println("Error creating URL for death music.");
            e.printStackTrace();
        }
    }

    // Helper method to fade out current music before transitioning to new music
    private void fadeOutCurrentMusic(Runnable afterFade) {
        if (this.sndSystem != null && this.sndSystem.playing("BgMusic") && !isFadingOut) {
            isFadingOut = true; // Mark that we are fading out

            new Thread(() -> {
                float volume = 1.0F;

                // Gradually reduce the volume over 2 seconds (2000ms)
                for (int i = 0; i < 20; i++) {
                    volume -= 0.05F; // Reduce volume in steps
                    this.sndSystem.setVolume("BgMusic", volume);
                    try {
                        Thread.sleep(100); // Pause for 100ms between steps
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Stop the music once volume is completely down
                this.sndSystem.stop("BgMusic");
                currentMusicCategory = ""; // Reset the current category

                // Execute the code to play the new music after fade-out
                afterFade.run();
                isFadingOut = false; // Reset the fade-out flag once complete
            }).start();
        } else {
            // If no background music is playing, directly play the new music
            afterFade.run();
        }
    }

    // Helper method to fade in new music after it's started
    private void fadeInNewMusic() {
        if (!isFadingIn) {
            isFadingIn = true; // Mark that we are fading in

            new Thread(() -> {
                float volume = 0.0F;

                // Gradually increase the volume over 2 seconds (2000ms)
                for (int i = 0; i < 20; i++) {
                    volume += 0.05F; // Increase volume in steps
                    this.sndSystem.setVolume("BgMusic", volume);
                    try {
                        Thread.sleep(100); // Pause for 100ms between steps
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                isFadingIn = false; // Reset the fade-in flag once complete
            }).start();
        }
    }

    public void playRandomMusicIfReady() {
        if (isFadingOut || isFadingIn || !this.loaded || !this.options.music) {
            return; // Exit if music is fading or not ready
        }

        String soundPrefix;

        // Check if player is dead and handle death music
        if (mc.thePlayer != null && mc.thePlayer.health <= 0) {
            if (deathMusicPlaying) {
                return; // Already playing death music, do nothing
            }

            if (!transitioningToDeath) {
                transitioningToDeath = true;
                fadeOutToDeathMusic(); // Fade out current music and play death music
            }
            return;
        }

        // Stop death music if player is alive
        if (deathMusicPlaying) {
            this.sndSystem.stop("DeathMusic");
            deathMusicPlaying = false; // Mark that death music is no longer playing
        }

        // Determine the music category based on the world level type
        int worldLevelTheme = World.levelTheme;
        switch (worldLevelTheme) {
            case 1: // Hell music type
                soundPrefix = "hell";
                break;
            case 4: // Winter music type
                soundPrefix = "winter";
                break;
            default: // Default category for other types
                soundPrefix = "calm";
                break;
        }

        // If the correct category is already playing, do nothing
        if (soundPrefix.equals(currentMusicCategory) && this.sndSystem.playing("BgMusic")) {
            return;
        }

        // Transition to the new music after fade-out
        fadeOutCurrentMusic(() -> {
            try {
                List<File> themedFiles = listFilesByPrefix(soundPrefix);
                List<File> pianoFiles = listPianoTracks();

                if (themedFiles.isEmpty() && pianoFiles.isEmpty()) {
                    System.out.println("No music found for both themes and piano.");
                    return;
                }

                Random rand = new Random();
                File selectedFile;
                String selectedTrack = "";

                // Randomly choose between piano and themed tracks
                if (!pianoFiles.isEmpty() && rand.nextInt(4) == 0) {
                    int pianoTrackNumber = rand.nextInt(pianoFiles.size());
                    selectedFile = pianoFiles.get(pianoTrackNumber);
                    selectedTrack = "piano" + (pianoTrackNumber + 1); // Show track number for piano tracks
                } else if (!themedFiles.isEmpty()) {
                    selectedFile = themedFiles.get(rand.nextInt(themedFiles.size()));
                    selectedTrack = soundPrefix;
                } else {
                    return; // No valid tracks found
                }

                SoundPoolEntry entry = new SoundPoolEntry(selectedFile.getName(), selectedFile.toURI().toURL());
                if (entry != null) {
                    this.sndSystem.newStreamingSource(true, "BgMusic", entry.soundUrl, entry.soundName, false, 0.0F, 0.0F, 0.0F, 0, 0.0F);
                    this.sndSystem.setVolume("BgMusic", 0.0F); // Start at 0 volume for fade-in
                    this.sndSystem.play("BgMusic");

                    // Print the selected track with the prefix and track number if it's piano
                    System.out.println("Playing track: " + selectedTrack + " - " + entry.soundName);

                    currentMusicCategory = soundPrefix;

                    // Start fading in the new music
                    fadeInNewMusic();
                }
            } catch (MalformedURLException e) {
                System.err.println("Error creating URL for music file.");
                e.printStackTrace();
            }
        });
    }

    // List files for specific music prefix (e.g., calm, hell, winter)
    private List<File> listFilesByPrefix(String prefix) {
        File soundDir = new File(mc.mcDataDir, "resources/music");
        if (!soundDir.exists() || !soundDir.isDirectory()) {
            System.err.println("Sound directory does not exist or is not a directory.");
            return new ArrayList<>();
        }

        FilenameFilter filter = (dir, name) -> name.startsWith(prefix) && name.endsWith(".ogg");
        File[] matchingFiles = soundDir.listFiles(filter);
        List<File> files = new ArrayList<>();
        if (matchingFiles != null) {
            for (File file : matchingFiles) {
                files.add(file);
            }
        }

        return files;
    }

    // List piano tracks
    private List<File> listPianoTracks() {
        File pianoDir = new File(mc.mcDataDir, "resources/newmusic");
        if (!pianoDir.exists() || !pianoDir.isDirectory()) {
            System.err.println("Piano track directory does not exist or is not a directory.");
            return new ArrayList<>();
        }

        FilenameFilter filter = (dir, name) -> name.startsWith("piano") && name.endsWith(".ogg");
        File[] matchingFiles = pianoDir.listFiles(filter);
        List<File> files = new ArrayList<>();
        if (matchingFiles != null) {
            for (File file : matchingFiles) {
                files.add(file);
            }
        }

        return files;
    }

    public final void setListener(EntityLiving entity, float partialTicks) {
        if (this.loaded && this.options.sound) {
            if (entity != null) {
                float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
                float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
                float posX = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
                float posY = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks;
                float posZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
                float x = MathHelper.cos(-yaw * ((float)Math.PI / 180.0F) - (float)Math.PI);
                float y = MathHelper.sin(-yaw * ((float)Math.PI / 180.0F) - (float)Math.PI);
                float z = MathHelper.cos(-pitch * ((float)Math.PI / 180.0F));
                pitch = MathHelper.sin(-pitch * ((float)Math.PI / 180.0F));
                float orientationX = -y * z;
                float orientationY = -x * z;
                y = -y * pitch;
                x = -x * pitch;
                this.sndSystem.setListenerPosition(posX, posY, posZ);
                this.sndSystem.setListenerOrientation(orientationX, pitch, orientationY, y, z, x);
            }
        }
    }

    public final void playSound(String name, float x, float y, float z, float volume, float pitch) {
        if (this.loaded && this.options.sound) {
            SoundPoolEntry entry = this.soundPoolSounds.getRandomSoundFromSoundPool(name);
            if (entry != null && volume > 0.0F) {
                this.latestSoundID = (this.latestSoundID + 1) % 256;
                String soundID = "sound_" + this.latestSoundID;
                float maxDistance = 16.0F;
                if (volume > 1.0F) {
                    maxDistance = 16.0F * volume;
                }

                this.sndSystem.newSource(volume > 1.0F, soundID, entry.soundUrl, entry.soundName, false, x, y, z, 2, maxDistance);
                this.sndSystem.setPitch(soundID, pitch);
                if (volume > 1.0F) {
                    volume = 1.0F;
                }

                this.sndSystem.setVolume(soundID, volume);
                this.sndSystem.play(soundID);
            }
        }
    }

    public final void playSoundFX(String name, float x, float y) {
        if (this.loaded && this.options.sound) {
            SoundPoolEntry entry = this.soundPoolSounds.getRandomSoundFromSoundPool(name);
            if (entry != null) {
                this.latestSoundID = (this.latestSoundID + 1) % 256;
                String soundID = "sound_" + this.latestSoundID;
                this.sndSystem.newSource(false, soundID, entry.soundUrl, entry.soundName, false, 0.0F, 0.0F, 0.0F, 0, 0.0F);
                this.sndSystem.setPitch(soundID, 1.0F);
                this.sndSystem.setVolume(soundID, 0.25F);
                this.sndSystem.play(soundID);
            }
        }
    }
}
