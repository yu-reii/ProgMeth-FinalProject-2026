package application;

import javafx.scene.media.AudioClip; // 🌟 อย่าลืม Import ตัวนี้เพิ่ม
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager {
    private static MediaPlayer mediaPlayer;
    private static String currentTrack = "";

    // ----- สำหรับเล่นเพลงพื้นหลัง (เล่นวนลูปและทับเพลงเก่า) -----
    public static void playMusic(String fileName) {
        if (currentTrack.equals(fileName)) return;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            URL resource = SoundManager.class.getResource("/sound/" + fileName);
            if (resource == null) {
                System.out.println("❌ ไม่พบไฟล์เสียง: /sound/" + fileName);
                return;
            }

            Media media = new Media(resource.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0.5);
            mediaPlayer.play();
            currentTrack = fileName;

        } catch (Exception e) {
            System.out.println("❌ เล่นเพลงไม่ได้: " + e.getMessage());
        }
    }

    // 🌟 ฟังก์ชันใหม่! สำหรับเล่นเสียงเอฟเฟกต์สั้นๆ (เล่นทับเพลงพื้นหลังได้เลย)
    public static void playSFX(String fileName) {
        try {
            URL resource = SoundManager.class.getResource("/sound/" + fileName);
            if (resource == null) {
                System.out.println("❌ ไม่พบไฟล์เสียงเอฟเฟกต์: /sound/" + fileName);
                return;
            }

            // AudioClip จะไม่ไปยุ่งกับ MediaPlayer ของเพลงพื้นหลัง
            AudioClip clip = new AudioClip(resource.toExternalForm());
            clip.setVolume(0.8); // ปรับความดังของลูกเต๋า (0.0 ถึง 1.0)
            clip.play();

        } catch (Exception e) {
            System.out.println("❌ เล่นเสียงเอฟเฟกต์ไม่ได้: " + e.getMessage());
        }
    }
}