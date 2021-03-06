package com.cursonelio.javaspringboot.cursoNelio.service;

import com.cursonelio.javaspringboot.cursoNelio.service.exception.FileException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageService {

    public BufferedImage getJpgImageFromFile(MultipartFile uploadFile) { // extrai a imagem
        String ext = FilenameUtils.getExtension(uploadFile.getOriginalFilename()); // pega a extensao do arquivo
        if (!"png".equals(ext) && !"jpg".equals(ext)){
            throw new FileException("Somente images PNG e JPG são permitidas");
        }

        try{
            BufferedImage img = ImageIO.read(uploadFile.getInputStream()); // obtendo um buffered img
            if ("png".equals(ext)){
                img = pngToJpg(img);
            }
            return img;
        }
        catch (IOException e){
            throw new FileException("Erro ao ler o arquivo");
        }
    }

    public BufferedImage pngToJpg(BufferedImage img) {
        BufferedImage jpgImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        jpgImage.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
        return jpgImage;
    }

    public InputStream getInputStrem (BufferedImage img, String extension){ //
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, extension, os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            throw new FileException("Erro ao ler arquivo");
        }
    }
}
