package ru.vova.game.minesweeper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class I18NProvider extends ResourceBundle.Control {
    public static class XMLResourceBundleControl extends ResourceBundle.Control {
        private static String PROP = "properties";

        public List<String> getFormats(String baseName) {
            return Collections.singletonList(PROP);
        }

        public ResourceBundle newBundle(String baseName, Locale locale,
                                        String format, ClassLoader loader, boolean reload)
                throws IOException {
            if ((baseName == null) || (locale == null) || (format == null)
                    || (loader == null)) {
                throw new NullPointerException();
            }
            ResourceBundle bundle = null;
            if (format.equals(PROP)) {
                String bundleName = toBundleName(baseName, locale);
                String resourceName = toResourceName(bundleName, format);
                try (InputStream stream = I18NProvider.class.getResourceAsStream(resourceName)) {
                    bundle = new PropResourceBundle(stream);

                }
            }
            return bundle;
        }
    }

    private static class PropResourceBundle extends ResourceBundle {
        private Properties props;

        PropResourceBundle(InputStream stream) throws IOException {
            props = new Properties();
            props.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
        }

        protected Object handleGetObject(String key) {
            return props.getProperty(key);
        }

        public Enumeration<String> getKeys() {
            Set<String> handleKeys = props.stringPropertyNames();
            return Collections.enumeration(handleKeys);
        }
    }

    private ResourceBundle i18n;


    public String getString(String key) {
        return i18n.getString(key);
    }

    private I18NProvider(Locale locale) {
        i18n = ResourceBundle.getBundle("i18n", locale, new XMLResourceBundleControl());
    }


    private static final I18NProvider instance = new I18NProvider(Locale.getDefault());

    public void changeLocale(Locale locale) {
        instance.i18n = ResourceBundle.getBundle("i18n", locale, new XMLResourceBundleControl());
    }

    public static I18NProvider getInstance() {
        return instance;
    }
}
