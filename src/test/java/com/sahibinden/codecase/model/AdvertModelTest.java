package com.sahibinden.codecase.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class AdvertModelTest {

    private AdvertModel advertModel;

    @BeforeEach
    public void setUp() {
        advertModel = new AdvertModel();
        advertModel.setID(1);
        advertModel.setTitle("İlan Başlığı");
        advertModel.setDescription("İlan Açıklaması");
        advertModel.setCategory("Emlak");
        advertModel.setStatus("Onay Bekliyor");
    }

    @Test
    public void testGetters() {
        assertEquals(1, advertModel.getID());
        assertEquals("İlan Başlığı", advertModel.getTitle());
        assertEquals("İlan Açıklaması", advertModel.getDescription());
        assertEquals("Emlak", advertModel.getCategory());
        assertEquals("Onay Bekliyor", advertModel.getStatus());
    }

    @Test
    public void testSetters() {
        advertModel.setID(2);
        advertModel.setTitle("Yeni Başlık");
        advertModel.setDescription("Yeni Açıklama");
        advertModel.setCategory("Vasıta");
        advertModel.setStatus("Aktif");

        assertEquals(2, advertModel.getID());
        assertEquals("Yeni Başlık", advertModel.getTitle());
        assertEquals("Yeni Açıklama", advertModel.getDescription());
        assertEquals("Vasıta", advertModel.getCategory());
        assertEquals("Aktif", advertModel.getStatus());
    }

    @Test
    public void testToString() {
        String expectedToString = "AdvertModel(ID=1, title=İlan Başlığı, description=İlan Açıklaması, category=Emlak, status=Onay Bekliyor)";
        assertEquals(expectedToString, advertModel.toString());
    }

    @Test
    public void testEqualsAndHashCode() {
        AdvertModel anotherModel = new AdvertModel();
        anotherModel.setID(1);
        anotherModel.setTitle("İlan Başlığı");
        anotherModel.setDescription("İlan Açıklaması");
        anotherModel.setCategory("Emlak");
        anotherModel.setStatus("Onay Bekliyor");

        assertEquals(advertModel, anotherModel);
        assertEquals(advertModel.hashCode(), anotherModel.hashCode());

        anotherModel.setID(2);
        assertNotEquals(advertModel, anotherModel);
        assertNotEquals(advertModel.hashCode(), anotherModel.hashCode());
    }

    @Test
    public void testAllArgsConstructor() {
        AdvertModel anotherModel = new AdvertModel(2, "Başlık", "Açıklama", "Kategori", "Durum");
        assertEquals(2, anotherModel.getID());
        assertEquals("Başlık", anotherModel.getTitle());
        assertEquals("Açıklama", anotherModel.getDescription());
        assertEquals("Kategori", anotherModel.getCategory());
        assertEquals("Durum", anotherModel.getStatus());
    }
}
