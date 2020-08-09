package ru.homyakin.gwent.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtils {
    private final static Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    public static Optional<InputStream> combineAvatarAndBorder(String avatarLink, Optional<String> borderLink) {
        try {
            if (borderLink.isEmpty()) {
                return Optional.of(new URL(avatarLink).openStream());
            }
            var border = ImageIO.read(new URL(borderLink.get()).openStream());
            var avatar = ImageIO.read(new URL(avatarLink).openStream());
            int heightAvatar = 125;
            int widthAvatar = 125;
            int heightBorder = border.getHeight();
            int widthBorder = border.getWidth();
            int offset = (widthBorder - widthAvatar) / 2;
            var bufferedImage = new BufferedImage(widthBorder, heightBorder, BufferedImage.TYPE_INT_RGB);
            var graphics = bufferedImage.getGraphics();
            graphics.drawImage(avatar, offset, offset, widthAvatar, heightAvatar, null);
            graphics.drawImage(border, 0, 0, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            return Optional.of(new ByteArrayInputStream(os.toByteArray()));
        } catch (Exception e) {
            logger.error("Error during creating avatar", e);
            return Optional.empty();
        }
    }
}
