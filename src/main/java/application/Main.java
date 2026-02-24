package application;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Pixel Adventure");

        // เริ่มเกมโดยเรียกหน้า StartScreen
        new StartScreen(primaryStage).show();

        primaryStage.show();
    }

    // 🌟 พระเอกของเรา: เมธอดสำหรับดึงฟอนต์พิกเซลไปใช้ทั้งเกม
    public static Font getPixelFont(double size) {
        try {
            // โหลดฟอนต์จาก resources/font/pixel.ttf
            Font pixelFont = Font.loadFont(Main.class.getResourceAsStream("/font/pixel.ttf"), size);

            // ถ้าโหลดสำเร็จให้คืนค่าฟอนต์นี้กลับไป
            if (pixelFont != null) {
                return pixelFont;
            } else {
                System.out.println("⚠️ โหลดฟอนต์พิกเซลไม่ได้ (ไฟล์อาจผิดพลาด) ใช้ฟอนต์ระบบแทน");
                return Font.font("Arial", size);
            }
        } catch (Exception e) {
            System.out.println("⚠️ หาไฟล์ฟอนต์ไม่เจอ (เช็คชื่อโฟลเดอร์และไฟล์) ใช้ฟอนต์ระบบแทน");
            return Font.font("Arial", size);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}