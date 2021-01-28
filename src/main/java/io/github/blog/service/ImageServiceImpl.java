package io.github.blog.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.blog.entity.Image;
import io.github.blog.repository.ImageRepository;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

	@Override
	public Image save(Image image) {
		return imageRepository.save(image);
	}

	@Override
	public Optional<Image> findById(String id) {
		return imageRepository.findById(id);
	}
}
