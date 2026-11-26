import org.openqa.selenium.*;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.events.WebDriverListener;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import javax.imageio.ImageIO;

public class TurtleModeListener implements WebDriverListener {
    private final WebDriver rawDriver;
    private final long delayMs;
    private final Path runDir;
    private int step = 1;

    // Чтобы не плодить лишний шум на технических вызовах вроде getWindowHandle
    private static final Set<String> TRACKED_METHODS = Set.of(
            "get", "click", "sendKeys", "clear", "submit", "navigate", "back", "forward", "refresh"
    );

    public TurtleModeListener(WebDriver rawDriver, long delayMs) {
        this.rawDriver = rawDriver;
        this.delayMs = delayMs;
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
        this.runDir = Path.of("report_screens", "run_" + timestamp);
        try {
            Files.createDirectories(runDir);
        } catch (IOException ignored) {}
    }

    private void shot(String tag) {
        int currentStep = step++;
        try {
            if (delayMs > 0) {
                Thread.sleep(delayMs);
            }
            try {
                File src = ((TakesScreenshot) rawDriver).getScreenshotAs(OutputType.FILE);
                String fileName = String.format("step_%03d_%s.png", currentStep, tag);
                File out = runDir.resolve(fileName).toFile();
                FileHandler.copy(src, out);
            } catch (UnhandledAlertException alertEx) {
                File out = runDir.resolve(String.format("step_%03d_alert_%s.png", currentStep, tag)).toFile();
                captureDesktop(out);
            }
        } catch (Exception e) {
            System.out.println("[TurtleMode] Ошибка на шаге " + currentStep + " (" + tag + "): " + e.getMessage());
        }
    }

    private void highlight(WebElement element) {
        try {
            ((JavascriptExecutor) rawDriver).executeScript(
                    "arguments[0].style.border='3px solid red'", element
            );
        } catch (Exception ignored) {}
    }

    private boolean captureDesktop(File outFile) {
        try {
            Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage img = new Robot().createScreenCapture(rect);
            ImageIO.write(img, "png", outFile);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    // --- До действия ---

    @Override
    public void beforeGet(WebDriver driver, String url) {
        shot("before_get");
    }

    @Override
    public void beforeClick(WebElement element) {
        highlight(element);
        shot("before_click");
    }

    @Override
    public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
        highlight(element);
        shot("before_sendKeys");
    }

    @Override
    public void beforeClear(WebElement element) {
        highlight(element);
        shot("before_clear");
    }

    @Override
    public void beforeSubmit(WebElement element) {
        highlight(element);
        shot("before_submit");
    }

    // --- После действия ---

    @Override
    public void afterGet(WebDriver driver, String url) {
        shot("after_get");
    }

    @Override
    public void afterClick(WebElement element) {
        shot("after_click");
    }

    @Override
    public void afterSendKeys(WebElement element, CharSequence... keysToSend) {
        shot("after_sendKeys");
    }

    @Override
    public void afterClear(WebElement element) {
        shot("after_clear");
    }

    @Override
    public void afterSubmit(WebElement element) {
        shot("after_submit");
    }

    // --- Любые другие вызовочные методы драйвера/элемента при необходимости ---

    @Override
    public void afterAnyCall(Object target, Method method, Object[] args, Object result) {
        String name = method.getName();
        if (TRACKED_METHODS.contains(name)
                && !name.equals("get") && !name.equals("click")
                && !name.equals("sendKeys") && !name.equals("clear") && !name.equals("submit")) {
            shot("after_" + name);
        }
    }
}