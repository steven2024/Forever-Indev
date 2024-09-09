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
    private Mixer.Info currentMixerInfo;
    private Minecraft mc;

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

    private String currentMusicCategory = ""; // Variable to track the current music category (e.g., "hell" or "calm")

    public void stopBackgroundMusic() {
        if (this.sndSystem != null && this.sndSystem.playing("BgMusic")) {
            this.sndSystem.stop("BgMusic");
            currentMusicCategory = ""; // Reset the current category when stopping music
        }
    }

    public void playRandomMusicIfReady() {
        if (this.loaded && this.options.music) {
            int worldLevelType = World.levelType;
            String soundPrefix;

            // Determine the music category based on the world level type
            switch (worldLevelType) {
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

            stopBackgroundMusic(); // Stop the current music before starting a new one

            System.out.println("Selected sound prefix: " + soundPrefix);

            try {
                List<File> files = listFilesByPrefix(soundPrefix);
                if (files.isEmpty()) {
                    System.out.println("No sounds found for prefix: " + soundPrefix);
                    return;
                }

                // Pick a random sound file from the list
                File soundFile = files.get(new Random().nextInt(files.size()));
                SoundPoolEntry entry = new SoundPoolEntry(soundFile.getName(), soundFile.toURI().toURL());

                if (entry != null) {
                    System.out.println("Playing sound: " + entry.soundName);
                    this.sndSystem.newStreamingSource(true, "BgMusic", entry.soundUrl, entry.soundName, false, 0.0F, 0.0F, 0.0F, 0, 0.0F);
                    this.sndSystem.setVolume("BgMusic", 1.0F);
                    this.sndSystem.play("BgMusic");

                    // Update the current category to the one just started
                    currentMusicCategory = soundPrefix;
                } else {
                    System.out.println("No sound entry created.");
                }
            } catch (MalformedURLException e) {
                System.err.println("Error creating URL for sound file.");
                e.printStackTrace();
            }
        } else {
            stopBackgroundMusic(); // Stop music if conditions aren't met
        }
    }

    private List<File> listFilesByPrefix(String prefix) {
        File soundDir = new File(mc.mcDataDir, "resources/music");
        if (!soundDir.exists() || !soundDir.isDirectory()) {
            System.err.println("Sound directory does not exist or is not a directory.");
            return new ArrayList<>();
        }

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(prefix) && name.endsWith(".ogg");
            }
        };

        File[] matchingFiles = soundDir.listFiles(filter);
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
