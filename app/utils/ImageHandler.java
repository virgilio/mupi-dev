package utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageHandler {
	private final static int MEDIUM = 140;
	private final static int SMALL_PROFILE = 44;
	private final static int SMALL_INTEREST = 40;
	private final static int MEDIUM_PROMOTION = 360;

	public static boolean isWide(BufferedImage img){
		return img.getWidth() > img.getHeight();
	}

	public static BufferedImage createMediumPromotion(File file){
		BufferedImage img;
		try {
			img = ImageIO.read(file);
			if(isWide(img)){
				return scale(img, (MEDIUM_PROMOTION*img.getWidth())/img.getHeight(), MEDIUM_PROMOTION, img.getType());
			}
			else return scale(img, MEDIUM_PROMOTION, (MEDIUM_PROMOTION*img.getHeight())/img.getWidth(), img.getType());
		} catch (IOException e) {
			return null;		
		}		
	}

	public static BufferedImage createMediumSquare(File file){
		BufferedImage img;
		try {
			img = ImageIO.read(file);
			if(isWide(img)){
				return scale(img, (MEDIUM*img.getWidth())/img.getHeight(), MEDIUM, img.getType());
			}
			else return scale(img, MEDIUM, (MEDIUM*img.getHeight())/img.getWidth(), img.getType());
		} catch (IOException e) {
			return null;		
		}		
	}

	public static BufferedImage createSmallProfile(File file){
		BufferedImage img;
		try {
			img = ImageIO.read(file);
			if(isWide(img)){
				return scale(img, (SMALL_PROFILE*img.getWidth())/img.getHeight(), SMALL_PROFILE, img.getType());
			}
			else return scale(img, SMALL_PROFILE, (SMALL_PROFILE*img.getHeight())/img.getWidth(), img.getType());
		} catch (IOException e) {
			return null;		
		}		
	}

	public static BufferedImage createSmallInterest(File file){
		BufferedImage img;
		try {
			img = ImageIO.read(file);
			if(isWide(img)){
				return scale(img, (SMALL_INTEREST*img.getWidth())/img.getHeight(), SMALL_INTEREST, img.getType());
			}
			else return scale(img, SMALL_INTEREST, (SMALL_INTEREST*img.getHeight())/img.getWidth(), img.getType());
		} catch (IOException e) {
			return null;		
		}		
	}

	public static BufferedImage scale(BufferedImage bi, int width, int height, int type){
		BufferedImage scaled = new BufferedImage(width, height, type);
		Graphics2D g = scaled.createGraphics();
		if (type == BufferedImage.TYPE_INT_RGB) {
			g.setComposite(AlphaComposite.Src);
		}
		g.drawImage(bi, 0, 0, width, height, null); 
		g.dispose();
		return scaled;
	}

}
