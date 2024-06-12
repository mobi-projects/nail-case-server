package com.nailcase.model.entity;

import com.nailcase.common.Image;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "post_image")
@DiscriminatorValue("POST")
public class PostImage extends Image {
}
