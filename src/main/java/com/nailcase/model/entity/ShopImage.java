package com.nailcase.model.entity;

import com.nailcase.common.Image;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "shop_image")
@DiscriminatorValue("SHOP")
public class ShopImage extends Image {
}