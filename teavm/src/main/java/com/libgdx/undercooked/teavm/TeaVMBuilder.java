package com.libgdx.undercooked.teavm;

import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.AssetFilter;
import com.github.xpenatan.gdx.backends.teavm.config.AssetFilterOption;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass;
import java.io.File;
import java.io.IOException;
import org.teavm.tooling.TeaVMTool;
import org.teavm.vm.TeaVMOptimizationLevel;

/** Builds the browser version into teavm/build/dist/webapp. */
@SkipClass
public class TeaVMBuilder {
    private static final String[] EXCLUDED_FOLDERS = {
        "github-icons", "ui/images", "sprites/Chef1", "sprites/Chef2", "sprites_raw",
        "floating_icons/images", "tilesets/mapsButtons", "maps/extra", "poof", "fx_sprites/poof",
    };
    private static final String[] EXCLUDED_FILES = {
        "audio/map6_sound.mp3", "maps/map6.tmx", "maps/extra.tmx",
        "tilesets/Modern_Exteriors_Complete_Tileset_32x32.png", "maps/Modern_Exteriors_Complete_Tileset_32x32.tsx",
        "tilesets/Room_Builder_32x32 - Copy.png", "tilesets/houses.png", "tilesets/houses2.png",
    };

    public static void main(String[] args) throws IOException {
        TeaBuildConfiguration config = new TeaBuildConfiguration();
        AssetFileHandle assets = new AssetFileHandle("../assets");
        config.assetsPath.add(assets);
        // The browser downloads every asset before the game starts, all at once. With too many
        // files Chrome refuses some of the downloads (ERR_INSUFFICIENT_RESOURCES) and the game then
        // can't find them, so leave out everything the game never loads: loose animation frames
        // that are already packed into .atlas files, editor files, unused maps and README images.
        // If you add a new asset folder that the game loads directly, make sure it isn't listed here.
        config.assetFilter = new AssetFilter() {
            @Override
            public boolean accept(String file, boolean isDirectory, AssetFilterOption op) {
                String f = file.replace('\\', '/');
                int i = f.indexOf("/webapp/assets/");
                String rel = i >= 0 ? f.substring(i + "/webapp/assets/".length()) : f;
                if (rel.equals("sprites/Chef2/idle_down_01.png") || rel.equals("sprites_raw/login_box2.png")) {
                    return true;
                }
                for (String dir : EXCLUDED_FOLDERS) {
                    if (rel.equals(dir) || rel.startsWith(dir + "/")) {
                        // Keep the folder itself if one of the kept files lives in it.
                        return isDirectory && (dir.equals("sprites/Chef2") || dir.equals("sprites_raw"));
                    }
                }
                if (isDirectory) return true;
                for (String name : EXCLUDED_FILES) {
                    if (rel.equals(name)) return false;
                }
                return !(rel.endsWith(".aseprite") || rel.endsWith(".tpproj") || rel.endsWith(".scmp")
                    || rel.endsWith(".gif") || rel.endsWith(".wav") || rel.endsWith(".jar")
                    || rel.endsWith("assets.txt"));
            }
        };
        config.webappPath = new File("build/dist").getCanonicalPath();
        config.htmlTitle = "Undercooked";
        config.htmlWidth = 1400;
        config.htmlHeight = 800;
        config.showLoadingLogo = false;

        TeaVMTool tool = TeaBuilder.config(config);
        tool.setMainClass(TeaVMLauncher.class.getName());
        tool.setOptimizationLevel(TeaVMOptimizationLevel.ADVANCED);
        tool.setObfuscated(true);
        if (!TeaBuilder.build(tool)) {
            throw new IllegalStateException("TeaVM build failed, see the errors above.");
        }
        scaleCanvasToWindow(new File(config.webappPath, "webapp/index.html"));
    }

    /**
     * The game always runs at 1400x800 (every menu is laid out for that size), but the page used to
     * show it at that exact size, with black bars or scrollbars on other screens. This makes the
     * canvas scale up or down to fill the browser window while keeping its shape, with crisp pixels.
     * Mouse input still lines up because the backend converts clicks from page to canvas pixels.
     */
    private static void scaleCanvasToWindow(File index) throws IOException {
        if (!index.exists()) {
            System.err.println("index.html not found at " + index + ", canvas scaling skipped");
            return;
        }
        String html = new String(java.nio.file.Files.readAllBytes(index.toPath()), java.nio.charset.StandardCharsets.UTF_8);
        String css = "<style>\n"
            + "      body { display: flex; background: #1b1820; }\n"
            + "      body > div { display: flex; width: 100vw; height: 100vh; align-items: center; justify-content: center; }\n"
            + "      #canvas { display: block; width: min(100vw, calc(100vh * 1.75)); height: min(100vh, calc(100vw / 1.75));\n"
            + "                image-rendering: pixelated; image-rendering: crisp-edges; outline: none; }\n"
            + "    </style>\n</head>";
        html = html.replaceFirst("</head>", java.util.regex.Matcher.quoteReplacement(css));
        java.nio.file.Files.write(index.toPath(), html.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
