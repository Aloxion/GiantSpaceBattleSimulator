package gsbs.common.components;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.nanovg.NanoVG.NVG_IMAGE_NEAREST;
import static org.lwjgl.nanovg.NanoVG.nvgCreateImageMem;

public class Sprite extends Component {
    // Prevent loading sprites multiple times by mapping URLs to sprite id's
    private static final Map<URL, Integer> spriteList = new HashMap<>();

    private final int width;
    private final int height;
    private final URL spriteURL;

    public Sprite(URL spriteURL, int width, int height) {
        this.spriteURL = spriteURL;
        this.width = width;
        this.height = height;
    }

    public int getSpriteId(long nvgContext) {
        if (this.spriteURL == null) {
            return -1;
        }

        if (Sprite.spriteList.containsKey(this.spriteURL)) {
            return spriteList.get(this.spriteURL);
        }

        // Read the URL
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (InputStream stream = this.spriteURL.openStream()) {
            byte[] chunk = new byte[4096];
            int bytesRead;

            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        var textureData = outputStream.toByteArray();
        var textureBuffer = ByteBuffer.allocateDirect(textureData.length);
        textureBuffer.put(textureData);
        textureBuffer.flip();

        int spriteId = nvgCreateImageMem(nvgContext, NVG_IMAGE_NEAREST, textureBuffer);
        Sprite.spriteList.put(spriteURL, spriteId);

        return spriteId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
