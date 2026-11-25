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
    public static void main(String[] args) throws IOException {
        TeaBuildConfiguration config = new TeaBuildConfiguration();
        AssetFileHandle assets = new AssetFileHandle("../assets");
        config.assetsPath.add(assets);
        // The browser downloads every asset before the game starts, so leave out everything the
        // game never loads: editor source files, README images and the audio originals.
        config.assetFilter = new AssetFilter() {
            @Override
            public boolean accept(String file, boolean isDirectory, AssetFilterOption op) {
                String f = file.replace('\\', '/');
                if (f.contains("/github-icons") || f.contains("/.")) return false;
                if (isDirectory) return true;
                return !(f.endsWith(".aseprite") || f.endsWith(".tpproj") || f.endsWith(".scmp")
                    || f.endsWith(".gif") || f.endsWith(".wav") || f.endsWith(".jar")
                    || f.endsWith("assets.txt"));
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
    }
}
