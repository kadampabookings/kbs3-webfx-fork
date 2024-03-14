package dev.webfx.kit.launcher.spi.impl.openjfx;

import dev.webfx.kit.launcher.spi.FastPixelReaderWriter;
import dev.webfx.platform.os.OperatingSystem;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import dev.webfx.kit.launcher.spi.impl.base.WebFxKitLauncherProviderBase;
import dev.webfx.platform.util.function.Factory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class JavaFxWebFxKitLauncherProvider extends WebFxKitLauncherProviderBase {

    private static List<Runnable> onReadyRunnables = new ArrayList<>();
    private static Factory<Application> applicationFactory;

    private static Stage primaryStage;
    private static Application application;

    public JavaFxWebFxKitLauncherProvider() {
        super(true);
    }

    @Override
    public double getVerticalScrollbarExtraWidth() {
        // OpenJFX has a 15px bar width on desktops, but Gluon provides a perfect scrollbar on mobiles (not introducing extra space)
        return OperatingSystem.isMobile() ? 0 : 15;
    }

    @Override
    public HostServices getHostServices() {
        return getApplication().getHostServices();
    }

    @Override
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public void launchApplication(Factory<Application> applicationFactory, String... args) {
        JavaFxWebFxKitLauncherProvider.applicationFactory = applicationFactory;
        new Thread(() -> {
            Application.launch(FxKitWrapperApplication.class, args);
            System.exit(0);
        }).start();
    }

    private static void onJavaFxToolkitReady() {
        // Activating SVG support
        // de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory.install();
        executeReadyRunnables();
    }

    @Override
    public boolean isReady() {
        return onReadyRunnables == null;
    }

    @Override
    public void onReady(Runnable runnable) {
        synchronized (JavaFxWebFxKitLauncherProvider.class) {
            if (onReadyRunnables != null)
                onReadyRunnables.add(runnable);
            else
                super.onReady(runnable);
        }
    }

    private static void executeReadyRunnables() {
        synchronized (JavaFxWebFxKitLauncherProvider.class) {
            if (onReadyRunnables != null) {
                List<Runnable> runnables = onReadyRunnables;
                onReadyRunnables = null;
                //runnables.forEach(Runnable::run); doesn't work on Android
                for (Runnable runnable : runnables)
                    runnable.run();
            }
        }
    }

    public static class FxKitWrapperApplication extends Application {

        @Override
        public void init() throws Exception {
            if (applicationFactory != null)
                application = applicationFactory.create();
            if (application != null) {
                //ParametersImpl.registerParameters(application, getParameters());
                application.init();
            }
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            JavaFxWebFxKitLauncherProvider.primaryStage = primaryStage;
            onJavaFxToolkitReady();
            if (application != null)
                application.start(primaryStage);
        }

    }

    @Override
    public FastPixelReaderWriter getFastPixelReaderWriter(Image image) {
        return new OpenJFXFastPixelReaderWriter(image);
    }

    private final Text measurementText = new Text();
    @Override
    public Bounds measureText(String text, Font font) {
        measurementText.setText(text);
        measurementText.setFont(font);
        return measurementText.getLayoutBounds();
    }

    @Override
    public double measureBaselineOffset(Font font) {
        measurementText.setText("Baseline text");
        measurementText.setFont(font);
        return measurementText.getBaselineOffset();
    }
}
