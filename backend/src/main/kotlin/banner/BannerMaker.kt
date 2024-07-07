package dev.triumphteam.backend.banner

import dev.triumphteam.website.splitSentence
import dev.triumphteam.website.trimAround
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


public class BannerMaker {

    private val template =
        requireNotNull(BannerMaker::class.java.classLoader.getResourceAsStream("board/board-template.png")) {
            "Could not find banner template!"
        }

    private val templateImage = ImageIO.read(template)
    private val fontRegular = resourceFont("board/poppins.ttf")
    private val fontBold = resourceFont("board/bold-poppins.ttf")

    public fun create(
        icon: BufferedImage,
        group: String,
        title: String?,
        subTitle: String?,
        output: File,
    ) {

        val image = templateImage.deepCopy()
        val graphics = image.createGraphics()

        graphics.apply {
            // Change the quality of the strings rendered
            setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)

            // First write the bold part
            font = fontBold.deriveFont(45f)
            title?.let { drawString(it.trimAround(contextLength = 20), 66, 429) }

            // Then the rest
            font = fontRegular.deriveFont(20f)
            drawString(group, 66, 359)
            subTitle?.let {
                var location = 479
                val lineSize = 50

                val lines = it.splitSentence(maxLength = 50)

                lines.forEach { line ->
                    drawString(line, 66, location)
                    location += lineSize
                }
            }

            // Then the icon
            drawImage(icon, 712, 170, null)

            // Finally dispose
            dispose()
        }

        ImageIO.write(image, "png", output)
    }

    private fun resourceFont(path: String): Font {
        val input = requireNotNull(BannerMaker::class.java.classLoader.getResourceAsStream(path)) {
            "Could not find font on path '$path'!"
        }

        return requireNotNull(Font.createFont(Font.PLAIN, input)) {
            "Could not create font on path '$path'!"
        }
    }

    private fun BufferedImage.deepCopy(): BufferedImage {
        val cm = colorModel
        val isAlphaPremultiplied = cm.isAlphaPremultiplied
        val raster = copyData(null)
        return BufferedImage(cm, raster, isAlphaPremultiplied, null)
    }
}
